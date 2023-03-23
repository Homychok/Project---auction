package com.example.auctionproject.repositories;

import com.example.auctionproject.enums.LotStatus;
import com.example.auctionproject.models.Lot;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findAllByStatus(LotStatus lotStatus, PageRequest pageRequest);
    }
