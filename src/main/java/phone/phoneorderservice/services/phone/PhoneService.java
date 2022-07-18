package phone.phoneorderservice.services.phone;

import phone.phoneorderservice.web.model.PhoneDto;

import java.util.Optional;
import java.util.UUID;

public interface PhoneService {

    Optional<PhoneDto> getPhoneById(UUID uuid);

    Optional<PhoneDto> getPhoneByImei(String imei);
}
