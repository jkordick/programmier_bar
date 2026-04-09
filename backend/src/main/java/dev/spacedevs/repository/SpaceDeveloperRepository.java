package dev.spacedevs.repository;

import dev.spacedevs.model.SpaceDeveloper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceDeveloperRepository extends JpaRepository<SpaceDeveloper, Long>, JpaSpecificationExecutor<SpaceDeveloper> {
    List<SpaceDeveloper> findBySkillsContaining(String skill);
    List<SpaceDeveloper> findByCallSignContainingIgnoreCase(String callSign);
}
