package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.RestockRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.StockResponse;
import com.nelly.hivtbmonitoringsystem.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
public class AdminStockController {

    private final StockService stockService;

    @GetMapping("/resupply-requests")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'FACILITY_PROVIDER', 'SUPERVISOR')")
    public ResponseEntity<List<StockResponse>> getPendingResupplyRequests() {
        return ResponseEntity.ok(stockService.getPendingResupplyRequests());
    }

    @PutMapping("/{id}/restock")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'FACILITY_PROVIDER')")
    public ResponseEntity<StockResponse> restock(@PathVariable UUID id,
                                                  @Valid @RequestBody RestockRequest request) {
        return ResponseEntity.ok(stockService.restock(id, request));
    }
}
