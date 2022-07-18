package phone.phoneorderservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import phone.phoneorderservice.domain.Customer;
import phone.phoneorderservice.repositories.CustomerRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PhoneOrderBootStrap implements CommandLineRunner {
    public static final String DEMO_ROOM = "Demo Room";
    public static final String PHONE_1_IMEI = "338694371652036";
    public static final String PHONE_2_IMEI = "334015010517204";
    public static final String PHONE_3_IMEI = "863571738204276";

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCustomerData();
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer savedCustomer = customerRepository.save(Customer.builder()
                    .customerName(DEMO_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());

            log.debug("Demo Room Customer Id: " + savedCustomer.getId().toString());
        }
    }
}
