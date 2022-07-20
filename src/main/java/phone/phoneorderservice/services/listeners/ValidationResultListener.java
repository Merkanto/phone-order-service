package phone.phoneorderservice.services.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import phone.model.events.ValidateOrderResult;
import phone.phoneorderservice.config.JmsConfig;
import phone.phoneorderservice.services.PhoneOrderManager;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationResultListener {

    private final PhoneOrderManager phoneOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result){
        final UUID phoneOrderId = result.getOrderId();

        log.debug("Validation Result for Order Id: " + phoneOrderId);

        phoneOrderManager.processValidationResult(phoneOrderId, result.getIsValid());
    }
}
