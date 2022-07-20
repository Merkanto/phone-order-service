

package phone.phoneorderservice.services;


import org.springframework.data.domain.Pageable;
import phone.model.PhoneOrderDto;
import phone.model.PhoneOrderPagedList;

import java.util.UUID;

public interface PhoneOrderService {
    PhoneOrderPagedList listOrders(UUID customerId, Pageable pageable);

    PhoneOrderDto placeOrder(UUID customerId, PhoneOrderDto phoneOrderDto);

    PhoneOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);
}
