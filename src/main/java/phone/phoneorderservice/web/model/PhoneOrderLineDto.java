
package phone.phoneorderservice.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PhoneOrderLineDto extends BaseItem {

    @Builder
    public PhoneOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                            String imei, String phoneName,String phoneStyle, UUID phoneId, Integer orderQuantity, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.imei = imei;
        this.phoneName = phoneName;
        this.phoneStyle = phoneStyle;
        this.phoneId = phoneId;
        this.orderQuantity = orderQuantity;
        this.price = price;
    }

    private String imei;
    private String phoneName;
    private String phoneStyle;
    private UUID phoneId;
    private Integer orderQuantity = 0;
    private BigDecimal price;
}
