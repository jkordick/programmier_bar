package dev.spacedevs.controller;

import dev.spacedevs.model.Seniority;
import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import dev.spacedevs.service.JokeGeneratorService;
import jakarta.persistence.criteria.JoinType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/space-devs")
@CrossOrigin(origins = "http://localhost:5173")
public class SpaceDeveloperController {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "debuggingPowerLevel", "coffeesPerDayInLiters", "gitCommitStreak", "stackOverflowReputation"
    );

    private final SpaceDeveloperRepository repository;
    private final JokeGeneratorService jokeGenerator;

    public SpaceDeveloperController(SpaceDeveloperRepository repository, JokeGeneratorService jokeGenerator) {
        this.repository = repository;
        this.jokeGenerator = jokeGenerator;
    }

    @GetMapping
    public List<SpaceDeveloper> getAll(
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false) List<String> seniority,
            @RequestParam(required = false) String skill) {

        boolean hasFilters = sortBy != null || (seniority != null && !seniority.isEmpty()) || (skill != null && !skill.isBlank());

        if (!hasFilters) {
            return repository.findAll();
        }

        Specification<SpaceDeveloper> spec = Specification.where(null);

        if (seniority != null && !seniority.isEmpty()) {
            List<Seniority> seniorityEnums = seniority.stream()
                    .map(s -> {
                        try {
                            return Seniority.valueOf(s);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            if (!seniorityEnums.isEmpty()) {
                spec = spec.and((root, query, cb) -> root.get("seniority").in(seniorityEnums));
            }
        }

        if (skill != null && !skill.isBlank()) {
            spec = spec.and((root, query, cb) -> {
                var skillJoin = root.join("skills", JoinType.INNER);
                query.distinct(true);
                return cb.like(cb.lower(skillJoin.as(String.class)), "%" + skill.toLowerCase() + "%");
            });
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && SORTABLE_FIELDS.contains(sortBy)) {
            sort = "asc".equalsIgnoreCase(order) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        }

        return repository.findAll(spec, sort);
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
}
