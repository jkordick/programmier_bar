package dev.spacedevs.controller;

import dev.spacedevs.model.Crew;
import dev.spacedevs.repository.CrewRepository;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crews")
@CrossOrigin(origins = "http://localhost:5173")
public class CrewController {

    private static final int MAX_CREW_SIZE = 6;

    private final CrewRepository crewRepository;
    private final SpaceDeveloperRepository spaceDeveloperRepository;

    public CrewController(CrewRepository crewRepository, SpaceDeveloperRepository spaceDeveloperRepository) {
        this.crewRepository = crewRepository;
        this.spaceDeveloperRepository = spaceDeveloperRepository;
    }

    @GetMapping
    public List<Crew> getAll() {
        return crewRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crew> getById(@PathVariable Long id) {
        return crewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Crew> create(@Valid @RequestBody Crew crew) {
        crew.setId(null);
        crew.setMembers(List.of());
        Crew saved = crewRepository.save(crew);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crew> update(@PathVariable Long id, @Valid @RequestBody Crew crew) {
        return crewRepository.findById(id)
                .map(existing -> {
                    existing.setName(crew.getName());
                    existing.setMissionStatement(crew.getMissionStatement());
                    existing.setShipName(crew.getShipName());
                    return ResponseEntity.ok(crewRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return crewRepository.findById(id)
                .map(crew -> {
                    // Un-assign all members before deleting
                    crew.getMembers().forEach(dev -> dev.setCrew(null));
                    spaceDeveloperRepository.saveAll(crew.getMembers());
                    crewRepository.delete(crew);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/members/{devId}")
    public ResponseEntity<Crew> addMember(@PathVariable Long id, @PathVariable Long devId) {
        var crewOpt = crewRepository.findById(id);
        if (crewOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var crew = crewOpt.get();

        return spaceDeveloperRepository.findById(devId)
                .map(dev -> {
                    if (dev.getCrew() != null) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).<Crew>build();
                    }
                    if (crew.getMembers().size() >= MAX_CREW_SIZE) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).<Crew>build();
                    }
                    dev.setCrew(crew);
                    spaceDeveloperRepository.save(dev);
                    crew.getMembers().add(dev);
                    return ResponseEntity.ok(crew);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/members/{devId}")
    public ResponseEntity<Crew> removeMember(@PathVariable Long id, @PathVariable Long devId) {
        var crewOpt = crewRepository.findById(id);
        if (crewOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var crew = crewOpt.get();

        return spaceDeveloperRepository.findById(devId)
                .filter(dev -> dev.getCrew() != null && dev.getCrew().getId().equals(id))
                .map(dev -> {
                    dev.setCrew(null);
                    spaceDeveloperRepository.save(dev);
                    crew.getMembers().removeIf(m -> m.getId().equals(devId));
                    return ResponseEntity.ok(crew);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
