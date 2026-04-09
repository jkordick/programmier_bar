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
        dev.setRealName("Jane Galaxy");
        dev.setDebuggingPowerLevel(4200);
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setShipName("The Null Pointer");
        dev.setSkills(List.of("Java", "TypeScript"));
        dev.setCoffeesPerDayInLiters(3);
        savedDev = devRepo.save(dev);
    }

    // --- GET /api/space-devs ---

    @Test
    void getAll_returnsListOfDevelopers() throws Exception {
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
                .andExpect(jsonPath("$.realName", is("Jane Galaxy")))
                .andExpect(jsonPath("$.debuggingPowerLevel", is(4200)));
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
        dev.setRealName("Alex Starfield");
        dev.setSeniority(Seniority.MASS_OF_A_MOON);
        dev.setDebuggingPowerLevel(1337);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.callSign", is("NebulaNinja")))
                .andExpect(jsonPath("$.realName", is("Alex Starfield")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void create_rejectsBlankCallSign() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("");
        dev.setRealName("Alex Starfield");
        dev.setSeniority(Seniority.MASS_OF_A_MOON);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsBlankRealName() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("NebulaNinja");
        dev.setRealName("");
        dev.setSeniority(Seniority.MASS_OF_A_MOON);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsDebuggingPowerLevelAbove9001() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("OverPowered");
        dev.setRealName("Max Power");
        dev.setSeniority(Seniority.MASS_OF_THE_UNIVERSE);
        dev.setDebuggingPowerLevel(9002);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsCoffeesPerDayAbove99() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("CaffeineKing");
        dev.setRealName("Joe Java");
        dev.setSeniority(Seniority.MASS_OF_A_STAR);
        dev.setCoffeesPerDayInLiters(100);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ignoresProvidedId() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setId(99999L);
        dev.setCallSign("IdIgnored");
        dev.setRealName("Some Dev");
        dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(99999)));
    }

    // --- PUT /api/space-devs/{id} ---

    @Test
    void update_returnsUpdatedDev() throws Exception {
        savedDev.setCallSign("UpdatedSign");
        savedDev.setRealName("Updated Name");

        mockMvc.perform(put("/api/space-devs/{id}", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("UpdatedSign")))
                .andExpect(jsonPath("$.realName", is("Updated Name")));
    }

    @Test
    void update_returns404WhenNotFound() throws Exception {
        savedDev.setCallSign("Phantom");

        mockMvc.perform(put("/api/space-devs/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_rejectsInvalidBody() throws Exception {
        savedDev.setCallSign("");

        mockMvc.perform(put("/api/space-devs/{id}", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isBadRequest());
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

    // --- GET /api/space-devs/search ---

    @Test
    void search_returnsMatchingDevsByCallSign() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "star"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("StarCoder")));
    }

    @Test
    void search_isCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "STARCODER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_returnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "zzznomatch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- GET /api/space-devs/random-joke ---

    @Test
    void randomJoke_returnsDefaultJokeWhenNoJokesExist() throws Exception {
        // savedDev has no favoriteDevJoke, so default joke is returned
        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Java")));
    }

    @Test
    void randomJoke_returnsJokeFromExistingDevs() throws Exception {
        savedDev.setFavoriteDevJoke("Why do programmers prefer dark mode? Because light attracts bugs!");
        devRepo.save(savedDev);

        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }
}
