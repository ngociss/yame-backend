package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.ReviewStatus;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends BaseEntity {
    private int rating; // e.g., 1 to 5
    private String comment;
    private String title;
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
