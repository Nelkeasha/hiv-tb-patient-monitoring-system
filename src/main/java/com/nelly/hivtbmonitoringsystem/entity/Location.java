package com.nelly.hivtbmonitoringsystem.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Code is required")
    @Size(min = 2, max = 10, message = "Code must be between 2 and 10 characters")
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Location type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", nullable = false)
    private LocationType locationType;

    // Self-referential relationship for hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Location parent;

    // One-to-Many relationship with child locations
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Location> children = new ArrayList<>();

    @Column(name = "population")
    private Long population;

    @Column(name = "area_km2")
    private Double areaKm2;

    // Optional field for villages
    @Column(name = "village_chief")
    private String villageChief;

    // Constructors
    public Location() {
    }

    public Location(String name, String code, String description, LocationType locationType) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.locationType = locationType;
    }

    public Location(String name, String code, String description, LocationType locationType, Location parent) {
        this(name, code, description, locationType);
        this.parent = parent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    public List<Location> getChildren() {
        return children;
    }

    public void setChildren(List<Location> children) {
        this.children = children;
    }

    public Long getPopulation() {
        return population;
    }

    public void setPopulation(Long population) {
        this.population = population;
    }

    public Double getAreaKm2() {
        return areaKm2;
    }

    public void setAreaKm2(Double areaKm2) {
        this.areaKm2 = areaKm2;
    }

    public String getVillageChief() {
        return villageChief;
    }

    public void setVillageChief(String villageChief) {
        this.villageChief = villageChief;
    }

    // Helper methods
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Location child) {
        children.remove(child);
        child.setParent(null);
    }

    // Helper methods to get hierarchy levels
    @JsonIgnore
    public Location getProvince() {
        if (locationType == LocationType.PROVINCE) {
            return this;
        }
        if (parent != null) {
            return parent.getProvince();
        }
        return null;
    }

    @JsonIgnore
    public Location getDistrict() {
        if (locationType == LocationType.DISTRICT) {
            return this;
        }
        if (parent != null) {
            return parent.getDistrict();
        }
        return null;
    }

    @JsonIgnore
    public Location getSector() {
        if (locationType == LocationType.SECTOR) {
            return this;
        }
        if (parent != null) {
            return parent.getSector();
        }
        return null;
    }

    @JsonIgnore
    public Location getCell() {
        if (locationType == LocationType.CELL) {
            return this;
        }
        if (parent != null) {
            return parent.getCell();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", locationType=" + locationType +
                ", parent=" + (parent != null ? parent.getName() : "null") +
                ", population=" + population +
                ", areaKm2=" + areaKm2 +
                '}';
    }

    // Enum for Location Type
    public enum LocationType {
        PROVINCE,
        DISTRICT,
        SECTOR,
        CELL,
        VILLAGE
    }
}

