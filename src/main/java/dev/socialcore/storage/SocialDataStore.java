package dev.socialcore.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.socialcore.SocialCoreGt;
import dev.socialcore.friends.FriendRelationship;
import dev.socialcore.teams.Team;
import dev.socialcore.teams.TeamDirectory;
import dev.socialcore.teams.TeamRole;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public final class SocialDataStore {
    private static final int CURRENT_SCHEMA_VERSION = 1;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path directory;

    public SocialDataStore(Path directory) {
        this.directory = Objects.requireNonNull(directory, "directory");
    }

    public PersistentSocialData load() throws IOException {
        try {
            Files.createDirectories(this.directory);
            FriendsDocument friends = this.readDocument(
                    this.directory.resolve("friends.json"),
                    FriendsDocument.class,
                    FriendsDocument::empty);
            TeamsDocument teams = this.readDocument(
                    this.directory.resolve("teams.json"),
                    TeamsDocument.class,
                    TeamsDocument::empty);
            return new PersistentSocialData(this.readFriendships(friends), this.readTeams(teams));
        } catch (IOException exception) {
            SocialCoreGt.LOGGER.error("Could not load SocialCore-GT persistent data from {}", this.directory, exception);
            throw exception;
        }
    }

    private <T> T readDocument(Path path, Class<T> documentType, Supplier<T> emptyDocument) throws IOException {
        if (Files.notExists(path)) {
            T document = emptyDocument.get();
            try {
                Files.writeString(path, GSON.toJson(document), StandardCharsets.UTF_8);
            } catch (IOException exception) {
                throw new IOException("Could not initialize persistent data file " + path, exception);
            }
            return document;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            T document = GSON.fromJson(reader, documentType);
            if (document == null) {
                throw new IllegalArgumentException("File is empty or contains JSON null");
            }
            return document;
        } catch (IOException | JsonParseException | IllegalArgumentException exception) {
            throw new IOException("Invalid persistent data file " + path + ": " + exception.getMessage(), exception);
        }
    }

    private Set<FriendRelationship> readFriendships(FriendsDocument document) throws IOException {
        if (document.schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw invalidFile("friends.json", "Unsupported schema version " + document.schemaVersion);
        }
        if (document.relationships == null) {
            throw invalidFile("friends.json", "Missing relationships list");
        }

        Set<FriendRelationship> friendships = new LinkedHashSet<>();
        try {
            for (FriendEntry entry : document.relationships) {
                if (entry == null || entry.firstPlayerId == null || entry.secondPlayerId == null) {
                    throw new IllegalArgumentException("Friend relationship is missing a player UUID");
                }
                FriendRelationship relationship = new FriendRelationship(entry.firstPlayerId, entry.secondPlayerId);
                if (!friendships.add(relationship)) {
                    throw new IllegalArgumentException("Duplicate friend relationship " + relationship);
                }
            }
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw invalidFile("friends.json", exception.getMessage(), exception);
        }
        return friendships;
    }

    private TeamDirectory readTeams(TeamsDocument document) throws IOException {
        if (document.schemaVersion != CURRENT_SCHEMA_VERSION) {
            throw invalidFile("teams.json", "Unsupported schema version " + document.schemaVersion);
        }
        if (document.teams == null) {
            throw invalidFile("teams.json", "Missing teams list");
        }

        Map<UUID, Team> teams = new LinkedHashMap<>();
        try {
            for (TeamEntry entry : document.teams) {
                if (entry == null || entry.id == null || entry.name == null
                        || entry.ownerId == null || entry.roles == null) {
                    throw new IllegalArgumentException("Team record is missing required fields");
                }
                Team team = new Team(entry.id, entry.name, entry.ownerId, entry.roles);
                if (teams.putIfAbsent(team.id(), team) != null) {
                    throw new IllegalArgumentException("Duplicate team UUID " + team.id());
                }
            }
            return new TeamDirectory(teams);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw invalidFile("teams.json", exception.getMessage(), exception);
        }
    }

    private IOException invalidFile(String filename, String reason) {
        return invalidFile(filename, reason, null);
    }

    private IOException invalidFile(String filename, String reason, Exception cause) {
        String message = "Invalid persistent data file " + this.directory.resolve(filename) + ": " + reason;
        return cause == null ? new IOException(message) : new IOException(message, cause);
    }

    private static final class FriendsDocument {
        private int schemaVersion = CURRENT_SCHEMA_VERSION;
        private Set<FriendEntry> relationships = new LinkedHashSet<>();

        private static FriendsDocument empty() {
            return new FriendsDocument();
        }
    }

    private static final class FriendEntry {
        private UUID firstPlayerId;
        private UUID secondPlayerId;

        private FriendEntry() {
        }
    }

    private static final class TeamsDocument {
        private int schemaVersion = CURRENT_SCHEMA_VERSION;
        private Set<TeamEntry> teams = new LinkedHashSet<>();

        private static TeamsDocument empty() {
            return new TeamsDocument();
        }
    }

    private static final class TeamEntry {
        private UUID id;
        private String name;
        private UUID ownerId;
        private Map<UUID, TeamRole> roles = new LinkedHashMap<>();

        private TeamEntry() {
        }
    }
}
