package com.example.auctionproject.pojection;

import java.time.LocalDateTime;

public interface LotProjection {
    String getBidderName();
    LocalDateTime getBidDate();
}
