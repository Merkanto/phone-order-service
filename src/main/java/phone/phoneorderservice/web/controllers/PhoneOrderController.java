
package phone.phoneorderservice.web.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import phone.phoneorderservice.services.PhoneOrderService;
import phone.model.PhoneOrderDto;
import phone.model.PhoneOrderPagedList;

import java.util.UUID;

@RequestMapping("/api/v1/customers/{customerId}/")
@RestController
public class PhoneOrderController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final PhoneOrderService phoneOrderService;

    public PhoneOrderController(PhoneOrderService phoneOrderService) {
        this.phoneOrderService = phoneOrderService;
    }

    @GetMapping("orders")
    public PhoneOrderPagedList listOrders(@PathVariable("customerId") UUID customerId,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return phoneOrderService.listOrders(customerId, PageRequest.of(pageNumber, pageSize));
    }

    @PostMapping("orders")
    @ResponseStatus(HttpStatus.CREATED)
    public PhoneOrderDto placeOrder(@PathVariable("customerId") UUID customerId, @RequestBody PhoneOrderDto phoneOrderDto) {
        return phoneOrderService.placeOrder(customerId, phoneOrderDto);
    }

    @GetMapping("orders/{orderId}")
    public PhoneOrderDto getOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        return phoneOrderService.getOrderById(customerId, orderId);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable("customerId") UUID customerId, @PathVariable("orderId") UUID orderId) {
        phoneOrderService.pickupOrder(customerId, orderId);
    }
}
