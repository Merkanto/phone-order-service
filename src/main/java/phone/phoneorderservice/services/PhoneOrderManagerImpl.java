package phone.phoneorderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.model.PhoneOrderDto;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.repositories.PhoneOrderRepository;
import phone.phoneorderservice.state.machine.PhoneOrderStateChangeInterceptor;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhoneOrderManagerImpl implements PhoneOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID_HEADER";

    private final StateMachineFactory<PhoneOrderStatusEnum, PhoneOrderEventEnum> stateMachineFactory;
    private final PhoneOrderRepository phoneOrderRepository;
    private final PhoneOrderStateChangeInterceptor phoneOrderStateChangeInterceptor;

    @Transactional
    @Override
    public PhoneOrder newPhoneOrder(PhoneOrder phoneOrder) {
        phoneOrder.setId(null);
        phoneOrder.setOrderStatus(PhoneOrderStatusEnum.NEW);

        PhoneOrder savedPhoneOrder = phoneOrderRepository.saveAndFlush(phoneOrder);
        sendPhoneOrderEvent(savedPhoneOrder, PhoneOrderEventEnum.VALIDATE_ORDER);
        return savedPhoneOrder;
    }

    @Transactional
    @Override
    public void processValidationResult(UUID phoneOrderId, Boolean isValid) {
        log.debug("Process Validation Result for phoneOrderId: " + phoneOrderId + " Valid? " + isValid);

        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(phoneOrderId);

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            if(isValid){
                sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.VALIDATION_PASSED);

                //wait for status change
                awaitForStatus(phoneOrderId, PhoneOrderStatusEnum.VALIDATED);

                PhoneOrder validatedOrder = phoneOrderRepository.findById(phoneOrderId).get();

                sendPhoneOrderEvent(validatedOrder, PhoneOrderEventEnum.ALLOCATE_ORDER);

            } else {
                sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.VALIDATION_FAILED);
            }
        }, () -> log.error("Order Not Found. Id: " + phoneOrderId));
    }

    @Override
    public void phoneOrderAllocationPassed(PhoneOrderDto phoneOrderDto) {
        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(phoneOrderDto.getId());

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.ALLOCATION_SUCCESS);
            awaitForStatus(phoneOrder.getId(), PhoneOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(phoneOrderDto);
        }, () -> log.error("Order Id Not Found: " + phoneOrderDto.getId() ));
    }

    @Override
    public void phoneOrderAllocationPendingInventory(PhoneOrderDto phoneOrderDto) {
        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(phoneOrderDto.getId());

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.ALLOCATION_NO_INVENTORY);
            awaitForStatus(phoneOrder.getId(), PhoneOrderStatusEnum.PENDING_INVENTORY);
            updateAllocatedQty(phoneOrderDto);
        }, () -> log.error("Order Id Not Found: " + phoneOrderDto.getId() ));

    }

    private void updateAllocatedQty(PhoneOrderDto phoneOrderDto) {
        Optional<PhoneOrder> allocatedOrderOptional = phoneOrderRepository.findById(phoneOrderDto.getId());

        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getPhoneOrderLines().forEach(phoneOrderLine -> {
                phoneOrderDto.getPhoneOrderLines().forEach(phoneOrderLineDto -> {
                    if(phoneOrderLine.getId().equals(phoneOrderLineDto.getId())){
                        phoneOrderLine.setQuantityAllocated(phoneOrderLineDto.getQuantityAllocated());
                    }
                });
            });

            phoneOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + phoneOrderDto.getId()));
    }

    @Override
    public void phoneOrderAllocationFailed(PhoneOrderDto phoneOrderDto) {
        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(phoneOrderDto.getId());

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Order Not Found. Id: " + phoneOrderDto.getId()) );

    }

    @Override
    public void phoneOrderPickedUp(UUID id) {
        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(id);

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            //do process
            sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.PHONEORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    @Override
    public void cancelOrder(UUID id) {
        phoneOrderRepository.findById(id).ifPresentOrElse(phoneOrder -> {
            sendPhoneOrderEvent(phoneOrder, PhoneOrderEventEnum.CANCEL_ORDER);
        }, () -> log.error("Order Not Found. Id: " + id));
    }

    private void sendPhoneOrderEvent(PhoneOrder phoneOrder, PhoneOrderEventEnum eventEnum){
        StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> sm = build(phoneOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                .setHeader(ORDER_ID_HEADER, phoneOrder.getId().toString())
                .build();

        sm.sendEvent(msg);
    }

    private void awaitForStatus(UUID phoneOrderId, PhoneOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            if (loopCount.incrementAndGet() > 10) {
                found.set(true);
                log.debug("Loop Retries exceeded");
            }

            phoneOrderRepository.findById(phoneOrderId).ifPresentOrElse(phoneOrder -> {
                if (phoneOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + phoneOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }

    private StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> build(PhoneOrder phoneOrder){
        StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> sm = stateMachineFactory.getStateMachine(phoneOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(phoneOrderStateChangeInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(phoneOrder.getOrderStatus(), null, null, null));
                });

        sm.start();

        return sm;
    }
}
