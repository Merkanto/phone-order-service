package phone.phoneorderservice.state.machine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
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
}
