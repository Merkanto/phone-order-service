package phone.phoneorderservice.services.phone;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import phone.model.PhoneDto;

import java.util.Optional;
import java.util.UUID;

@ConfigurationProperties(prefix = "factory.phone", ignoreUnknownFields = false)
@Service
public class PhoneServiceImpl implements PhoneService {

    public final String PHONE_PATH_V1 = "/api/v1/phone/";
    public final String PHONE_IMEI_PATH_V1 = "/api/v1/phoneImei/";
    private final RestTemplate restTemplate;

    private String phoneServiceHost;

    public PhoneServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<PhoneDto> getPhoneById(UUID uuid){
        return Optional.of(restTemplate.getForObject(phoneServiceHost + PHONE_PATH_V1 + uuid.toString(), PhoneDto.class));
    }

    @Override
    public Optional<PhoneDto> getPhoneByImei(String imei) {
        return Optional.of(restTemplate.getForObject(phoneServiceHost + PHONE_IMEI_PATH_V1 + imei, PhoneDto.class));
    }

    public void setPhoneServiceHost(String phoneServiceHost) {
        this.phoneServiceHost = phoneServiceHost;
    }
}
