package com.nelly.hivtbmonitoringsystem.controller;


import com.nelly.hivtbmonitoringsystem.entity.Location;
import com.nelly.hivtbmonitoringsystem.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    // Create
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<Location> create(@RequestBody Location location) {
        return new ResponseEntity<>(locationService.createLocation(location), HttpStatus.CREATED);
    }

    // Read - All locations
    @GetMapping
    public ResponseEntity<List<Location>> all() {
        return new ResponseEntity<>(locationService.getAllLocations(), HttpStatus.OK);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Location>> all(Pageable pageable) {
        return new ResponseEntity<>(locationService.getAllLocations(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> get(@PathVariable Long id) {
        Optional<Location> location = locationService.getLocationById(id);
        return location.map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read - By type
    @GetMapping("/type/{locationType}")
    public ResponseEntity<List<Location>> getByType(@PathVariable Location.LocationType locationType) {
        return new ResponseEntity<>(locationService.getLocationsByType(locationType), HttpStatus.OK);
    }

    @GetMapping("/type/{locationType}/paged")
    public ResponseEntity<Page<Location>> getByType(@PathVariable Location.LocationType locationType, Pageable pageable) {
        return new ResponseEntity<>(locationService.getLocationsByType(locationType, pageable), HttpStatus.OK);
    }

    // Read - Type-specific convenience endpoints
    @GetMapping("/provinces")
    public ResponseEntity<List<Location>> getAllProvinces() {
        return new ResponseEntity<>(locationService.getAllProvinces(), HttpStatus.OK);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<Location>> getAllDistricts() {
        return new ResponseEntity<>(locationService.getAllDistricts(), HttpStatus.OK);
    }

    @GetMapping("/sectors")
    public ResponseEntity<List<Location>> getAllSectors() {
        return new ResponseEntity<>(locationService.getAllSectors(), HttpStatus.OK);
    }

    @GetMapping("/cells")
    public ResponseEntity<List<Location>> getAllCells() {
        return new ResponseEntity<>(locationService.getAllCells(), HttpStatus.OK);
    }

    @GetMapping("/villages")
    public ResponseEntity<List<Location>> getAllVillages() {
        return new ResponseEntity<>(locationService.getAllVillages(), HttpStatus.OK);
    }

    // Read - By parent
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Location>> getByParent(@PathVariable Long parentId) {
        return new ResponseEntity<>(locationService.getLocationsByParentId(parentId), HttpStatus.OK);
    }

    @GetMapping("/parent/{parentId}/paged")
    public ResponseEntity<Page<Location>> getByParent(@PathVariable Long parentId, Pageable pageable) {
        return new ResponseEntity<>(locationService.getLocationsByParentId(parentId, pageable), HttpStatus.OK);
    }

    // Read - Hierarchy convenience endpoints
    @GetMapping("/provinces/{provinceId}/districts")
    public ResponseEntity<List<Location>> getDistrictsByProvince(@PathVariable Long provinceId) {
        return new ResponseEntity<>(locationService.getDistrictsByProvinceId(provinceId), HttpStatus.OK);
    }

    @GetMapping("/districts/{districtId}/sectors")
    public ResponseEntity<List<Location>> getSectorsByDistrict(@PathVariable Long districtId) {
        return new ResponseEntity<>(locationService.getSectorsByDistrictId(districtId), HttpStatus.OK);
    }

    @GetMapping("/sectors/{sectorId}/cells")
    public ResponseEntity<List<Location>> getCellsBySector(@PathVariable Long sectorId) {
        return new ResponseEntity<>(locationService.getCellsBySectorId(sectorId), HttpStatus.OK);
    }

    @GetMapping("/cells/{cellId}/villages")
    public ResponseEntity<List<Location>> getVillagesByCell(@PathVariable Long cellId) {
        return new ResponseEntity<>(locationService.getVillagesByCellId(cellId), HttpStatus.OK);
    }

    // Read - By code
    @GetMapping("/code/{code}")
    public ResponseEntity<Location> getByCode(@PathVariable String code) {
        Optional<Location> location = locationService.getLocationByCode(code);
        return location.map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Read - By name
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Location>> getByName(@PathVariable String name) {
        return new ResponseEntity<>(locationService.getLocationsByName(name), HttpStatus.OK);
    }

    // Update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<Location> update(@PathVariable Long id, @RequestBody Location locationDetails) {
        try {
            Location updated = locationService.updateLocation(id, locationDetails);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}


