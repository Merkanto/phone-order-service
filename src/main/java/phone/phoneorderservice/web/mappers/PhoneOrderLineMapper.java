package phone.phoneorderservice.web.mappers;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import phone.phoneorderservice.domain.PhoneOrderLine;
import phone.phoneorderservice.web.model.PhoneOrderLineDto;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(PhoneOrderLineMapperDecorator.class)
public interface PhoneOrderLineMapper {
    PhoneOrderLineDto phoneOrderLineToDto(PhoneOrderLine line);

    PhoneOrderLine dtoToPhoneOrderLine(PhoneOrderLineDto dto);
}
