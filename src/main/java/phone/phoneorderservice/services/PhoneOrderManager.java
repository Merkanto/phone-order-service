package phone.phoneorderservice.services;

import phone.phoneorderservice.domain.PhoneOrder;

public interface PhoneOrderManager {

    PhoneOrder newPhoneOrder(PhoneOrder phoneOrder);
}
