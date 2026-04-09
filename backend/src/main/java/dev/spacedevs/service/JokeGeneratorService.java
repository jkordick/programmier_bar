package dev.spacedevs.service;

import com.github.copilot.sdk.CopilotClient;
import com.github.copilot.sdk.events.AssistantMessageEvent;
import com.github.copilot.sdk.json.MessageOptions;
import com.github.copilot.sdk.json.SessionConfig;
import com.github.copilot.sdk.json.PermissionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JokeGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(JokeGeneratorService.class);

    public String generateJoke(String callSign, String skills) {
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
                    log.debug("Received message: {}", content);
                }
            });

            String prompt = String.format(
                "Generate a single short, funny developer joke for a space developer " +
                "with call sign '%s' who knows %s. " +
                "The joke should be nerdy, space-themed, and related to their skills. " +
                "Be creative and unique — do NOT repeat previous jokes. Seed: %d. " +
                "Return ONLY the joke text, nothing else. No quotes around it.",
                callSign, skills, System.nanoTime()
            );

            session.sendAndWait(new MessageOptions().setPrompt(prompt)).get();

            if (lastMessage[0] != null) {
                return lastMessage[0];
            }
            log.warn("No message received from Copilot SDK");
            return "The joke nebula is empty right now. Try again! 🌌";
        } catch (Exception e) {
            log.error("Failed to generate joke via Copilot SDK", e);
            return "Why did the developer's joke fail to compile? Because the AI was on a coffee break. ☕🚀";
        }
    }
}
