package com.example.auctionproject.dto;

import com.example.auctionproject.enums.LotStatus;
import com.example.auctionproject.models.Lot;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FullLotDTO {
    private BidDTO lastBid;
    private Long id;
    private LotStatus status;
    private String title;
    private String description;
    private Long currentPrice;
    private Long startPrice;
    private Long bidPrice;
public static FullLotDTO fromLot(Lot lot) {
    FullLotDTO fullDTO = new FullLotDTO();
    fullDTO.setId(lot.getId());
    fullDTO.setStatus(lot.getStatus());
    fullDTO.setTitle(lot.getTitle());
    fullDTO.setDescription(lot.getDescription());
    fullDTO.setStartPrice(lot.getStartPrice());
    fullDTO.setBidPrice(lot.getBidPrice());
    fullDTO.setCurrentPrice(lot.getCurrentPrice());
    return fullDTO;
}

    public Lot toLot() {
        Lot lot = new Lot();
        lot.setId(this.getId());
        lot.setStatus(this.getStatus());
        lot.setTitle(this.getTitle());
        lot.setDescription(this.getDescription());
        lot.setStartPrice(this.getStartPrice());
        lot.setBidPrice(this.getBidPrice());
        lot.setCurrentPrice(this.getCurrentPrice());
        return lot;
    }
}
