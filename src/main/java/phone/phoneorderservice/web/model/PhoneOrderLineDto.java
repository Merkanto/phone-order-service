
package phone.phoneorderservice.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PhoneOrderLineDto extends BaseItem {

    @Builder
    public PhoneOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                             String imei, String phoneName, UUID phoneId, Integer orderQuantity) {
        super(id, version, createdDate, lastModifiedDate);
        this.imei = imei;
        this.phoneName = phoneName;
        this.phoneId = phoneId;
        this.orderQuantity = orderQuantity;
    }

    private String imei;
    private String phoneName;
    private UUID phoneId;
    private Integer orderQuantity = 0;
}
