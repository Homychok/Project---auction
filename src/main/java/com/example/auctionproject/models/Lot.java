package com.example.auctionproject.models;

import com.example.auctionproject.enums.LotStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LotStatus status;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "start_price")
    private Long startPrice;
    @Column(name = "bid_price")
    private Long bidPrice;
    private Long currentPrice;
    @OneToMany(mappedBy = "lot")
    private List<Bid> bids;
}
