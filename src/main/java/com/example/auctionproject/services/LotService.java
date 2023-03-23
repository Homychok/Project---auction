package com.example.auctionproject.services;

import com.example.auctionproject.dto.*;
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
        return bidService.findFrequent(id);
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

    private Long countCurrentPrice(Long id) {
        Lot lot = getLotById(id).toLot();
        Long countPrice = bidService.countTotalPrice(id);
        return countPrice * lot.getBidPrice() + lot.getStartPrice();
    }

    private BidDTO findLastBid(Long id) {
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
    @Transactional
    public List<LotDTO> getAllLotsByStatusOnPage(LotStatus lotStatus, Integer pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 10);
        return lotRepository.findAllByStatus(lotStatus, pageRequest)
                .stream()
                .map(LotDTO::fromLot)
                .collect(Collectors.toList());
    }
    public List<FullLotDTO> getAllFullLots() {
        return lotRepository.findAll()
                .stream()
                .map(FullLotDTO::fromLot)
                .peek(fullLotDTO -> fullLotDTO.setCurrentPrice(countCurrentPrice(fullLotDTO.getId())))
                .peek(fullLotDTO -> fullLotDTO.setLastBid(findLastBid(fullLotDTO.getId())))
                .collect(Collectors.toList());
    }

//    public FullLotDTO getFullLotById (Long id) {
//        Lot lot = lotRepository.findById(id).orElse(null);
//        if (lot == null) {
//            return null;
//        }
//        FullLotDTO fullLotDTO = FullLotDTO.fromLot(lot);
//        fullLotDTO.setCurrentPrice(countCurrentPrice(id));
//        fullLotDTO.setLastBid(getLastBidderByLotId(id));
//        return fullLotDTO;
//    }

//    public BidDTOForFullLotDTO getLastBidderByLotId (Long lotId) {
//        return bidService.getLastBidderByLotId(lotId);
//    }
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
