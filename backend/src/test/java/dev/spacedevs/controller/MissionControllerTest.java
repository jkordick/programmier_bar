package dev.spacedevs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.spacedevs.model.Mission;
import dev.spacedevs.model.MissionStatus;
import dev.spacedevs.model.Seniority;
import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.MissionRepository;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpaceDeveloperRepository devRepo;

    @Autowired
    private MissionRepository missionRepo;

    private SpaceDeveloper savedDev;

    @BeforeEach
    void setUp() {
        missionRepo.deleteAll();
        devRepo.deleteAll();

        var dev = new SpaceDeveloper();
        dev.setCallSign("TestPilot");
        dev.setRealName("Test McTester");
        dev.setDebuggingPowerLevel(5000);
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setShipName("The Asserter");
        savedDev = devRepo.save(dev);
    }

    @Test
    void getMissions_returnsEmptyListForDevWithNoMissions() throws Exception {
        mockMvc.perform(get("/api/space-devs/{devId}/missions", savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getMissions_returns404ForNonexistentDev() throws Exception {
        mockMvc.perform(get("/api/space-devs/{devId}/missions", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMissions_returnsMissionsSortedByDateDesc() throws Exception {
        createMission("Older Mission", LocalDate.of(2026, 1, 1), 2, MissionStatus.SUCCESS);
        createMission("Newer Mission", LocalDate.of(2026, 3, 1), 4, MissionStatus.IN_PROGRESS);

        mockMvc.perform(get("/api/space-devs/{devId}/missions", savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Newer Mission")))
                .andExpect(jsonPath("$[1].title", is("Older Mission")));
    }

    @Test
    void createMission_returnsCreatedMission() throws Exception {
        var mission = new Mission();
        mission.setTitle("New Mission");
        mission.setDescription("A test mission");
        mission.setDate(LocalDate.of(2026, 4, 7));
        mission.setDifficultyRating(3);
        mission.setStatus(MissionStatus.IN_PROGRESS);

        mockMvc.perform(post("/api/space-devs/{devId}/missions", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Mission")))
                .andExpect(jsonPath("$.difficultyRating", is(3)))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void createMission_returns404ForNonexistentDev() throws Exception {
        var mission = new Mission();
        mission.setTitle("Orphan Mission");
        mission.setDate(LocalDate.of(2026, 4, 7));
        mission.setDifficultyRating(3);
        mission.setStatus(MissionStatus.SUCCESS);

        mockMvc.perform(post("/api/space-devs/{devId}/missions", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mission)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMission_rejectsDifficultyRatingBelow1() throws Exception {
        var mission = new Mission();
        mission.setTitle("Too Easy");
        mission.setDate(LocalDate.of(2026, 4, 7));
        mission.setDifficultyRating(0);
        mission.setStatus(MissionStatus.SUCCESS);

        mockMvc.perform(post("/api/space-devs/{devId}/missions", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMission_rejectsDifficultyRatingAbove5() throws Exception {
        var mission = new Mission();
        mission.setTitle("Over 9000");
        mission.setDate(LocalDate.of(2026, 4, 7));
        mission.setDifficultyRating(6);
        mission.setStatus(MissionStatus.SUCCESS);

        mockMvc.perform(post("/api/space-devs/{devId}/missions", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMission_rejectsBlankTitle() throws Exception {
        var mission = new Mission();
        mission.setTitle("");
        mission.setDate(LocalDate.of(2026, 4, 7));
        mission.setDifficultyRating(3);
        mission.setStatus(MissionStatus.SUCCESS);

        mockMvc.perform(post("/api/space-devs/{devId}/missions", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mission)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMission_removesExistingMission() throws Exception {
        Mission saved = createMission("To Delete", LocalDate.of(2026, 4, 7), 1, MissionStatus.SUCCESS);

        mockMvc.perform(delete("/api/space-devs/{devId}/missions/{missionId}", savedDev.getId(), saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/space-devs/{devId}/missions", savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void deleteMission_returns404ForNonexistentMission() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{devId}/missions/{missionId}", savedDev.getId(), 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMission_returns404WhenMissionBelongsToDifferentDev() throws Exception {
        var otherDev = new SpaceDeveloper();
        otherDev.setCallSign("OtherPilot");
        otherDev.setRealName("Other McPilot");
        otherDev.setDebuggingPowerLevel(1000);
        otherDev.setSeniority(Seniority.MASS_OF_SPACE_DUST);
        otherDev.setShipName("The Other Ship");
        otherDev = devRepo.save(otherDev);

        Mission mission = createMission("Not Yours", LocalDate.of(2026, 4, 7), 3, MissionStatus.SUCCESS);

        // Try to delete dev1's mission via dev2's endpoint
        mockMvc.perform(delete("/api/space-devs/{devId}/missions/{missionId}", otherDev.getId(), mission.getId()))
                .andExpect(status().isNotFound());
    }

    private Mission createMission(String title, LocalDate date, int difficulty, MissionStatus status) {
        var mission = new Mission();
        mission.setTitle(title);
        mission.setDate(date);
        mission.setDifficultyRating(difficulty);
        mission.setStatus(status);
        mission.setSpaceDeveloper(savedDev);
        return missionRepo.save(mission);
    }
}
