package com.example.auctionproject.controllers;

import com.example.auctionproject.dto.BidDTOForFullLotDTO;
import com.example.auctionproject.dto.CreateLotDTO;
import com.example.auctionproject.dto.FullLotDTO;
import com.example.auctionproject.dto.LotDTO;
import com.example.auctionproject.enums.LotStatus;
import com.example.auctionproject.pojection.LotProjection;
import com.example.auctionproject.services.BidService;
import com.example.auctionproject.services.LotService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

@RestController
@RequestMapping("lot")
public class LotController {
    private LotService lotService;
    private BidService bidService;
    public LotController(LotService lotService, BidService bidService) {
        this.lotService = lotService;
        this.bidService = bidService;
    }
    /*
1.Возвращает первого ставившего на этот лот
 */
    @GetMapping("/{id}/first")
    public ResponseEntity<?> getFirstBidder(@PathVariable Long id) {
        LotProjection firstBidder = bidService.getFirstBidderByLotId(id);
        if (firstBidder == null) {
            return ResponseEntity.status(404).body("Лот не найден");
        }
        return ResponseEntity.ok(firstBidder);
    }
    /*
2.Возвращает имя ставившего на данный лот наибольшее количество раз
*/
    @GetMapping("/{id}/frequent")
    public ResponseEntity<?> getMostFrequentBidder(@PathVariable Long id) {
        if (lotService.getLastBidForLot(id) == null) {
            return ResponseEntity.status(404).body("Лот не найден");
        }
        if (lotService.getLotById(id).getStatus().equals(LotStatus.CREATED)) {
            return ResponseEntity.status(404).body("Лот еще не участвует в аукционе");
        }
            return ResponseEntity.ok(lotService.getLastBidForLot(id));
    }
/*

 */
@GetMapping("/{id}")
public ResponseEntity<?> getFullLot(@PathVariable Long id){
    FullLotDTO fullLotDTO = lotService.getFullInfoAboutLot(id);
    if (fullLotDTO != null){
        return ResponseEntity.ok(fullLotDTO);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Лот не найден");
}
    /*
    4.Начать торги по лоту
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> updateStatusAuctionStart(@PathVariable Long id) {
        LotDTO updateInfoAboutLot = lotService.getLotById(id);
        if (updateInfoAboutLot == null) {
            return ResponseEntity.status(404).body("Лот не найден");
        }
        if (updateInfoAboutLot.getStatus().equals(LotStatus.STARTED)) {
            return ResponseEntity.status(200).body("Лот в статусе начато");
        }
        if (updateInfoAboutLot.getStatus().equals(LotStatus.STOPPED)) {
            lotService.updateStatus(id, LotStatus.STARTED);
            return ResponseEntity.status(200).body("Лот переведен в статус начато");
        }
        if (updateInfoAboutLot.getStatus().equals(LotStatus.CREATED)) {
            lotService.updateStatus(id, LotStatus.STARTED);
            return ResponseEntity.status(200).body("Лот переведен в статус начато");
        }
        return ResponseEntity.ok().build();
    }
    /*
5.Сделать ставку по лоту
 */
    @PostMapping("/{id}/bid")
    public ResponseEntity<?> createBid(@RequestBody BidDTOForFullLotDTO bidDTOForFullLotDTO,
                                               @PathVariable Long id) {
        LotDTO findInfoAboutLot = lotService.getLotById(id);
        if (findInfoAboutLot == null) {
            return ResponseEntity.status(404).body("Лот не найден");
        }
        if (findInfoAboutLot.getStatus().equals(LotStatus.STOPPED) ||
                findInfoAboutLot.getStatus().equals(LotStatus.CREATED)) {
            return ResponseEntity.status(400).body("Лот в неверном статусе");
        }
        lotService.createBidder(id, bidDTOForFullLotDTO);
        return ResponseEntity.status(200).body("Ставка создана");
    }
    /*
6.Остановить торги по лоту
 */
    @PostMapping("/{id}/stop")
    public ResponseEntity<?> updateStatusAuctionStop(@PathVariable Long id) {
        LotDTO findInfoAboutLot = lotService.getLotById(id);
        if (findInfoAboutLot == null) {
            return ResponseEntity.status(404).body("Лот не найден");
        }
        if (findInfoAboutLot.getStatus().equals(LotStatus.STOPPED)) {
            return ResponseEntity.status(200).body("Лот в статусе остановлен");
        }
        if (findInfoAboutLot.getStatus().equals(LotStatus.STARTED)) {
            lotService.updateStatus(id, LotStatus.STOPPED);
            return ResponseEntity.status(200).body("Лот переведен в статус остановлен");
        }
        if (findInfoAboutLot.getStatus().equals(LotStatus.CREATED)) {
            lotService.updateStatus(id, LotStatus.STOPPED);
            return ResponseEntity.status(200).body("Лот переведен в статус остановлен");

        }
        return ResponseEntity.ok().build();
    }
    /*
7.Создает новый лот
 */
    @PostMapping
    public ResponseEntity<?> createLot(@RequestBody CreateLotDTO createLotDTO) {
        LotDTO createNewLot = lotService.createNewLot(createLotDTO);
        boolean checking = lotService.checkMistakeInCreatingLot(createLotDTO);
        if (!checking) {
            return ResponseEntity.status(400).body("Лот передан с ошибкой");
        }
        return ResponseEntity.ok(createNewLot);
    }
    /*
    8.Получить все лоты, основываясь на фильтре статуса и номере страницы
     */
    @GetMapping
    public ResponseEntity<Collection<LotDTO>> findLots(@RequestParam LotStatus lotStatus,
                                                         @RequestParam(name = "page", required = false) Integer pageNumber) {
        if (pageNumber <= 0) {
            return ResponseEntity.ok(lotService.getAllLotsByStatusOnPage(lotStatus, 1));
        }

        return ResponseEntity.ok(lotService.getAllLotsByStatusOnPage(lotStatus, pageNumber));
    }
    /*
9. Экспортировать все лоты в файл CSV
 */
    @GetMapping("/export")
    public void downloadLotTable(HttpServletResponse response) throws IOException {
        Collection<FullLotDTO> lots = lotService.getAllFullLots();
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        for (FullLotDTO lot : lots) {
            csvPrinter.printRecord(lot.getId(),
                    lot.getTitle(),
                    lot.getStatus(),
                    lot.getStartPrice(),
                    lot.getLastBid() != null ? lot.getLastBid().getBidderName() : "",
                    lot.getCurrentPrice());
        }
        csvPrinter.flush();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"lots.csv\"");

        PrintWriter pWriter = response.getWriter();
        pWriter.write(writer.toString());
        pWriter.flush();
        pWriter.close();
    }
    /*
3.Получить полную информацию о лоте
 */

}


