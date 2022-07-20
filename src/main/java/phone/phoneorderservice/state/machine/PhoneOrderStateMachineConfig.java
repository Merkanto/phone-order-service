package phone.phoneorderservice.state.machine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class PhoneOrderStateMachineConfig extends StateMachineConfigurerAdapter<PhoneOrderStatusEnum, PhoneOrderEventEnum> {


    @Override
    public void configure(StateMachineStateConfigurer<PhoneOrderStatusEnum, PhoneOrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(PhoneOrderStatusEnum.NEW)
                .states(EnumSet.allOf(PhoneOrderStatusEnum.class))
                .end(PhoneOrderStatusEnum.PICKED_UP)
                .end(PhoneOrderStatusEnum.DELIVERED)
                .end(PhoneOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(PhoneOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(PhoneOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PhoneOrderStatusEnum, PhoneOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                    .source(PhoneOrderStatusEnum.NEW).target(PhoneOrderStatusEnum.VALIDATION_PENDING)
                    .event(PhoneOrderEventEnum.VALIDATE_ORDER)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.NEW).target(PhoneOrderStatusEnum.VALIDATED)
                    .event(PhoneOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                    .source(PhoneOrderStatusEnum.NEW).target(PhoneOrderStatusEnum.VALIDATED)
                    .event(PhoneOrderEventEnum.VALIDATION_FAILED);
    }
}
