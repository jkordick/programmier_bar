package dev.spacedevs.controller;

import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import dev.spacedevs.service.CallSignGeneratorService;
import dev.spacedevs.service.JokeGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/space-devs")
@CrossOrigin(origins = "http://localhost:5173")
public class SpaceDeveloperController {

    private final SpaceDeveloperRepository repository;
    private final JokeGeneratorService jokeGenerator;
    private final CallSignGeneratorService callSignGenerator;

    public SpaceDeveloperController(SpaceDeveloperRepository repository,
                                    JokeGeneratorService jokeGenerator,
                                    CallSignGeneratorService callSignGenerator) {
        this.repository = repository;
        this.jokeGenerator = jokeGenerator;
        this.callSignGenerator = callSignGenerator;
    }

    @GetMapping
    public List<SpaceDeveloper> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceDeveloper> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SpaceDeveloper> create(@Valid @RequestBody SpaceDeveloper dev) {
        dev.setId(null);
        SpaceDeveloper saved = repository.save(dev);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDeveloper> update(@PathVariable Long id, @Valid @RequestBody SpaceDeveloper dev) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dev.setId(id);
        return ResponseEntity.ok(repository.save(dev));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<SpaceDeveloper> search(@RequestParam String callSign) {
        return repository.findByCallSignContainingIgnoreCase(callSign);
    }

    @GetMapping("/random-joke")
    public ResponseEntity<String> randomJoke() {
        List<SpaceDeveloper> all = repository.findAll();
        List<String> jokes = all.stream()
                .map(SpaceDeveloper::getFavoriteDevJoke)
                .filter(j -> j != null && !j.isBlank())
                .toList();
        if (jokes.isEmpty()) {
            return ResponseEntity.ok("Why do Java developers wear glasses? Because they can't C#.");
        }
        return ResponseEntity.ok(jokes.get((int) (Math.random() * jokes.size())));
    }

    @GetMapping("/generate-joke")
    public ResponseEntity<String> generateJoke(@RequestParam String callSign, @RequestParam String skills) {
        String joke = jokeGenerator.generateJoke(callSign, skills);
        return ResponseEntity.ok(joke);
    }

    @PostMapping("/generate-call-sign")
    public ResponseEntity<String> generateCallSign(@RequestBody CallSignRequest request) {
        String callSign = callSignGenerator.generateCallSign(request.skills(), request.seniority());
        return ResponseEntity.ok(callSign);
    }

    public record CallSignRequest(List<String> skills, String seniority) {}
}
