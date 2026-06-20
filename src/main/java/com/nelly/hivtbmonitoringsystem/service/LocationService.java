package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.Location;
import com.nelly.hivtbmonitoringsystem.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // Create
    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    // Read - All locations
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<Location> getLocationByCode(String code) {
        return locationRepository.findByCode(code);
    }

    public List<Location> getLocationsByName(String name) {
        return locationRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Location> getLocationsByType(Location.LocationType locationType) {
        return locationRepository.findByLocationType(locationType);
    }

    public List<Location> getLocationsByParent(Location parent) {
        return locationRepository.findByParent(parent);
    }

    public List<Location> getLocationsByParentId(Long parentId) {
        return locationRepository.findByParentId(parentId);
    }

    public List<Location> getLocationsByPopulationGreaterThan(Long population) {
        return locationRepository.findByPopulationGreaterThan(population);
    }

    public List<Location> getLocationsByAreaGreaterThan(Double area) {
        return locationRepository.findByAreaKm2GreaterThan(area);
    }

    // Type-specific convenience methods
    public List<Location> getAllProvinces() {
        return locationRepository.findByLocationType(Location.LocationType.PROVINCE);
    }

    public List<Location> getAllDistricts() {
        return locationRepository.findByLocationType(Location.LocationType.DISTRICT);
    }

    public List<Location> getAllSectors() {
        return locationRepository.findByLocationType(Location.LocationType.SECTOR);
    }

    public List<Location> getAllCells() {
        return locationRepository.findByLocationType(Location.LocationType.CELL);
    }

    public List<Location> getAllVillages() {
        return locationRepository.findByLocationType(Location.LocationType.VILLAGE);
    }

    public List<Location> getDistrictsByProvinceId(Long provinceId) {
        return locationRepository.findByLocationTypeAndParentId(Location.LocationType.DISTRICT, provinceId);
    }

    public List<Location> getSectorsByDistrictId(Long districtId) {
        return locationRepository.findByLocationTypeAndParentId(Location.LocationType.SECTOR, districtId);
    }

    public List<Location> getCellsBySectorId(Long sectorId) {
        return locationRepository.findByLocationTypeAndParentId(Location.LocationType.CELL, sectorId);
    }

    public List<Location> getVillagesByCellId(Long cellId) {
        return locationRepository.findByLocationTypeAndParentId(Location.LocationType.VILLAGE, cellId);
    }

    // Pagination
    public Page<Location> getAllLocations(Pageable pageable) {
        return locationRepository.findAll(pageable);
    }

    public Page<Location> getLocationsByType(Location.LocationType locationType, Pageable pageable) {
        return locationRepository.findByLocationType(locationType, pageable);
    }

    public Page<Location> getLocationsByParentId(Long parentId, Pageable pageable) {
        return locationRepository.findByParentId(parentId, pageable);
    }

    public Page<Location> getLocationsByPopulationGreaterThan(Long population, Pageable pageable) {
        return locationRepository.findByPopulationGreaterThan(population, pageable);
    }

    public Page<Location> getLocationsByName(String name, Pageable pageable) {
        return locationRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    // Sorting
    public List<Location> getLocationsOrderedByName() {
        return locationRepository.findAllByOrderByNameAsc();
    }

    public List<Location> getLocationsOrderedByPopulation() {
        return locationRepository.findAllByOrderByPopulationDesc();
    }

    public List<Location> getLocationsOrderedByArea() {
        return locationRepository.findAllByOrderByAreaKm2Desc();
    }

    public List<Location> getLocationsByTypeOrderedByName(Location.LocationType locationType) {
        return locationRepository.findByLocationTypeOrderByNameAsc(locationType);
    }

    // Update
    public Location updateLocation(Long id, Location locationDetails) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        location.setName(locationDetails.getName());
        location.setCode(locationDetails.getCode());
        location.setDescription(locationDetails.getDescription());
        location.setLocationType(locationDetails.getLocationType());
        location.setParent(locationDetails.getParent());
        location.setPopulation(locationDetails.getPopulation());
        location.setAreaKm2(locationDetails.getAreaKm2());
        if (locationDetails.getVillageChief() != null) {
            location.setVillageChief(locationDetails.getVillageChief());
        }

        return locationRepository.save(location);
    }

    // Delete
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }

    // Exists checks
    public boolean existsByName(String name) {
        return locationRepository.existsByName(name);
    }

    public boolean existsByCode(String code) {
        return locationRepository.existsByCode(code);
    }

    public boolean existsByLocationType(Location.LocationType locationType) {
        return locationRepository.existsByLocationType(locationType);
    }

    public boolean existsByPopulationGreaterThan(Long population) {
        return locationRepository.existsByPopulationGreaterThan(population);
    }

    // Custom queries
    public List<Location> getLocationsByTypeWithPopulationGreaterThan(Location.LocationType locationType,
                                                                      Long population) {
        return locationRepository.findLocationsByTypeWithPopulationGreaterThan(locationType, population);
    }

    public List<Location> getLocationsByAreaRange(Double minArea, Double maxArea) {
        return locationRepository.findLocationsByAreaRange(minArea, maxArea);
    }

    public long countByLocationType(Location.LocationType locationType) {
        return locationRepository.countByLocationType(locationType);
    }

    public long countByParent(Location parent) {
        return locationRepository.countByParent(parent);
    }

    // Village-specific queries
    public List<Location> getVillagesByChiefName(String chiefName) {
        return locationRepository.findVillagesByChiefName(chiefName);
    }

    public long countVillagesByCellId(Long cellId) {
        return locationRepository.countVillagesByCellId(cellId);
    }

    public List<Location> getLocationsByVillageChief(String villageChief) {
        return locationRepository.findByVillageChief(villageChief);
    }
}

