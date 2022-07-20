

package phone.model;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PhoneOrderPagedList extends PageImpl<PhoneOrderDto> {
    public PhoneOrderPagedList(List<PhoneOrderDto> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PhoneOrderPagedList(List<PhoneOrderDto> content) {
        super(content);
    }
}
