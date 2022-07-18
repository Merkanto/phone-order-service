
package phone.phoneorderservice.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.web.model.PhoneOrderDto;

@Mapper(uses = {DateMapper.class, PhoneOrderLineMapper.class})
public interface PhoneOrderMapper {

    @Mapping(target = "customerId", source = "customer.id")
    PhoneOrderDto phoneOrderToDto(PhoneOrder phoneOrder);

    PhoneOrder dtoToPhoneOrder(PhoneOrderDto dto);
}
