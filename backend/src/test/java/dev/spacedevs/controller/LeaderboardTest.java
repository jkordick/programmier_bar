package dev.spacedevs.controller;

import dev.spacedevs.model.Seniority;
import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.MissionRepository;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeaderboardTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpaceDeveloperRepository devRepo;

    @Autowired
    private MissionRepository missionRepo;

    @BeforeEach
    void setUp() {
        missionRepo.deleteAll();
        devRepo.deleteAll();

        var dev1 = new SpaceDeveloper();
        dev1.setCallSign("AlphaOne");
        dev1.setRealName("Alpha Tester");
        dev1.setSeniority(Seniority.MASS_OF_A_STAR);
        dev1.setDebuggingPowerLevel(9000);
        dev1.setCoffeesPerDayInLiters(5);
        dev1.setGitCommitStreak(100);
        dev1.setStackOverflowReputation(50000);
        dev1.setSkills(List.of("Java", "Python"));
        devRepo.save(dev1);

        var dev2 = new SpaceDeveloper();
        dev2.setCallSign("BetaTwo");
        dev2.setRealName("Beta Tester");
        dev2.setSeniority(Seniority.MASS_OF_A_PLANET);
        dev2.setDebuggingPowerLevel(3000);
        dev2.setCoffeesPerDayInLiters(10);
        dev2.setGitCommitStreak(50);
        dev2.setStackOverflowReputation(80000);
        dev2.setSkills(List.of("JavaScript", "TypeScript"));
        devRepo.save(dev2);

        var dev3 = new SpaceDeveloper();
        dev3.setCallSign("GammaThree");
        dev3.setRealName("Gamma Tester");
        dev3.setSeniority(Seniority.MASS_OF_A_STAR);
        dev3.setDebuggingPowerLevel(7000);
        dev3.setCoffeesPerDayInLiters(2);
        dev3.setGitCommitStreak(200);
        dev3.setStackOverflowReputation(30000);
        dev3.setSkills(List.of("Java", "Rust"));
        devRepo.save(dev3);
    }

    @Test
    void getAll_withoutParams_returnsAllDevs() throws Exception {
        mockMvc.perform(get("/api/space-devs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAll_sortByDebuggingPowerLevel_desc() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "debuggingPowerLevel")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].callSign", is("AlphaOne")))
                .andExpect(jsonPath("$[1].callSign", is("GammaThree")))
                .andExpect(jsonPath("$[2].callSign", is("BetaTwo")));
    }

    @Test
    void getAll_sortByDebuggingPowerLevel_asc() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "debuggingPowerLevel")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].callSign", is("BetaTwo")))
                .andExpect(jsonPath("$[1].callSign", is("GammaThree")))
                .andExpect(jsonPath("$[2].callSign", is("AlphaOne")));
    }

    @Test
    void getAll_sortByCoffeesPerDayInLiters_desc() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "coffeesPerDayInLiters")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].callSign", is("BetaTwo")))
                .andExpect(jsonPath("$[2].callSign", is("GammaThree")));
    }

    @Test
    void getAll_sortByGitCommitStreak_desc() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "gitCommitStreak")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].callSign", is("GammaThree")))
                .andExpect(jsonPath("$[1].callSign", is("AlphaOne")))
                .andExpect(jsonPath("$[2].callSign", is("BetaTwo")));
    }

    @Test
    void getAll_sortByStackOverflowReputation_desc() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "stackOverflowReputation")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].callSign", is("BetaTwo")))
                .andExpect(jsonPath("$[1].callSign", is("AlphaOne")))
                .andExpect(jsonPath("$[2].callSign", is("GammaThree")));
    }

    @Test
    void getAll_filterBySeniority() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("seniority", "MASS_OF_A_STAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].callSign", containsInAnyOrder("AlphaOne", "GammaThree")));
    }

    @Test
    void getAll_filterByMultipleSeniority() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("seniority", "MASS_OF_A_STAR")
                        .param("seniority", "MASS_OF_A_PLANET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAll_filterBySkill() throws Exception {
        // "Java" matches both "Java" and "JavaScript" via text search
        mockMvc.perform(get("/api/space-devs")
                        .param("skill", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAll_filterBySkill_caseInsensitive() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("skill", "java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAll_filterBySkill_exactMatch() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("skill", "Rust"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("GammaThree")));
    }

    @Test
    void getAll_combinedFilterAndSort() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("seniority", "MASS_OF_A_STAR")
                        .param("sortBy", "debuggingPowerLevel")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].callSign", is("AlphaOne")))
                .andExpect(jsonPath("$[1].callSign", is("GammaThree")));
    }

    @Test
    void getAll_filterReturnsEmptyWhenNoMatch() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("seniority", "MASS_OF_SPACE_DUST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAll_filterBySkillAndSeniority() throws Exception {
        // "Java" matches "Java" and "JavaScript"; combined with MASS_OF_A_STAR keeps AlphaOne and GammaThree
        mockMvc.perform(get("/api/space-devs")
                        .param("skill", "Rust")
                        .param("seniority", "MASS_OF_A_STAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].callSign", is("GammaThree")));
    }

    @Test
    void getAll_invalidSortByField_ignoredReturnsUnsorted() throws Exception {
        mockMvc.perform(get("/api/space-devs")
                        .param("sortBy", "invalidField")
                        .param("seniority", "MASS_OF_A_STAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
