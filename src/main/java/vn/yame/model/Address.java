package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseEntity {
    private String recipientName;
    private String phoneNumber;
    private String streetAddress;
    private String ward;
    private String province;

    @Column(name = "is_default")
    private boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
