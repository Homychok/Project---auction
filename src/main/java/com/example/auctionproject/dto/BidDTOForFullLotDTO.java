package com.example.auctionproject.dto;

import com.example.auctionproject.models.Bid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class BidDTOForFullLotDTO {
    private Long id;
    private String bidderName;
    @JsonIgnore
    private LocalDateTime bidDate;
    private Long lotId;
    public static BidDTOForFullLotDTO fromBid(Bid bid) {
        BidDTOForFullLotDTO bidDTOForFullLotDTO = new BidDTOForFullLotDTO();
        bidDTOForFullLotDTO.setId(bid.getId());
        bidDTOForFullLotDTO.setBidderName(bid.getBidderName());
        bidDTOForFullLotDTO.setBidDate(bid.getBidDate());
        bidDTOForFullLotDTO.setLotId(bidDTOForFullLotDTO.getLotId());
        return bidDTOForFullLotDTO;
    }

    public Bid toBid() {
        Bid bid = new Bid();
        bid.setId(this.getId());
        bid.setBidderName(this.getBidderName());
        bid.setBidDate(LocalDateTime.now());
        return bid;
    }
}
