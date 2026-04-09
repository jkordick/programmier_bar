package dev.spacedevs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.spacedevs.model.Crew;
import dev.spacedevs.model.Seniority;
import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.CrewRepository;
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
class CrewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrewRepository crewRepo;

    @Autowired
    private SpaceDeveloperRepository devRepo;

    @Autowired
    private MissionRepository missionRepo;

    private SpaceDeveloper savedDev;

    @BeforeEach
    void setUp() {
        missionRepo.deleteAll();
        // Un-assign all devs from crews before deleting crews
        devRepo.findAll().forEach(dev -> {
            dev.setCrew(null);
            devRepo.save(dev);
        });
        crewRepo.deleteAll();
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
    void getAll_returnsEmptyListWhenNoCrews() throws Exception {
        mockMvc.perform(get("/api/crews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void create_returnsCreatedCrew() throws Exception {
        var crew = new Crew();
        crew.setName("Test Crew");
        crew.setMissionStatement("Test all the things");
        crew.setShipName("The Tester");

        mockMvc.perform(post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crew)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Crew")))
                .andExpect(jsonPath("$.missionStatement", is("Test all the things")))
                .andExpect(jsonPath("$.shipName", is("The Tester")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.members", hasSize(0)));
    }

    @Test
    void create_rejectsBlankName() throws Exception {
        var crew = new Crew();
        crew.setName("");
        crew.setMissionStatement("No name");

        mockMvc.perform(post("/api/crews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crew)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsCrewWithMembers() throws Exception {
        var crew = new Crew();
        crew.setName("Detail Crew");
        crew.setMissionStatement("Show details");
        crew.setShipName("Detail Ship");
        crew = crewRepo.save(crew);

        savedDev.setCrew(crew);
        devRepo.save(savedDev);

        mockMvc.perform(get("/api/crews/{id}", crew.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Detail Crew")))
                .andExpect(jsonPath("$.members", hasSize(1)))
                .andExpect(jsonPath("$.members[0].callSign", is("TestPilot")));
    }

    @Test
    void getById_returns404ForNonexistentCrew() throws Exception {
        mockMvc.perform(get("/api/crews/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesCrewFields() throws Exception {
        var crew = new Crew();
        crew.setName("Original");
        crew.setMissionStatement("Original mission");
        crew.setShipName("Original ship");
        crew = crewRepo.save(crew);

        var updated = new Crew();
        updated.setName("Updated");
        updated.setMissionStatement("Updated mission");
        updated.setShipName("Updated ship");

        mockMvc.perform(put("/api/crews/{id}", crew.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated")))
                .andExpect(jsonPath("$.missionStatement", is("Updated mission")))
                .andExpect(jsonPath("$.shipName", is("Updated ship")));
    }

    @Test
    void delete_removesCrewAndUnassignsMembers() throws Exception {
        var crew = new Crew();
        crew.setName("To Delete");
        crew = crewRepo.save(crew);

        savedDev.setCrew(crew);
        devRepo.save(savedDev);

        mockMvc.perform(delete("/api/crews/{id}", crew.getId()))
                .andExpect(status().isNoContent());

        // Crew should be gone
        mockMvc.perform(get("/api/crews/{id}", crew.getId()))
                .andExpect(status().isNotFound());

        // Dev should still exist but with no crew
        var dev = devRepo.findById(savedDev.getId()).orElseThrow();
        assert dev.getCrew() == null;
    }

    @Test
    void addMember_assignsDevToCrew() throws Exception {
        var crew = new Crew();
        crew.setName("Join Crew");
        crew = crewRepo.save(crew);

        mockMvc.perform(post("/api/crews/{id}/members/{devId}", crew.getId(), savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members", hasSize(1)))
                .andExpect(jsonPath("$.members[0].callSign", is("TestPilot")));
    }

    @Test
    void addMember_returns404ForNonexistentCrew() throws Exception {
        mockMvc.perform(post("/api/crews/{id}/members/{devId}", 99999, savedDev.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMember_returns404ForNonexistentDev() throws Exception {
        var crew = new Crew();
        crew.setName("Lonely Crew");
        crew = crewRepo.save(crew);

        mockMvc.perform(post("/api/crews/{id}/members/{devId}", crew.getId(), 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void addMember_returnsConflictWhenDevAlreadyInCrew() throws Exception {
        var crew1 = new Crew();
        crew1.setName("Crew 1");
        crew1 = crewRepo.save(crew1);

        var crew2 = new Crew();
        crew2.setName("Crew 2");
        crew2 = crewRepo.save(crew2);

        savedDev.setCrew(crew1);
        devRepo.save(savedDev);

        mockMvc.perform(post("/api/crews/{id}/members/{devId}", crew2.getId(), savedDev.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void addMember_returnsConflictWhenCrewIsFull() throws Exception {
        var crew = new Crew();
        crew.setName("Full Crew");
        crew = crewRepo.save(crew);

        // Add 6 members
        for (int i = 0; i < 6; i++) {
            var dev = new SpaceDeveloper();
            dev.setCallSign("Member" + i);
            dev.setRealName("Real " + i);
            dev.setSeniority(Seniority.MASS_OF_SPACE_DUST);
            dev.setCrew(crew);
            devRepo.save(dev);
        }

        mockMvc.perform(post("/api/crews/{id}/members/{devId}", crew.getId(), savedDev.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    void removeMember_unassignsDevFromCrew() throws Exception {
        var crew = new Crew();
        crew.setName("Leave Crew");
        crew = crewRepo.save(crew);

        savedDev.setCrew(crew);
        devRepo.save(savedDev);

        mockMvc.perform(delete("/api/crews/{id}/members/{devId}", crew.getId(), savedDev.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members", hasSize(0)));
    }

    @Test
    void removeMember_returns404WhenDevNotInCrew() throws Exception {
        var crew = new Crew();
        crew.setName("Not My Crew");
        crew = crewRepo.save(crew);

        mockMvc.perform(delete("/api/crews/{id}/members/{devId}", crew.getId(), savedDev.getId()))
                .andExpect(status().isNotFound());
    }
}
