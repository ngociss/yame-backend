package vn.yame.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
public class AddressRequest {
    private String city;
    private String district;
    private String streetAddress;
    private String recipientName;
    private String phoneNumber;
    private Long userId;
}
