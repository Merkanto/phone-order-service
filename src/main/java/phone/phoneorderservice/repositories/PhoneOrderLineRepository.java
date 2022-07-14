

package phone.phoneorderservice.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import phone.phoneorderservice.domain.PhoneOrderLine;

import java.util.UUID;

public interface PhoneOrderLineRepository extends PagingAndSortingRepository<PhoneOrderLine, UUID> {
}
