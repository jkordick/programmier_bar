package dev.spacedevs.service;

import com.github.copilot.sdk.CopilotClient;
import com.github.copilot.sdk.events.AssistantMessageEvent;
import com.github.copilot.sdk.json.MessageOptions;
import com.github.copilot.sdk.json.SessionConfig;
import com.github.copilot.sdk.json.PermissionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class CallSignGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(CallSignGeneratorService.class);

    private static final String[] SPACE_ADJECTIVES = {
        "Cosmic", "Stellar", "Orbital", "Nebular", "Galactic", "Quantum", "Solar",
        "Astral", "Lunar", "Radiant", "Hypersonic", "Interstellar", "Photonic",
        "Supernova", "Chromatic", "Blazing", "Silent", "Warp-Speed", "Dark-Matter",
        "Zero-G", "Oxidized", "Compiled", "Async", "Recursive", "Binary"
    };

    private static final String[] SPACE_NOUNS = {
        "Voyager", "Pioneer", "Navigator", "Ranger", "Comet", "Pulsar", "Quasar",
        "Phoenix", "Falcon", "Sentinel", "Architect", "Oracle", "Corsair",
        "Nebula", "Titan", "Horizon", "Vortex", "Eclipse", "Photon", "Catalyst"
    };

    private static final String[] SKILL_ADJECTIVES = {
        "Rusty", "Oxidized", "Caffeinated", "Threaded", "Dockerized",
        "Compiled", "Interpreted", "Reactive", "Functional", "Polymorphic",
        "Encrypted", "Distributed", "Stateless", "Immutable", "Concurrent"
    };

    private final Random random = new Random();

    public String generateCallSign(List<String> skills, String seniority) {
        try {
            return generateWithAI(skills, seniority);
        } catch (Exception e) {
            log.warn("AI call sign generation failed, using fallback", e);
            return generateFallback(skills);
        }
    }

    private String generateWithAI(List<String> skills, String seniority) throws Exception {
        var lastMessage = new String[]{null};

        try (var client = new CopilotClient()) {
            client.start().get();

            var session = client.createSession(
                new SessionConfig()
                    .setOnPermissionRequest(PermissionHandler.APPROVE_ALL)
                    .setModel("gpt-4.1")
            ).get();

            session.on(AssistantMessageEvent.class, msg -> {
                String content = msg.getData().content();
                if (content != null) {
                    lastMessage[0] = content;
                    log.debug("Received call sign: {}", content);
                }
            });

            String skillList = skills != null && !skills.isEmpty()
                ? String.join(", ", skills)
                : "general programming";

            String seniorityHint = seniority != null ? seniority.replace("_", " ").toLowerCase() : "";

            String prompt = String.format(
                "Generate a single creative, space-themed call sign for a developer " +
                "with skills in [%s] and seniority level '%s'. " +
                "The call sign should be 2-4 words, memorable, and blend space/sci-fi themes " +
                "with references to their tech skills. " +
                "Examples: 'The Oxidized Orbital', 'Quantum Rustacean', 'Async Nebula'. " +
                "Be creative and unique. Seed: %d. " +
                "Return ONLY the call sign text, nothing else. No quotes around it.",
                skillList, seniorityHint, System.nanoTime()
            );

            session.sendAndWait(new MessageOptions().setPrompt(prompt)).get();

            if (lastMessage[0] != null) {
                return lastMessage[0].trim();
            }
            log.warn("No message received from Copilot SDK for call sign generation");
            return generateFallback(skills);
        }
    }

    String generateFallback(List<String> skills) {
        String adjective;
        if (skills != null && !skills.isEmpty()) {
            adjective = SKILL_ADJECTIVES[Math.abs(skills.hashCode()) % SKILL_ADJECTIVES.length];
        } else {
            adjective = SPACE_ADJECTIVES[random.nextInt(SPACE_ADJECTIVES.length)];
        }
        String noun = SPACE_NOUNS[random.nextInt(SPACE_NOUNS.length)];
        return "The " + adjective + " " + noun;
    }
}
