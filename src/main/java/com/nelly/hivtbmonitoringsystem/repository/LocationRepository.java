package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Basic findBy methods
    List<Location> findByName(String name);
    List<Location> findByNameContainingIgnoreCase(String name);
    Optional<Location> findByCode(String code);
    List<Location> findByLocationType(Location.LocationType locationType);
    List<Location> findByParent(Location parent);
    List<Location> findByParentId(Long parentId);
    List<Location> findByPopulationGreaterThan(Long population);
    List<Location> findByPopulationBetween(Long minPopulation, Long maxPopulation);
    List<Location> findByAreaKm2GreaterThan(Double area);
    List<Location> findByVillageChief(String villageChief);

    // Combined queries
    List<Location> findByLocationTypeAndParent(Location.LocationType locationType, Location parent);
    List<Location> findByLocationTypeAndParentId(Location.LocationType locationType, Long parentId);
    List<Location> findByLocationTypeAndPopulationGreaterThan(Location.LocationType locationType, Long population);
    List<Location> findByParentAndPopulationGreaterThan(Location parent, Long population);

    // existsBy methods
    boolean existsByName(String name);
    boolean existsByCode(String code);
    boolean existsByLocationType(Location.LocationType locationType);
    boolean existsByParent(Location parent);
    boolean existsByPopulationGreaterThan(Long population);
    boolean existsByVillageChief(String villageChief);

    // Sorting methods
    List<Location> findAllByOrderByNameAsc();
    List<Location> findAllByOrderByPopulationDesc();
    List<Location> findAllByOrderByAreaKm2Desc();
    List<Location> findByLocationTypeOrderByNameAsc(Location.LocationType locationType);
    List<Location> findByParentOrderByNameAsc(Location parent);
    List<Location> findByNameContainingIgnoreCaseOrderByPopulationDesc(String name);
    List<Location> findByVillageChiefOrderByNameAsc(String villageChief);

    // Pagination methods
    Page<Location> findAll(Pageable pageable);
    Page<Location> findByLocationType(Location.LocationType locationType, Pageable pageable);
    Page<Location> findByParent(Location parent, Pageable pageable);
    Page<Location> findByParentId(Long parentId, Pageable pageable);
    Page<Location> findByPopulationGreaterThan(Long population, Pageable pageable);
    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Custom query methods
    @Query("SELECT l FROM Location l WHERE l.locationType = :locationType AND l.population > :population ORDER BY l.population DESC")
    List<Location> findLocationsByTypeWithPopulationGreaterThan(@Param("locationType") Location.LocationType locationType, @Param("population") Long population);

    @Query("SELECT l FROM Location l WHERE l.areaKm2 BETWEEN :minArea AND :maxArea")
    List<Location> findLocationsByAreaRange(@Param("minArea") Double minArea, @Param("maxArea") Double maxArea);

    @Query("SELECT COUNT(l) FROM Location l WHERE l.locationType = :locationType")
    long countByLocationType(@Param("locationType") Location.LocationType locationType);

    @Query("SELECT COUNT(l) FROM Location l WHERE l.parent = :parent")
    long countByParent(@Param("parent") Location parent);

    @Query("SELECT l FROM Location l JOIN l.children c WHERE c.name = :childName")
    List<Location> findByChildName(@Param("childName") String childName);

    @Query("SELECT l FROM Location l WHERE l.locationType = :locationType AND SIZE(l.children) > :childCount")
    List<Location> findByLocationTypeWithMoreThanChildren(@Param("locationType") Location.LocationType locationType, @Param("childCount") int childCount);

    // Specific type queries
    @Query("SELECT l FROM Location l WHERE l.locationType = 'PROVINCE'")
    List<Location> findAllProvinces();

    @Query("SELECT l FROM Location l WHERE l.locationType = 'DISTRICT'")
    List<Location> findAllDistricts();

    @Query("SELECT l FROM Location l WHERE l.locationType = 'SECTOR'")
    List<Location> findAllSectors();

    @Query("SELECT l FROM Location l WHERE l.locationType = 'CELL'")
    List<Location> findAllCells();

    @Query("SELECT l FROM Location l WHERE l.locationType = 'VILLAGE'")
    List<Location> findAllVillages();

    // Village-specific queries
    @Query("SELECT l FROM Location l WHERE l.locationType = 'VILLAGE' AND l.villageChief = :chiefName")
    List<Location> findVillagesByChiefName(@Param("chiefName") String chiefName);

    @Query("SELECT COUNT(l) FROM Location l WHERE l.locationType = 'VILLAGE' AND l.parent.id = :cellId")
    long countVillagesByCellId(@Param("cellId") Long cellId);

    // Find max code for counter initialization
    @Query("SELECT MAX(l.code) FROM Location l WHERE l.code LIKE 'RW%'")
    String findMaxCode();
}


