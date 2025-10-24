package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String streetAddress;
    private String ward;
    private String province;
    private String address; // Full address: streetAddress, ward, province
    private boolean isDefault;
}
