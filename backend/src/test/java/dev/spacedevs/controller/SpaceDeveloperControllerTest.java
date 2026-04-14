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

    @BeforeEach
    void setUp() {
        missionRepo.deleteAll();
        devRepo.deleteAll();
    }

    // ── GET /api/space-devs ───────────────────────────────────────────────────

    @Test
    void getAll_returnsEmptyListWhenNoneExist() throws Exception {
        mockMvc.perform(get("/api/space-devs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAll_returnsAllSavedDevs() throws Exception {
        saveMinimalDev("Alpha", "Alice A");
        saveMinimalDev("Bravo", "Bob B");

        mockMvc.perform(get("/api/space-devs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].callSign", containsInAnyOrder("Alpha", "Bravo")));
    }

    // ── GET /api/space-devs/{id} ──────────────────────────────────────────────

    @Test
    void getById_returnsDevWhenFound() throws Exception {
        SpaceDeveloper dev = saveMinimalDev("GammaDev", "Grace G");

        mockMvc.perform(get("/api/space-devs/{id}", dev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("GammaDev")))
                .andExpect(jsonPath("$.realName", is("Grace G")));
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/space-devs ──────────────────────────────────────────────────

    @Test
    void create_returnsCreatedDev() throws Exception {
        SpaceDeveloper dev = buildDev("NewPilot", "Neil N");

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.callSign", is("NewPilot")))
                .andExpect(jsonPath("$.seniority", is("MASS_OF_A_MOON")));
    }

    @Test
    void create_rejectsBlankCallSign() throws Exception {
        SpaceDeveloper dev = buildDev("", "Neil N");

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsBlankRealName() throws Exception {
        SpaceDeveloper dev = buildDev("CallSign", "");

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsCoffeesPerDayAbove99() throws Exception {
        SpaceDeveloper dev = buildDev("CaffeineKing", "Carl C");
        dev.setCoffeesPerDayInLiters(100);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsCoffeesPerDayBelowZero() throws Exception {
        SpaceDeveloper dev = buildDev("TeaDrinker", "Tara T");
        dev.setCoffeesPerDayInLiters(-1);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsDebuggingPowerLevelAbove9001() throws Exception {
        SpaceDeveloper dev = buildDev("OverNineThousand", "Owen O");
        dev.setDebuggingPowerLevel(9002);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_acceptsDebuggingPowerLevelAt9001() throws Exception {
        SpaceDeveloper dev = buildDev("MaxPower", "Max M");
        dev.setDebuggingPowerLevel(9001);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.debuggingPowerLevel", is(9001)));
    }

    // ── PUT /api/space-devs/{id} ──────────────────────────────────────────────

    @Test
    void update_returnsUpdatedDev() throws Exception {
        SpaceDeveloper saved = saveMinimalDev("OldSign", "Old Name");

        SpaceDeveloper updated = buildDev("NewSign", "New Name");

        mockMvc.perform(put("/api/space-devs/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("NewSign")))
                .andExpect(jsonPath("$.realName", is("New Name")));
    }

    @Test
    void update_returns404WhenNotFound() throws Exception {
        SpaceDeveloper dev = buildDev("Ghost", "No One");

        mockMvc.perform(put("/api/space-devs/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_rejectsInvalidBody() throws Exception {
        SpaceDeveloper saved = saveMinimalDev("Valid", "Valid Name");
        SpaceDeveloper invalid = buildDev("", ""); // blank callSign and realName

        mockMvc.perform(put("/api/space-devs/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/space-devs/{id} ───────────────────────────────────────────

    @Test
    void delete_removesExistingDev() throws Exception {
        SpaceDeveloper saved = saveMinimalDev("ToDelete", "Delete Me");

        mockMvc.perform(delete("/api/space-devs/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/space-devs/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns404WhenNotFound() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/space-devs/search ────────────────────────────────────────────

    @Test
    void search_returnsMatchingDevsByCallSign() throws Exception {
        saveMinimalDev("NebulaNinja", "Nina N");
        saveMinimalDev("StarSlayer", "Stan S");

        mockMvc.perform(get("/api/space-devs/search").param("callSign", "nebula"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("NebulaNinja")));
    }

    @Test
    void search_returnsEmptyListWhenNoMatch() throws Exception {
        saveMinimalDev("AlphaCentauri", "Alice A");

        mockMvc.perform(get("/api/space-devs/search").param("callSign", "zzzunknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void search_isCaseInsensitive() throws Exception {
        saveMinimalDev("GalaxyGuru", "Gary G");

        mockMvc.perform(get("/api/space-devs/search").param("callSign", "GALAXY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ── GET /api/space-devs/random-joke ──────────────────────────────────────

    @Test
    void randomJoke_returnsDefaultJokeWhenNoDevsHaveJokes() throws Exception {
        saveMinimalDev("Humorless", "Han H"); // no joke set

        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())));
    }

    @Test
    void randomJoke_returnsDevJokeWhenPresent() throws Exception {
        SpaceDeveloper dev = buildDev("JokeMaster", "Johnny J");
        dev.setFavoriteDevJoke("Why do Java developers wear glasses? Because they can't C#.");
        devRepo.save(dev);

        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SpaceDeveloper saveMinimalDev(String callSign, String realName) {
        return devRepo.save(buildDev(callSign, realName));
    }

    private SpaceDeveloper buildDev(String callSign, String realName) {
        SpaceDeveloper dev = new SpaceDeveloper();
        dev.setCallSign(callSign);
        dev.setRealName(realName);
        dev.setSeniority(Seniority.MASS_OF_A_MOON);
        dev.setDebuggingPowerLevel(42);
        dev.setShipName("Test Ship");
        return dev;
    }
}
