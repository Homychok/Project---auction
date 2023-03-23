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
//    private LotService lotService;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }
////@Autowired
//    public BidService(BidRepository bidRepository, LotService lotService) {
//        this.bidRepository = bidRepository;
//        this.lotService = lotService;
////    }
//    public BidDTO createBid (BidDTO bidDTO) {
//        Bid bid = BidDTO.toBid(bidDTO);
//        bid.setBidderName(bid.getBidderName());
//        Bid newBid = bidRepository.save(bid);
//        return BidDTO.fromBid(newBid);
//    }
    public LotProjection getFirstBidderByLotId (Long lotId) {
    return bidRepository.findFirstBidderByBidDateMin(lotId);
    }
    public LotProjection getMaxBiddersOfBidByLotId(Long lotId) {
        BidDTO bidDTO = new BidDTO();
        bidDTO.setBidderName(bidRepository.findLastByBidDateMax(lotId).getBidderName());
        bidDTO.setBidDate(bidRepository.findLastByBidDateMax(lotId).getBidDate());
        return bidRepository.getLastBidderWithMaxNumbersOfBid(lotId);
    }

    public BidDTOForFullLotDTO createNewBidder (BidDTOForFullLotDTO bidDTOForFullLotDTO, LotDTO lotDTO) {
        Bid bid = bidDTOForFullLotDTO.toBid();
        bid.setLot(lotDTO.toLot());
        bid.setBidDate(LocalDateTime.now());
        return BidDTOForFullLotDTO.fromBid(bidRepository.save(bid));
    }

    public Bid getLastBidderByLotId (Long lotId) {
        return bidRepository.findLastByBidDateMax(lotId);
    }

    public Long countTotalPrice(Long lotId) {
        return bidRepository.getCountNumberOfBidByLotId(lotId);
    }
    public BidDTOForFullLotDTO findLastBid(Long id) {
        return BidDTOForFullLotDTO.fromBid(bidRepository.findLastByBidDateMax(id));
    }
}
