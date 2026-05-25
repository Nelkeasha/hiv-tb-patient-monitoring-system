package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.DispenseRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.InitStockRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.DispenseResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.StockResponse;
import com.nelly.hivtbmonitoringsystem.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chw/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CHW')")
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> initStock(@Valid @RequestBody InitStockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.initStock(request));
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getMyStock() {
        return ResponseEntity.ok(stockService.getMyStock());
    }

    @GetMapping("/low")
    public ResponseEntity<List<StockResponse>> getLowStock() {
        return ResponseEntity.ok(stockService.getLowStock());
    }

    @PostMapping("/dispense")
    public ResponseEntity<DispenseResponse> dispense(@Valid @RequestBody DispenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.dispense(request));
    }

    @PutMapping("/{id}/request-resupply")
    public ResponseEntity<StockResponse> requestResupply(@PathVariable UUID id) {
        return ResponseEntity.ok(stockService.requestResupply(id));
    }
}
