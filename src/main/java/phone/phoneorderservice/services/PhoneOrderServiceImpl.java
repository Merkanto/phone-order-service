

package phone.phoneorderservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.phoneorderservice.domain.Customer;
import phone.phoneorderservice.domain.PhoneOrderStatusEnum;
import phone.phoneorderservice.domain.PhoneOrder;
import phone.phoneorderservice.repositories.CustomerRepository;
import phone.phoneorderservice.repositories.PhoneOrderRepository;
import phone.phoneorderservice.web.mappers.PhoneOrderMapper;
import phone.phoneorderservice.web.model.PhoneOrderDto;
import phone.phoneorderservice.web.model.PhoneOrderPagedList;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PhoneOrderServiceImpl implements PhoneOrderService {

    private final PhoneOrderRepository phoneOrderRepository;
    private final CustomerRepository customerRepository;
    private final PhoneOrderMapper phoneOrderMapper;
    private final ApplicationEventPublisher publisher;

    public PhoneOrderServiceImpl(PhoneOrderRepository phoneOrderRepository,
                                 CustomerRepository customerRepository,
                                 PhoneOrderMapper phoneOrderMapper, ApplicationEventPublisher publisher) {
        this.phoneOrderRepository = phoneOrderRepository;
        this.customerRepository = customerRepository;
        this.phoneOrderMapper = phoneOrderMapper;
        this.publisher = publisher;
    }

    @Override
    public PhoneOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<PhoneOrder> phoneOrderPage =
                    phoneOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new PhoneOrderPagedList(phoneOrderPage
                    .stream()
                    .map(phoneOrderMapper::phoneOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    phoneOrderPage.getPageable().getPageNumber(),
                    phoneOrderPage.getPageable().getPageSize()),
                    phoneOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public PhoneOrderDto placeOrder(UUID customerId, PhoneOrderDto phoneOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            PhoneOrder phoneOrder = phoneOrderMapper.dtoToPhoneOrder(phoneOrderDto);
            phoneOrder.setId(null); //should not be set by outside client
            phoneOrder.setCustomer(customerOptional.get());
            phoneOrder.setOrderStatus(PhoneOrderStatusEnum.NEW);

            phoneOrder.getPhoneOrderLines().forEach(line -> line.setPhoneOrder(phoneOrder));

            PhoneOrder savedPhoneOrder = phoneOrderRepository.saveAndFlush(phoneOrder);

            log.debug("Saved Phone Order: " + phoneOrder.getId());

            //todo impl
            //  publisher.publishEvent(new NewPhoneOrderEvent(savedPhoneOrder));

            return phoneOrderMapper.phoneOrderToDto(savedPhoneOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public PhoneOrderDto getOrderById(UUID customerId, UUID orderId) {
        return phoneOrderMapper.phoneOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        PhoneOrder phoneOrder = getOrder(customerId, orderId);
        phoneOrder.setOrderStatus(PhoneOrderStatusEnum.PICKED_UP);

        phoneOrderRepository.save(phoneOrder);
    }

    private PhoneOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Optional<PhoneOrder> phoneOrderOptional = phoneOrderRepository.findById(orderId);

            if (phoneOrderOptional.isPresent()) {
                PhoneOrder phoneOrder = phoneOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if (phoneOrder.getCustomer().getId().equals(customerId)) {
                    return phoneOrder;
                }
            }
            throw new RuntimeException("Phone Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
