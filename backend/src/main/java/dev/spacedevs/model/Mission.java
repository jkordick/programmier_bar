package dev.spacedevs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer difficultyRating;

    @NotNull
    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_developer_id", nullable = false)
    @JsonIgnore
    private SpaceDeveloper spaceDeveloper;

    public Mission() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getDifficultyRating() { return difficultyRating; }
    public void setDifficultyRating(Integer difficultyRating) { this.difficultyRating = difficultyRating; }

    public MissionStatus getStatus() { return status; }
    public void setStatus(MissionStatus status) { this.status = status; }

    public SpaceDeveloper getSpaceDeveloper() { return spaceDeveloper; }
    public void setSpaceDeveloper(SpaceDeveloper spaceDeveloper) { this.spaceDeveloper = spaceDeveloper; }
}
