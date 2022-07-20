package phone.phoneorderservice.services;

import phone.model.PhoneOrderDto;
import phone.phoneorderservice.domain.PhoneOrder;

import java.util.UUID;

public interface PhoneOrderManager {

    PhoneOrder newPhoneOrder(PhoneOrder phoneOrder);

    void processValidationResult(UUID phoneOrderId, Boolean isValid);

    void phoneOrderAllocationPassed(PhoneOrderDto phoneOrderDto);

    void phoneOrderAllocationPendingInventory(PhoneOrderDto phoneOrderDto);

    void phoneOrderAllocationFailed(PhoneOrderDto phoneOrderDto);

    void phoneOrderPickedUp(UUID id);

    void cancelOrder(UUID id);
}
