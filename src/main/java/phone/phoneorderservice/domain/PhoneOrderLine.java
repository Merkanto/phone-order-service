
package phone.phoneorderservice.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class PhoneOrderLine extends BaseEntity {

    @Builder
    public PhoneOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                          PhoneOrder phoneOrder, UUID phoneId, Integer orderQuantity,
                          Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.phoneOrder = phoneOrder;
        this.phoneId = phoneId;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }

    @ManyToOne
    private PhoneOrder phoneOrder;

    private UUID phoneId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;
}
