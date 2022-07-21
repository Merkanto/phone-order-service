package phone.phoneorderservice.services;

import org.springframework.data.domain.Pageable;
import phone.model.CustomerPagedList;

public interface CustomerService {

    CustomerPagedList listCustomers(Pageable pageable);

}
