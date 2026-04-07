package dev.spacedevs.config;

import dev.spacedevs.model.Seniority;
import dev.spacedevs.model.SpaceDeveloper;
import dev.spacedevs.repository.SpaceDeveloperRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(SpaceDeveloperRepository repo) {
        return args -> {
            var dev1 = new SpaceDeveloper();
            dev1.setCallSign("NebulaNinja");
            dev1.setRealName("Alex Starfield");
            dev1.setSeniority(Seniority.MASS_OF_A_STAR);
            dev1.setSkills(List.of("Java", "Spring Boot", "Kubernetes", "Telepathy"));
            dev1.setOssProjects(List.of("nebula-orm", "star-cli"));
            dev1.setFavoriteDevJoke("Why do Java devs wear glasses? Because they can't C#.");
            dev1.setCoffeesPerDayInLiters(7);
            dev1.setDebuggingPowerLevel(8500);
            dev1.setRubberDuckName("Quacksworth III");
            dev1.setFavoriteKeyboardShortcut("Ctrl+Z (my life philosophy)");
            dev1.setGitCommitStreak(365);
            dev1.setStackOverflowReputation(42000);
            dev1.setStillUsesVim(true);
            dev1.setShipName("USS Enterprise-D (Bug Edition)");

            var dev2 = new SpaceDeveloper();
            dev2.setCallSign("PixelPilot");
            dev2.setRealName("Sam Comet");
            dev2.setSeniority(Seniority.MASS_OF_A_PLANET);
            dev2.setSkills(List.of("React", "TypeScript", "CSS Wizardry", "Meme Engineering"));
            dev2.setOssProjects(List.of("pixel-ui", "comet-animations"));
            dev2.setFavoriteDevJoke("A CSS joke: I'm not sure why my humor doesn't display properly — probably a float issue.");
            dev2.setCoffeesPerDayInLiters(3);
            dev2.setDebuggingPowerLevel(7200);
            dev2.setRubberDuckName("Sir Ducks-a-Lot");
            dev2.setFavoriteKeyboardShortcut("Cmd+Shift+P (the portal to everything)");
            dev2.setGitCommitStreak(120);
            dev2.setStackOverflowReputation(15000);
            dev2.setStillUsesVim(false);
            dev2.setShipName("Millennium Falsy");

            var dev3 = new SpaceDeveloper();
            dev3.setCallSign("void_voyager");
            dev3.setRealName("Morgan Blackhole");
            dev3.setSeniority(Seniority.MASS_OF_THE_UNIVERSE);
            dev3.setSkills(List.of("Rust", "C++", "Assembly", "Staring at the Abyss", "Kernel Whispering"));
            dev3.setOssProjects(List.of("void-os", "gravity-db", "singularity-scheduler"));
            dev3.setFavoriteDevJoke("There are only 10 types of devs: those who understand binary and those who have mass overflow errors.");
            dev3.setCoffeesPerDayInLiters(42);
            dev3.setDebuggingPowerLevel(9001);
            dev3.setRubberDuckName("The Duckinator");
            dev3.setFavoriteKeyboardShortcut(":wq! (the only way to leave)");
            dev3.setGitCommitStreak(999);
            dev3.setStackOverflowReputation(200000);
            dev3.setStillUsesVim(true);
            dev3.setShipName("Event Horizon (no bugs escape)");

            var dev4 = new SpaceDeveloper();
            dev4.setCallSign("AstroByte");
            dev4.setRealName("Jordan Quasar");
            dev4.setSeniority(Seniority.MASS_OF_A_MOON);
            dev4.setSkills(List.of("Python", "Data Science", "Machine Learning", "Astro-debugging"));
            dev4.setOssProjects(List.of("quasar-ml"));
            dev4.setFavoriteDevJoke("Machine learning is just spicy if-statements.");
            dev4.setCoffeesPerDayInLiters(5);
            dev4.setDebuggingPowerLevel(6000);
            dev4.setRubberDuckName("Neural Duck");
            dev4.setFavoriteKeyboardShortcut("Tab (spaces are for mortals)");
            dev4.setGitCommitStreak(42);
            dev4.setStackOverflowReputation(8000);
            dev4.setStillUsesVim(false);
            dev4.setShipName("The Tensor Flow-rider");

            var dev5 = new SpaceDeveloper();
            dev5.setCallSign("CosmicIntern");
            dev5.setRealName("Riley Supernova");
            dev5.setSeniority(Seniority.MASS_OF_SPACE_DUST);
            dev5.setSkills(List.of("HTML", "Google-Fu", "Asking ChatGPT", "Coffee Fetching"));
            dev5.setOssProjects(List.of());
            dev5.setFavoriteDevJoke("My code works and I have no idea why. My code doesn't work and I have no idea why.");
            dev5.setCoffeesPerDayInLiters(1);
            dev5.setDebuggingPowerLevel(42);
            dev5.setRubberDuckName("Duckling Jr.");
            dev5.setFavoriteKeyboardShortcut("Ctrl+C Ctrl+V (the sacred combo)");
            dev5.setGitCommitStreak(3);
            dev5.setStackOverflowReputation(1);
            dev5.setStillUsesVim(false);
            dev5.setShipName("The Hello World");

            var dev6 = new SpaceDeveloper();
            dev6.setCallSign("GitGalactica");
            dev6.setRealName("Casey Pulsar");
            dev6.setSeniority(Seniority.MASS_OF_A_STAR);
            dev6.setSkills(List.of("Go", "Distributed Systems", "gRPC", "Yelling at YAML"));
            dev6.setOssProjects(List.of("pulsar-queue", "gitgalactica-sync", "config-hell"));
            dev6.setFavoriteDevJoke("A QA engineer walks into a bar. Orders 1 beer. Orders 0 beers. Orders 99999999 beers. Orders -1 beers. Orders a lizard.");
            dev6.setCoffeesPerDayInLiters(12);
            dev6.setDebuggingPowerLevel(8888);
            dev6.setRubberDuckName("Commander Quack");
            dev6.setFavoriteKeyboardShortcut("git rebase -i HEAD~42");
            dev6.setGitCommitStreak(730);
            dev6.setStackOverflowReputation(55000);
            dev6.setStillUsesVim(true);
            dev6.setShipName("The Rebase Station");

            var dev7 = new SpaceDeveloper();
            dev7.setCallSign("ZeroGravityZara");
            dev7.setRealName("Zara Eclipse");
            dev7.setSeniority(Seniority.MASS_OF_A_PLANET);
            dev7.setSkills(List.of("Kotlin", "Android", "Jetpack Compose", "Zero-G UI Design"));
            dev7.setOssProjects(List.of("eclipse-ui", "orbit-nav"));
            dev7.setFavoriteDevJoke("Why did the developer go broke? Because they used up all their cache.");
            dev7.setCoffeesPerDayInLiters(4);
            dev7.setDebuggingPowerLevel(7500);
            dev7.setRubberDuckName("Ducky McDuckface");
            dev7.setFavoriteKeyboardShortcut("Alt+Enter (IntelliJ is love)");
            dev7.setGitCommitStreak(200);
            dev7.setStackOverflowReputation(22000);
            dev7.setStillUsesVim(false);
            dev7.setShipName("The Null Pointer");

            var dev8 = new SpaceDeveloper();
            dev8.setCallSign("BinaryBard");
            dev8.setRealName("Robin Neutron");
            dev8.setSeniority(Seniority.MASS_OF_AN_ASTEROID);
            dev8.setSkills(List.of("Haskell", "Category Theory", "Monads", "Explaining Monads", "Failing to Explain Monads"));
            dev8.setOssProjects(List.of("monad-quest", "functor-fantasy"));
            dev8.setFavoriteDevJoke("A monad is just a monoid in the category of endofunctors, what's the problem?");
            dev8.setCoffeesPerDayInLiters(2);
            dev8.setDebuggingPowerLevel(4200);
            dev8.setRubberDuckName("Lambda Duck");
            dev8.setFavoriteKeyboardShortcut("M-x butterfly");
            dev8.setGitCommitStreak(77);
            dev8.setStackOverflowReputation(31337);
            dev8.setStillUsesVim(false);
            dev8.setShipName("The Functor");

            var dev9 = new SpaceDeveloper();
            dev9.setCallSign("DarkMatterDev");
            dev9.setRealName("Pat Andromeda");
            dev9.setSeniority(Seniority.MASS_OF_A_MOON);
            dev9.setSkills(List.of("DevOps", "Terraform", "Docker", "Kubernetes", "Pretending to Understand Networking"));
            dev9.setOssProjects(List.of("dark-deploy", "andromeda-infra"));
            dev9.setFavoriteDevJoke("It works on my machine. Then we'll ship your machine.");
            dev9.setCoffeesPerDayInLiters(8);
            dev9.setDebuggingPowerLevel(5500);
            dev9.setRubberDuckName("Containerized Duck");
            dev9.setFavoriteKeyboardShortcut("kubectl get pods --all-namespaces");
            dev9.setGitCommitStreak(150);
            dev9.setStackOverflowReputation(12000);
            dev9.setStillUsesVim(true);
            dev9.setShipName("The Pipeline Runner");

            var dev10 = new SpaceDeveloper();
            dev10.setCallSign("QuantumQoder");
            dev10.setRealName("Quinn Nebulae");
            dev10.setSeniority(Seniority.MASS_OF_AN_ASTEROID);
            dev10.setSkills(List.of("Quantum Computing", "Qiskit", "Linear Algebra", "Being in Two States at Once"));
            dev10.setOssProjects(List.of("qubit-sim"));
            dev10.setFavoriteDevJoke("Schrödinger's code: it both works and doesn't work until you run the tests.");
            dev10.setCoffeesPerDayInLiters(6);
            dev10.setDebuggingPowerLevel(3141);
            dev10.setRubberDuckName("Superposition Duck (may or may not exist)");
            dev10.setFavoriteKeyboardShortcut("Ctrl+Shift+Q (for quantum tunneling)");
            dev10.setGitCommitStreak(55);
            dev10.setStackOverflowReputation(5000);
            dev10.setStillUsesVim(false);
            dev10.setShipName("The Entangled Express");

            var dev11 = new SpaceDeveloper();
            dev11.setCallSign("AgentJules");
            dev11.setRealName("Julia Kordick");
            dev11.setSeniority(Seniority.MASS_OF_THE_UNIVERSE);
            dev11.setSkills(List.of("TypeScript", "Agentic Coding", "Telling Semi-Funny Jokes", "Mass-Producing Copilot Prompts", "Talking to AIs Until They Comply", "Public Speaking at Programmer Bars"));
            dev11.setOssProjects(List.of("agent-whisperer", "copilot-comedy-club"));
            dev11.setFavoriteDevJoke("I told my AI agent to refactor my code. It refactored my career instead.");
            dev11.setCoffeesPerDayInLiters(9);
            dev11.setDebuggingPowerLevel(8700);
            dev11.setRubberDuckName("GPT-Duck (responds in 3-5 business paragraphs)");
            dev11.setFavoriteKeyboardShortcut("Cmd+I (let the agent handle it)");
            dev11.setGitCommitStreak(404);
            dev11.setStackOverflowReputation(1337);
            dev11.setStillUsesVim(true);
            dev11.setShipName("The Prompt Injector");

            repo.saveAll(List.of(dev1, dev2, dev3, dev4, dev5, dev6, dev7, dev8, dev9, dev10, dev11));
        };
    }
}
