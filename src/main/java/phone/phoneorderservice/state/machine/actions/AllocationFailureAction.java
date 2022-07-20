package phone.phoneorderservice.state.machine.actions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import phone.model.events.AllocationFailureEvent;
import phone.phoneorderservice.config.JmsConfig;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.services.PhoneOrderManagerImpl;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationFailureAction implements Action<PhoneOrderStatusEnum, PhoneOrderEventEnum> {

    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<PhoneOrderStatusEnum, PhoneOrderEventEnum> context) {
        String phoneOrderId = (String) context.getMessage().getHeaders().get(PhoneOrderManagerImpl.ORDER_ID_HEADER);

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_FAILURE_QUEUE, AllocationFailureEvent.builder()
                .orderId(UUID.fromString(phoneOrderId))
                .build());

        log.debug("Sent Allocation Failure Message to queue for order id " + phoneOrderId);
    }
}
