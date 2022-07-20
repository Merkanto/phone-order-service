package phone.phoneorderservice.state.machine.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.services.PhoneOrderManagerImpl;

@Slf4j
@Component
public class ValidationFailureAction implements Action<PhoneOrderStatusEnum, PhoneOrderEventEnum> {

    @Override
    public void execute(StateContext<PhoneOrderStatusEnum, PhoneOrderEventEnum> context) {
        String phoneOrderId = (String) context.getMessage().getHeaders().get(PhoneOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compensating Transaction.... Validation Failed: " + phoneOrderId);
    }
}
