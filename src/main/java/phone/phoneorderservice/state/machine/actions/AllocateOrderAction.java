package phone.phoneorderservice.state.machine.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import phone.model.events.AllocateOrderRequest;
import phone.phoneorderservice.config.JmsConfig;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.repositories.PhoneOrderRepository;
import phone.phoneorderservice.services.PhoneOrderManagerImpl;
import phone.phoneorderservice.web.mappers.PhoneOrderMapper;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<PhoneOrderStatusEnum, PhoneOrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final PhoneOrderRepository phoneOrderRepository;
    private final PhoneOrderMapper phoneOrderMapper;

    public void execute(StateContext<PhoneOrderStatusEnum, PhoneOrderEventEnum> context) {
        String phoneOrderId = (String) context.getMessage().getHeaders().get(PhoneOrderManagerImpl.ORDER_ID_HEADER);
        Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(UUID.fromString(phoneOrderId));

        phoneOrderOptional.ifPresentOrElse(phoneOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE,
                    AllocateOrderRequest.builder()
                            .phoneOrderDto(phoneOrderMapper.phoneOrderToDto(phoneOrder))
                            .build());
            log.debug("Sent Allocation Request for order id: " + phoneOrderId);
        }, () -> log.error("Phone Order Not Found!"));
    }
}