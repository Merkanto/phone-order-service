package phone.phoneorderservice.web.mappers;

import org.mapstruct.Mapper;
import phone.model.CustomerDto;
import phone.phoneorderservice.domain.Customer;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {
    CustomerDto customerToDto(Customer customer);

    Customer dtoToCustomer(Customer dto);
}
