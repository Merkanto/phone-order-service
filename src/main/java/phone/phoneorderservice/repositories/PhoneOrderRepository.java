
package phone.phoneorderservice.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import phone.phoneorderservice.domain.Customer;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.domain.PhoneOrder;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface PhoneOrderRepository extends JpaRepository<PhoneOrder, UUID> {

    Page<PhoneOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<PhoneOrder> findAllByOrderStatus(PhoneOrderStatusEnum phoneOrderStatusEnum);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    PhoneOrder findOneById(UUID id);
}
