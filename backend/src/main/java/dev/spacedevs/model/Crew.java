package dev.spacedevs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "crews")
public class Crew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String missionStatement;

    private String shipName;

    @OneToMany(mappedBy = "crew", fetch = FetchType.EAGER)
    private List<SpaceDeveloper> members = new ArrayList<>();

    public Crew() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMissionStatement() { return missionStatement; }
    public void setMissionStatement(String missionStatement) { this.missionStatement = missionStatement; }

    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }

    public List<SpaceDeveloper> getMembers() { return members; }
    public void setMembers(List<SpaceDeveloper> members) { this.members = members; }
}
