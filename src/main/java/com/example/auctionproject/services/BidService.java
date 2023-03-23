package com.example.auctionproject.services;

import com.example.auctionproject.dto.BidDTO;
import com.example.auctionproject.dto.BidDTOForFullLotDTO;
import com.example.auctionproject.dto.LotDTO;
import com.example.auctionproject.models.Bid;
import com.example.auctionproject.pojection.LotProjection;
import com.example.auctionproject.repositories.BidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@Service
public class BidService {
    private BidRepository bidRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public LotProjection getFirstBidderByLotId (Long lotId) {
    return bidRepository.findFirstBidderByBidDateMin(lotId);
    }
    public BidDTOForFullLotDTO getMaxBiddersOfBidByLotId(Long lotId) {
        BidDTOForFullLotDTO bidDTOForFullLotDTO = new BidDTOForFullLotDTO();
        bidDTOForFullLotDTO.setBidDate(bidRepository.findLastByBidDateMax(lotId).getBidDate());
        bidDTOForFullLotDTO.setBidderName(bidRepository.findLastByBidDateMax(lotId).getBidderName());
        return bidDTOForFullLotDTO;
    }

    public BidDTOForFullLotDTO createNewBidder (BidDTOForFullLotDTO bidDTOForFullLotDTO, LotDTO lotDTO) {
        Bid bid = bidDTOForFullLotDTO.toBid();
        bid.setLot(lotDTO.toLot());
        bid.setBidDate(LocalDateTime.now());
        return BidDTOForFullLotDTO.fromBid(bidRepository.save(bid));
    }

//    public BidDTO getLastBidderByLotId (Long lotId) {
//        return bidRepository.findLastByBidDateMax(lotId);
//    }

    public Long countTotalPrice(Long lotId) {
        return bidRepository.getCountNumberOfBidByLotId(lotId);
    }
    public BidDTO findLastBid(Long id) {
        return BidDTO.fromBid(bidRepository.findLastByBidDateMax(id));
    }
    public LotProjection findFrequent(Long lotId){
        return bidRepository.getLastBidderWithMaxNumbersOfBid(lotId);
    }
}
