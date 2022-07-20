package phone.phoneorderservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.repositories.PhoneOrderRepository;

@RequiredArgsConstructor
@Service
public class PhoneOrderManagerImpl implements PhoneOrderManager {

    private final StateMachineFactory<PhoneOrderStatusEnum, PhoneOrderEventEnum> stateMachineFactory;
    private final PhoneOrderRepository phoneOrderRepository;

    @Transactional
    @Override
    public PhoneOrder newPhoneOrder(PhoneOrder phoneOrder) {
        phoneOrder.setId(null);
        phoneOrder.setOrderStatus(PhoneOrderStatusEnum.NEW);
        PhoneOrder savedPhoneOrder = phoneOrderRepository.save(phoneOrder);
        sendPhoneOrderEvent(savedPhoneOrder, PhoneOrderEventEnum.VALIDATE_ORDER);

        return savedPhoneOrder;
    }

    private void sendPhoneOrderEvent(PhoneOrder phoneOrder, PhoneOrderEventEnum eventEnum) {
        StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> sm = build(phoneOrder);
        Message msg = MessageBuilder.withPayload(eventEnum).build();

        sm.sendEvent(msg);
    }

    private StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> build(PhoneOrder phoneOrder) {
        StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> sm = stateMachineFactory.getStateMachine(phoneOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.resetStateMachine(new DefaultStateMachineContext<>(phoneOrder.getOrderStatus(), null, null, null));
                });
        sm.start();

        return sm;
    }
}
