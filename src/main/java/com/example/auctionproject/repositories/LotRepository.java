package com.example.auctionproject.repositories;

import com.example.auctionproject.enums.LotStatus;
import com.example.auctionproject.models.Lot;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository

public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findAllByStatus(LotStatus lotStatus, PageRequest pageRequest);
    }
