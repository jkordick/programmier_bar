package dev.spacedevs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "space_developers")
public class SpaceDeveloper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String callSign;

    @NotBlank
    private String realName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Seniority seniority;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "developer_skills", joinColumns = @JoinColumn(name = "developer_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "developer_oss_projects", joinColumns = @JoinColumn(name = "developer_id"))
    @Column(name = "project")
    private List<String> ossProjects = new ArrayList<>();

    private String favoriteDevJoke;

    @Min(0)
    @Max(99)
    private int coffeesPerDayInLiters;

    @Min(0)
    @Max(9001)
    private int debuggingPowerLevel;

    private String rubberDuckName;

    private String favoriteKeyboardShortcut;

    @Min(0)
    private int gitCommitStreak;

    @Min(0)
    private int stackOverflowReputation;

    private boolean stillUsesVim;

    private String shipName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    @JsonIgnore
    private Crew crew;

    @OneToMany(mappedBy = "spaceDeveloper", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Mission> missions = new ArrayList<>();

    public SpaceDeveloper() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCallSign() { return callSign; }
    public void setCallSign(String callSign) { this.callSign = callSign; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public Seniority getSeniority() { return seniority; }
    public void setSeniority(Seniority seniority) { this.seniority = seniority; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getOssProjects() { return ossProjects; }
    public void setOssProjects(List<String> ossProjects) { this.ossProjects = ossProjects; }

    public String getFavoriteDevJoke() { return favoriteDevJoke; }
    public void setFavoriteDevJoke(String favoriteDevJoke) { this.favoriteDevJoke = favoriteDevJoke; }

    public int getCoffeesPerDayInLiters() { return coffeesPerDayInLiters; }
    public void setCoffeesPerDayInLiters(int coffeesPerDayInLiters) { this.coffeesPerDayInLiters = coffeesPerDayInLiters; }

    public int getDebuggingPowerLevel() { return debuggingPowerLevel; }
    public void setDebuggingPowerLevel(int debuggingPowerLevel) { this.debuggingPowerLevel = debuggingPowerLevel; }

    public String getRubberDuckName() { return rubberDuckName; }
    public void setRubberDuckName(String rubberDuckName) { this.rubberDuckName = rubberDuckName; }

    public String getFavoriteKeyboardShortcut() { return favoriteKeyboardShortcut; }
    public void setFavoriteKeyboardShortcut(String favoriteKeyboardShortcut) { this.favoriteKeyboardShortcut = favoriteKeyboardShortcut; }

    public int getGitCommitStreak() { return gitCommitStreak; }
    public void setGitCommitStreak(int gitCommitStreak) { this.gitCommitStreak = gitCommitStreak; }

    public int getStackOverflowReputation() { return stackOverflowReputation; }
    public void setStackOverflowReputation(int stackOverflowReputation) { this.stackOverflowReputation = stackOverflowReputation; }

    public boolean isStillUsesVim() { return stillUsesVim; }
    public void setStillUsesVim(boolean stillUsesVim) { this.stillUsesVim = stillUsesVim; }

    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }

    public List<Mission> getMissions() { return missions; }
    public void setMissions(List<Mission> missions) { this.missions = missions; }

    public Crew getCrew() { return crew; }
    public void setCrew(Crew crew) { this.crew = crew; }
}
