package phone.phoneorderservice.state.machine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.domain.PhoneOrderEventEnum;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.repositories.PhoneOrderRepository;
import phone.phoneorderservice.services.PhoneOrderManagerImpl;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhoneOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<PhoneOrderStatusEnum, PhoneOrderEventEnum> {

    private final PhoneOrderRepository phoneOrderRepository;

    @Transactional
    public void preStateChange(State<PhoneOrderStatusEnum, PhoneOrderEventEnum> state, Message<PhoneOrderEventEnum> message,
                               Transition<PhoneOrderStatusEnum, PhoneOrderEventEnum> transition,
                               StateMachine<PhoneOrderStatusEnum, PhoneOrderEventEnum> stateMachine) {
        log.debug("Pre-State Change");

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(PhoneOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

                    PhoneOrder phoneOrder = phoneOrderRepository.getOne(UUID.fromString(orderId));
                    phoneOrder.setOrderStatus(state.getId());
                    phoneOrderRepository.saveAndFlush(phoneOrder);
                });
    }
}
