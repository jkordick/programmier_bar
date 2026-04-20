package dev.spacedevs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpaceDeveloperControllerTest {

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
        dev.setCallSign("StarCoder");
        dev.setRealName("Ada Lovelace");
        dev.setSeniority(Seniority.MASS_OF_A_STAR);
        dev.setDebuggingPowerLevel(8000);
        dev.setCoffeesPerDayInLiters(3);
        dev.setShipName("Enterprise Null");
        dev.setSkills(List.of("Java", "Kotlin"));
        dev.setFavoriteDevJoke("Why do Java developers wear glasses? Because they can't C#.");
        savedDev = devRepo.save(dev);
    }

    // --- GET /api/space-devs ---

    @Test
    void getAll_returnsListOfDevs() throws Exception {
        mockMvc.perform(get("/api/space-devs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].callSign", is("StarCoder")));
    }

    // --- GET /api/space-devs/{id} ---

    @Test
    void getById_returnsDevWhenFound() throws Exception {
        mockMvc.perform(get("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("StarCoder")))
                .andExpect(jsonPath("$.realName", is("Ada Lovelace")))
                .andExpect(jsonPath("$.seniority", is("MASS_OF_A_STAR")));
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/space-devs ---

    @Test
    void create_returnsCreatedDev() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("NebulaNinja");
        dev.setRealName("Alan Turing");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setDebuggingPowerLevel(5000);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.callSign", is("NebulaNinja")))
                .andExpect(jsonPath("$.realName", is("Alan Turing")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void create_rejectsBlankCallSign() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("");
        dev.setRealName("Alan Turing");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsBlankRealName() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("Ninja");
        dev.setRealName("");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsNullSeniority() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("Ninja");
        dev.setRealName("Alan Turing");
        // seniority intentionally null

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsDebuggingPowerLevelAbove9001() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("Ninja");
        dev.setRealName("Alan Turing");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setDebuggingPowerLevel(9002);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsCoffeesPerDayAbove99() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("Ninja");
        dev.setRealName("Alan Turing");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setCoffeesPerDayInLiters(100);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /api/space-devs/{id} ---

    @Test
    void update_returnsUpdatedDev() throws Exception {
        savedDev.setCallSign("UpdatedCoder");
        savedDev.setRealName("Updated Name");

        mockMvc.perform(put("/api/space-devs/{id}", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("UpdatedCoder")))
                .andExpect(jsonPath("$.realName", is("Updated Name")));
    }

    @Test
    void update_returns404WhenNotFound() throws Exception {
        savedDev.setCallSign("UpdatedCoder");

        mockMvc.perform(put("/api/space-devs/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/space-devs/{id} ---

    @Test
    void delete_removesExistingDev() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // --- GET /api/space-devs/search?callSign=X ---

    @Test
    void search_returnMatchingDevsByCallSign() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "Star"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("StarCoder")));
    }

    @Test
    void search_isCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "star"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_returnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "xyznotexist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/space-devs/random-joke ---

    @Test
    void randomJoke_returnsJokeFromDevs() throws Exception {
        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    void randomJoke_returnsFallbackJokeWhenNoJokesExist() throws Exception {
        missionRepo.deleteAll();
        devRepo.deleteAll();

        // Create a dev with no joke
        var dev = new SpaceDeveloper();
        dev.setCallSign("NoJokeDev");
        dev.setRealName("Humorless Hal");
        dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);
        devRepo.save(dev);

        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Java")));
    }
}
