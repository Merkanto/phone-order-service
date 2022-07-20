package phone.phoneorderservice.state.machine;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;

import java.util.EnumSet;

@RequiredArgsConstructor
@Configuration
@EnableStateMachineFactory
public class PhoneOrderStateMachineConfig extends StateMachineConfigurerAdapter<PhoneOrderStatusEnum, PhoneOrderEventEnum> {

    private final Action<PhoneOrderStatusEnum, PhoneOrderEventEnum> validateOrderAction;
    private final Action<PhoneOrderStatusEnum, PhoneOrderEventEnum>  allocateOrderAction;
    private final Action<PhoneOrderStatusEnum, PhoneOrderEventEnum>  validationFailureAction;
    private final Action<PhoneOrderStatusEnum, PhoneOrderEventEnum>  allocationFailureAction;
    private final Action<PhoneOrderStatusEnum, PhoneOrderEventEnum>  deallocateOrderAction;


    @Override
    public void configure(StateMachineStateConfigurer<PhoneOrderStatusEnum, PhoneOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(PhoneOrderStatusEnum.NEW)
                .states(EnumSet.allOf(PhoneOrderStatusEnum.class))
                .end(PhoneOrderStatusEnum.PICKED_UP)
                .end(PhoneOrderStatusEnum.DELIVERED)
                .end(PhoneOrderStatusEnum.CANCELLED)
                .end(PhoneOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(PhoneOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(PhoneOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PhoneOrderStatusEnum, PhoneOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                    .source(PhoneOrderStatusEnum.NEW).target(PhoneOrderStatusEnum.VALIDATION_PENDING)
                    .event(PhoneOrderEventEnum.VALIDATE_ORDER)
                    .action(validateOrderAction)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.VALIDATION_PENDING).target(PhoneOrderStatusEnum.VALIDATED)
                    .event(PhoneOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.VALIDATION_PENDING).target(PhoneOrderStatusEnum.CANCELLED)
                    .event(PhoneOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.VALIDATION_PENDING).target(PhoneOrderStatusEnum.VALIDATION_EXCEPTION)
                    .event(PhoneOrderEventEnum.VALIDATION_FAILED)
                    .action(validationFailureAction)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.VALIDATED).target(PhoneOrderStatusEnum.ALLOCATION_PENDING)
                    .event(PhoneOrderEventEnum.ALLOCATE_ORDER)
                    .action(allocateOrderAction)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.VALIDATED).target(PhoneOrderStatusEnum.CANCELLED)
                    .event(PhoneOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATION_PENDING).target(PhoneOrderStatusEnum.ALLOCATED)
                    .event(PhoneOrderEventEnum.ALLOCATION_SUCCESS)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATION_PENDING).target(PhoneOrderStatusEnum.ALLOCATION_EXCEPTION)
                    .event(PhoneOrderEventEnum.ALLOCATION_FAILED)
                    .action(allocationFailureAction)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATION_PENDING).target(PhoneOrderStatusEnum.CANCELLED)
                    .event(PhoneOrderEventEnum.CANCEL_ORDER)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATION_PENDING).target(PhoneOrderStatusEnum.PENDING_INVENTORY)
                    .event(PhoneOrderEventEnum.ALLOCATION_NO_INVENTORY)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATED).target(PhoneOrderStatusEnum.PICKED_UP)
                    .event(PhoneOrderEventEnum.PHONEORDER_PICKED_UP)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.ALLOCATED).target(PhoneOrderStatusEnum.CANCELLED)
                    .event(PhoneOrderEventEnum.CANCEL_ORDER)
                    .action(deallocateOrderAction);
    }
}
