package vn.yame.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UserUpdateRequest extends UserRequest implements Serializable {

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;

    private Set<Long> roleIds;
}
