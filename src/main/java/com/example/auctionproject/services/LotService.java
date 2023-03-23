package com.example.auctionproject.services;

import com.example.auctionproject.dto.BidDTOForFullLotDTO;
import com.example.auctionproject.dto.CreateLotDTO;
import com.example.auctionproject.dto.FullLotDTO;
import com.example.auctionproject.dto.LotDTO;
import com.example.auctionproject.enums.LotStatus;
import com.example.auctionproject.models.Lot;
import com.example.auctionproject.pojection.LotProjection;
import com.example.auctionproject.repositories.BidRepository;
import com.example.auctionproject.repositories.LotRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@Transactional
public class LotService {
    private final HttpServletResponse response;
    private LotRepository lotRepository;
    private BidService bidService;
    private BidRepository bidRepository;

    public LotService(LotRepository lotRepository, BidService bidService, HttpServletResponse response) {
        this.lotRepository = lotRepository;
        this.bidService = bidService;
        this.response = response;
    }

    public LotProjection getLastBidForLot(Long id) {
//        BidDTOForFullLotDTO bidDTOForFullLotDTO = new BidDTOForFullLotDTO();
//        bidDTOForFullLotDTO.setBidderName(bi);
//        bidDTOForFullLotDTO.setBidDate(bidService.getMaxBiddersOfBidByLotId(id).getBidDate());
        return bidRepository.getLastBidderWithMaxNumbersOfBid(id);
    }

    public FullLotDTO getFullInfoAboutLot(Long id) {
        Lot lot = lotRepository.findById(id).orElse(null);
        if (lot == null) {
            return null;
        }
            FullLotDTO fullLotDTO = FullLotDTO.fromLot(lot);
            fullLotDTO.setCurrentPrice(countCurrentPrice(id));
            fullLotDTO.setLastBid(findLastBid(id));
            return fullLotDTO;    }

    private Long countCurrentPrice(Long lotId) {
        Lot lot = getLotById(lotId).toLot();
        Long countPrice = bidService.countTotalPrice(lotId);
        return countPrice * lot.getBidPrice() + lot.getStartPrice();
    }

    private BidDTOForFullLotDTO findLastBid(Long id) {
        if (bidService.countTotalPrice(id) != 0) {
            return bidService.findLastBid(id);
        }
        return null;
    }

    public LotDTO getLotById(Long id) {
        Lot lot = lotRepository.findById(id).orElse(null);
        return LotDTO.fromLot(lot);
    }

    public void updateStatus(Long id, LotStatus lotStatus) {
        Lot lot = lotRepository.findById(id).orElse(null);
        lot.setStatus(lotStatus);
        lotRepository.save(lot);
    }

    public BidDTOForFullLotDTO createBidder(Long id, BidDTOForFullLotDTO bidDTOForFullLotDTO) {
        LotDTO lotDTO = LotDTO.fromLot(lotRepository.findById(id).orElse(null));
        if (lotDTO == null) {
            return null;
        }
        return bidService.createNewBidder(bidDTOForFullLotDTO, lotDTO);
    }

    public LotDTO createNewLot(CreateLotDTO createLotDTO) {
        Lot lot = createLotDTO.toLot();
        lot.setStatus(LotStatus.CREATED);
        return LotDTO.fromLot(lotRepository.save(lot));
    }

    public List<LotDTO> getAllLotsByStatusOnPage(LotStatus lotStatus, Integer pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10);
        return lotRepository.findAllByStatus(lotStatus, pageRequest)
                .stream()
                .map(LotDTO::fromLot)
                .collect(Collectors.toList());
    }
    public Collection<FullLotDTO> getAllFullLots() {
        return lotRepository.findAll()
                .stream()
                .map(FullLotDTO::fromLot)
                .peek(lot -> lot.setCurrentPrice(countCurrentPrice(lot.getId())))
                .peek(lot -> lot.setLastBid(bidService.getMaxBiddersOfBidByLotId(lot.getId())))
                .collect(Collectors.toList());
    }

    public FullLotDTO getFullLotById (Long id) {
        Lot lot = lotRepository.findById(id).orElse(null);
        if (lot == null) {
            return null;
        }
        FullLotDTO fullLotDTO = FullLotDTO.fromLot(lot);
        fullLotDTO.setCurrentPrice(countCurrentPrice(id));
        fullLotDTO.setLastBid(getLastBidderByLotId(id));
        return fullLotDTO;
    }

    public BidDTOForFullLotDTO getLastBidderByLotId (Long lotId) {
        return BidDTOForFullLotDTO.fromBid(bidService.getLastBidderByLotId(lotId));
    }
    public boolean checkMistakeInCreatingLot(CreateLotDTO createLotDTO) {
        if(createLotDTO.getTitle() == null || createLotDTO.getTitle().isBlank() ||
                createLotDTO.getDescription() == null || createLotDTO.getDescription().isBlank() ||
        createLotDTO.getStartPrice() == null || createLotDTO.getBidPrice() == null){
            return false;
        } else {
            return true;
        }
    }


}
