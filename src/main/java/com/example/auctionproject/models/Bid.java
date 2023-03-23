package com.example.auctionproject.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "bidder_name")
    private String bidderName;
    @Column(name = "bid_date")
    private LocalDateTime bidDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Lot lot;

}
