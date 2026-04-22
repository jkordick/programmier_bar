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
        dev.setCallSign("NebulaNinja");
        dev.setRealName("Alex Starfield");
        dev.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev.setDebuggingPowerLevel(4200);
        dev.setShipName("Millennium Falsy");
        dev.setSkills(List.of("Java", "TypeScript"));
        savedDev = devRepo.save(dev);
    }

    @Test
    void getAll_returnsListOfDevelopers() throws Exception {
        mockMvc.perform(get("/api/space-devs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].callSign", is("NebulaNinja")));
    }

    @Test
    void getById_returnsExistingDeveloper() throws Exception {
        mockMvc.perform(get("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("NebulaNinja")))
                .andExpect(jsonPath("$.realName", is("Alex Starfield")))
                .andExpect(jsonPath("$.seniority", is("MASS_OF_A_PLANET")));
    }

    @Test
    void getById_returns404ForNonexistentDeveloper() throws Exception {
        mockMvc.perform(get("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreatedDeveloper() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("StarSurfer");
        dev.setRealName("Zara Nova");
        dev.setSeniority(Seniority.MASS_OF_A_MOON);
        dev.setDebuggingPowerLevel(1500);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.callSign", is("StarSurfer")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void create_rejectsBlankCallSign() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("");
        dev.setRealName("Valid Name");
        dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsBlankRealName() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("ValidSign");
        dev.setRealName("");
        dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsNullSeniority() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("ValidSign");
        dev.setRealName("Valid Name");
        // seniority left null

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsCoffeesPerDayAboveMax() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("CoffeeLover");
        dev.setRealName("Java Bean");
        dev.setSeniority(Seniority.MASS_OF_A_STAR);
        dev.setCoffeesPerDayInLiters(100);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_rejectsDebuggingPowerLevelAboveMax() throws Exception {
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
    void update_returnsUpdatedDeveloper() throws Exception {
        savedDev.setCallSign("UpdatedNinja");
        savedDev.setDebuggingPowerLevel(9001);

        mockMvc.perform(put("/api/space-devs/{id}", savedDev.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDev)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callSign", is("UpdatedNinja")))
                .andExpect(jsonPath("$.debuggingPowerLevel", is(9001)));
    }

    @Test
    void update_returns404ForNonexistentDeveloper() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setCallSign("Ghost");
        dev.setRealName("Nobody");
        dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);

        mockMvc.perform(put("/api/space-devs/{id}", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_removesExistingDeveloper() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/space-devs/{id}", savedDev.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returns404ForNonexistentDeveloper() throws Exception {
        mockMvc.perform(delete("/api/space-devs/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void search_returnsMatchingDevelopers() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "nebula"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("NebulaNinja")));
    }

    @Test
    void search_returnsCaseInsensitiveMatches() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "NEBULA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void search_returnsEmptyListWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/space-devs/search").param("callSign", "xyznonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void randomJoke_returnsFallbackWhenNoDevelopersHaveJokes() throws Exception {
        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    void randomJoke_returnsJokeFromDeveloperWithJoke() throws Exception {
        savedDev.setFavoriteDevJoke("Why do Java developers wear glasses? Because they can't C#.");
        devRepo.save(savedDev);

        mockMvc.perform(get("/api/space-devs/random-joke"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Java")));
    }

    @Test
    void create_ignoresClientProvidedId() throws Exception {
        var dev = new SpaceDeveloper();
        dev.setId(9999L);
        dev.setCallSign("ForcedId");
        dev.setRealName("Id Force");
        dev.setSeniority(Seniority.MASS_OF_AN_ASTEROID);

        mockMvc.perform(post("/api/space-devs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dev)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", not(9999)));
    }
}
