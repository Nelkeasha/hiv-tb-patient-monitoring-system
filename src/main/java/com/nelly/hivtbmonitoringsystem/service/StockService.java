package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.DispenseRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.InitStockRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RestockRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.DispenseResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.StockResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.DispensingEvent;
import com.nelly.hivtbmonitoringsystem.entity.HomeVisit;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.StockRecord;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.DispensingEventRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.StockRecordRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRecordRepository stockRepository;
    private final DispensingEventRepository dispensingRepository;
    private final PatientRepository patientRepository;
    private final HomeVisitRepository visitRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;

    @Transactional
    public StockResponse initStock(InitStockRequest req) {
        Chw chw = resolveCurrentChw();

        stockRepository.findByChwIdAndMedicationName(chw.getId(), req.getMedicationName())
                .ifPresent(s -> { throw new RuntimeException("Stock already exists for: " + req.getMedicationName()); });

        StockRecord stock = StockRecord.builder()
                .chw(chw)
                .medicationName(req.getMedicationName())
                .currentQuantity(req.getInitialQuantity())
                .reorderLevel(req.getReorderLevel() != null ? req.getReorderLevel() : 14)
                .unit(req.getUnit() != null ? req.getUnit() : "tablets")
                .daysRemaining(req.getInitialQuantity())
                .resupplyRequested(false)
                .lastRestockedAt(LocalDateTime.now())
                .build();

        stockRepository.save(stock);
        return toResponse(stock);
    }

    public List<StockResponse> getMyStock() {
        Chw chw = resolveCurrentChw();
        return stockRepository.findByChwId(chw.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StockResponse> getLowStock() {
        Chw chw = resolveCurrentChw();
        return stockRepository.findByChwIdAndCurrentQuantityBelowReorderLevel(chw.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public DispenseResponse dispense(DispenseRequest req) {
        Chw chw = resolveCurrentChw();

        StockRecord stock = stockRepository.findByChwIdAndMedicationName(chw.getId(), req.getMedicationName())
                .orElseThrow(() -> new RuntimeException("No stock found for: " + req.getMedicationName()));

        if (stock.getCurrentQuantity() < req.getQuantityDispensed()) {
            throw new RuntimeException("Insufficient stock. Available: " + stock.getCurrentQuantity());
        }

        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found: " + req.getPatientId()));

        HomeVisit visit = null;
        if (req.getVisitId() != null) {
            visit = visitRepository.findById(req.getVisitId()).orElse(null);
        }

        stock.setCurrentQuantity(stock.getCurrentQuantity() - req.getQuantityDispensed());
        stock.setDaysRemaining(stock.getCurrentQuantity());

        if (stock.getCurrentQuantity() <= stock.getReorderLevel()) {
            stock.setResupplyRequested(true);
        }

        stockRepository.save(stock);

        DispensingEvent event = DispensingEvent.builder()
                .stock(stock)
                .patient(patient)
                .chw(chw)
                .medicationName(req.getMedicationName())
                .quantityDispensed(req.getQuantityDispensed())
                .visit(visit)
                .syncStatus(SyncStatus.PENDING)
                .build();

        dispensingRepository.save(event);

        return DispenseResponse.builder()
                .dispensingEventId(event.getId())
                .medicationName(event.getMedicationName())
                .quantityDispensed(event.getQuantityDispensed())
                .patientName(patient.getFullName())
                .dispensedAt(event.getDispensedAt())
                .updatedStock(toResponse(stock))
                .build();
    }

    @Transactional
    public StockResponse requestResupply(UUID stockId) {
        Chw chw = resolveCurrentChw();
        StockRecord stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock record not found: " + stockId));
        if (!stock.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: stock not owned by you");
        }
        stock.setResupplyRequested(true);
        stockRepository.save(stock);
        return toResponse(stock);
    }

    @Transactional
    public StockResponse restock(UUID stockId, RestockRequest req) {
        StockRecord stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock record not found: " + stockId));
        stock.setCurrentQuantity(stock.getCurrentQuantity() + req.getQuantityAdded());
        stock.setDaysRemaining(stock.getCurrentQuantity());
        stock.setResupplyRequested(false);
        stock.setLastRestockedAt(LocalDateTime.now());
        stockRepository.save(stock);
        return toResponse(stock);
    }

    public List<StockResponse> getPendingResupplyRequests() {
        return stockRepository.findByResupplyRequestedTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private Chw resolveCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CHW profile not found for user: " + email));
    }

    private StockResponse toResponse(StockRecord s) {
        return StockResponse.builder()
                .id(s.getId())
                .medicationName(s.getMedicationName())
                .currentQuantity(s.getCurrentQuantity())
                .reorderLevel(s.getReorderLevel())
                .unit(s.getUnit())
                .daysRemaining(s.getDaysRemaining())
                .resupplyRequested(s.getResupplyRequested())
                .belowReorderLevel(s.getCurrentQuantity() <= s.getReorderLevel())
                .lastRestockedAt(s.getLastRestockedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
