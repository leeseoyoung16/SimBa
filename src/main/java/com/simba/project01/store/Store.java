package com.simba.project01.store;

import com.simba.project01.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "store",
        uniqueConstraints = @UniqueConstraint(columnNames = {"latitude","longitude"})
)
public class Store
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable=false, precision=9, scale=6)
    private BigDecimal latitude; //위도

    @Column(nullable=false, precision=9, scale=6)
    private BigDecimal longitude; //경도

    @Column(nullable = false)
    private StoreCategory category;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
