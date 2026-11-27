package dev.socialcore.storage;

import dev.socialcore.friends.FriendRelationship;
import dev.socialcore.teams.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SocialDataStoreTest {
    private static final UUID FIRST_PLAYER_ID = UUID.fromString("50000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_PLAYER_ID = UUID.fromString("50000000-0000-0000-0000-000000000002");
    private static final UUID TEAM_ID = UUID.fromString("60000000-0000-0000-0000-000000000001");

    @TempDir
    Path directory;

    @Test
    void initializesMissingVersionedFilesWithEmptyData() throws IOException {
        PersistentSocialData data = new SocialDataStore(this.directory).load();

        assertTrue(data.friendships().isEmpty());
        assertTrue(data.teams().teams().isEmpty());
        assertTrue(Files.readString(this.directory.resolve("friends.json")).contains("\"schemaVersion\": 1"));
        assertTrue(Files.readString(this.directory.resolve("teams.json")).contains("\"schemaVersion\": 1"));
    }

    @Test
    void loadsFriendshipsAndTeamsFromVersionOneFiles() throws IOException {
        Files.writeString(this.directory.resolve("friends.json"), """
                {
                  "schemaVersion": 1,
                  "relationships": [
                    {
                      "firstPlayerId": "%s",
                      "secondPlayerId": "%s"
                    }
                  ]
                }
                """.formatted(SECOND_PLAYER_ID, FIRST_PLAYER_ID));
        Files.writeString(this.directory.resolve("teams.json"), """
                {
                  "schemaVersion": 1,
                  "teams": [
                    {
                      "id": "%s",
                      "name": "  Builders  ",
                      "ownerId": "%s",
                      "roles": {
                        "%s": "OWNER",
                        "%s": "MEMBER"
                      }
                    }
                  ]
                }
                """.formatted(TEAM_ID, FIRST_PLAYER_ID, FIRST_PLAYER_ID, SECOND_PLAYER_ID));

        PersistentSocialData data = new SocialDataStore(this.directory).load();

        assertEquals(1, data.friendships().size());
        assertTrue(data.friendships().contains(new FriendRelationship(FIRST_PLAYER_ID, SECOND_PLAYER_ID)));
        assertEquals("Builders", data.teams().teams().get(TEAM_ID).name());
        assertEquals(TeamRole.MEMBER,
                data.teams().teams().get(TEAM_ID).roles().get(SECOND_PLAYER_ID));
    }

    @Test
    void reportsUnsupportedSchemaAndPreservesTheInvalidFile() throws IOException {
        Path friendsFile = this.directory.resolve("friends.json");
        String invalidDocument = "{\"schemaVersion\":99,\"relationships\":[]}";
        Files.writeString(friendsFile, invalidDocument);

        IOException exception = assertThrows(IOException.class, () -> new SocialDataStore(this.directory).load());

        assertTrue(exception.getMessage().contains(friendsFile.toString()));
        assertFalse(exception.getMessage().isBlank());
        assertEquals(invalidDocument, Files.readString(friendsFile));
    }

    @Test
    void rejectsInvalidTeamMembershipInsteadOfLoadingPartialData() throws IOException {
        Files.writeString(this.directory.resolve("teams.json"), """
                {
                  "schemaVersion": 1,
                  "teams": [
                    {
                      "id": "%s",
                      "name": "First",
                      "ownerId": "%s",
                      "roles": {"%s": "OWNER", "%s": "MEMBER"}
                    },
                    {
                      "id": "60000000-0000-0000-0000-000000000002",
                      "name": "Second",
                      "ownerId": "%s",
                      "roles": {"%s": "OWNER"}
                    }
                  ]
                }
                """.formatted(
                TEAM_ID,
                FIRST_PLAYER_ID,
                FIRST_PLAYER_ID,
                SECOND_PLAYER_ID,
                SECOND_PLAYER_ID,
                SECOND_PLAYER_ID));

        IOException exception = assertThrows(IOException.class, () -> new SocialDataStore(this.directory).load());

        assertTrue(exception.getMessage().contains("multiple teams"));
    }
}
