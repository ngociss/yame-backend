package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long id;
    private String recipientName;
    private String phoneNumber;
    private String address;
    private boolean isDefault;
}
