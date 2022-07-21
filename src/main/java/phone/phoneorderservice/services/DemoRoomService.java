package phone.phoneorderservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.phoneorderservice.bootstrap.PhoneOrderBootStrap;
import phone.phoneorderservice.domain.Customer;
import phone.phoneorderservice.repositories.CustomerRepository;
import phone.phoneorderservice.repositories.PhoneOrderRepository;
import phone.model.PhoneOrderDto;
import phone.model.PhoneOrderLineDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class DemoRoomService {

    private final CustomerRepository customerRepository;
    private final PhoneOrderService phoneOrderService;
    private final PhoneOrderRepository phoneOrderRepository;
    private final List<String> phoneIMEIs = new ArrayList<>(3);

    public DemoRoomService(CustomerRepository customerRepository, PhoneOrderService phoneOrderService,
                           PhoneOrderRepository phoneOrderRepository) {
        this.customerRepository = customerRepository;
        this.phoneOrderService = phoneOrderService;
        this.phoneOrderRepository = phoneOrderRepository;

        phoneIMEIs.add(PhoneOrderBootStrap.PHONE_1_IMEI);
        phoneIMEIs.add(PhoneOrderBootStrap.PHONE_2_IMEI);
        phoneIMEIs.add(PhoneOrderBootStrap.PHONE_3_IMEI);
    }

    @Transactional
    @Scheduled(fixedRate = 2000) //run every 2 seconds
    public void placeDemoRoomOrder() {

        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(PhoneOrderBootStrap.DEMO_ROOM);

        if (customerList.size() == 1) { //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few demo room customers found");

            customerList.forEach(customer -> log.debug(customer.toString()));
        }
    }

    private void doPlaceOrder(Customer customer) {
        String phoneToOrder = getRandomPhoneIMEI();

        PhoneOrderLineDto phoneOrderLine = PhoneOrderLineDto.builder()
                .imei(phoneToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<PhoneOrderLineDto> phoneOrderLineSet = new ArrayList<>();
        phoneOrderLineSet.add(phoneOrderLine);

        PhoneOrderDto phoneOrder = PhoneOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .phoneOrderLines(phoneOrderLineSet)
                .build();

        PhoneOrderDto savedOrder = phoneOrderService.placeOrder(customer.getId(), phoneOrder);

    }

    private String getRandomPhoneIMEI() {
        return phoneIMEIs.get(new Random().nextInt(phoneIMEIs.size() - 0));
    }
}
