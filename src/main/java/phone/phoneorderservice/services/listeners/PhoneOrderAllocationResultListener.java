package phone.phoneorderservice.services.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import phone.model.events.AllocateOrderResult;
import phone.phoneorderservice.config.JmsConfig;
import phone.phoneorderservice.services.PhoneOrderManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class PhoneOrderAllocationResultListener {
    private final PhoneOrderManager phoneOrderManager;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result){
        if(!result.getAllocationError() && !result.getPendingInventory()){
            //allocated normally
            phoneOrderManager.phoneOrderAllocationPassed(result.getPhoneOrderDto());
        } else if(!result.getAllocationError() && result.getPendingInventory()) {
            //pending inventory
            phoneOrderManager.phoneOrderAllocationPendingInventory(result.getPhoneOrderDto());
        } else if(result.getAllocationError()){
            //allocation error
            phoneOrderManager.phoneOrderAllocationFailed(result.getPhoneOrderDto());
        }
    }
}
