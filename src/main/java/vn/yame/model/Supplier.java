package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier extends BaseEntity {
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String address;

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;
}
