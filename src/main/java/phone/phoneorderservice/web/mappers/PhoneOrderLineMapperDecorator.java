package phone.phoneorderservice.web.mappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import phone.phoneorderservice.domain.PhoneOrderLine;
import phone.phoneorderservice.services.phone.PhoneService;
import phone.model.PhoneDto;
import phone.model.PhoneOrderLineDto;

import java.util.Optional;

public abstract class PhoneOrderLineMapperDecorator implements PhoneOrderLineMapper {
    private PhoneService phoneService;
    private PhoneOrderLineMapper phoneOrderLineMapper;

    @Autowired
    public void setPhoneService(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setPhoneOrderLineMapper(PhoneOrderLineMapper phoneOrderLineMapper) {
        this.phoneOrderLineMapper = phoneOrderLineMapper;
    }

    @Override
    public PhoneOrderLineDto phoneOrderLineToDto(PhoneOrderLine line) {
        PhoneOrderLineDto orderLineDto = phoneOrderLineMapper.phoneOrderLineToDto(line);
        Optional<PhoneDto> phoneDtoOptional = phoneService.getPhoneByImei(line.getImei());

        phoneDtoOptional.ifPresent(phoneDto -> {
            orderLineDto.setPhoneName(phoneDto.getPhoneName());
            orderLineDto.setPhoneStyle(phoneDto.getPhoneStyle());
            orderLineDto.setPrice(phoneDto.getPrice());
            orderLineDto.setPhoneId(phoneDto.getId());
        });

        return orderLineDto;
    }
}
