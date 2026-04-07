package dev.spacedevs.controller;

import dev.spacedevs.model.Mission;
import dev.spacedevs.repository.MissionRepository;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/space-devs/{devId}/missions")
@CrossOrigin(origins = "http://localhost:5173")
public class MissionController {

    private final MissionRepository missionRepository;
    private final SpaceDeveloperRepository spaceDeveloperRepository;

    public MissionController(MissionRepository missionRepository, SpaceDeveloperRepository spaceDeveloperRepository) {
        this.missionRepository = missionRepository;
        this.spaceDeveloperRepository = spaceDeveloperRepository;
    }

    @GetMapping
    public ResponseEntity<List<Mission>> getMissions(@PathVariable Long devId) {
        if (!spaceDeveloperRepository.existsById(devId)) {
            return ResponseEntity.notFound().build();
        }
        List<Mission> missions = missionRepository.findBySpaceDeveloperIdOrderByDateDesc(devId);
        return ResponseEntity.ok(missions);
    }

    @PostMapping
    public ResponseEntity<Mission> createMission(@PathVariable Long devId, @Valid @RequestBody Mission mission) {
        return spaceDeveloperRepository.findById(devId)
                .map(dev -> {
                    mission.setId(null);
                    mission.setSpaceDeveloper(dev);
                    Mission saved = missionRepository.save(mission);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{missionId}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long devId, @PathVariable Long missionId) {
        if (!spaceDeveloperRepository.existsById(devId)) {
            return ResponseEntity.notFound().build();
        }
        return missionRepository.findById(missionId)
                .filter(m -> m.getSpaceDeveloper().getId().equals(devId))
                .map(m -> {
                    missionRepository.delete(m);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
