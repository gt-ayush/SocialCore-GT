package dev.socialcore.teams;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamDirectoryTest {
    private static final UUID FIRST_TEAM_ID = UUID.fromString("30000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_TEAM_ID = UUID.fromString("30000000-0000-0000-0000-000000000002");
    private static final UUID OWNER_ID = UUID.fromString("40000000-0000-0000-0000-000000000001");
    private static final UUID SHARED_MEMBER_ID = UUID.fromString("40000000-0000-0000-0000-000000000002");

    @Test
    void permitsAnEmptyDirectory() {
        assertTrue(new TeamDirectory(Map.of()).teams().isEmpty());
    }

    @Test
    void rejectsPlayersAssignedToMoreThanOneTeam() {
        Team first = new Team(FIRST_TEAM_ID, "First", OWNER_ID, Map.of(
                OWNER_ID, TeamRole.OWNER,
                SHARED_MEMBER_ID, TeamRole.MEMBER));
        Team second = new Team(SECOND_TEAM_ID, "Second", SHARED_MEMBER_ID, Map.of(
                SHARED_MEMBER_ID, TeamRole.OWNER));

        assertThrows(IllegalArgumentException.class,
                () -> new TeamDirectory(Map.of(first.id(), first, second.id(), second)));
    }

    @Test
    void requiresDirectoryKeysToMatchTeamIds() {
        Team team = new Team(FIRST_TEAM_ID, "First", OWNER_ID, Map.of(OWNER_ID, TeamRole.OWNER));

        assertThrows(IllegalArgumentException.class,
                () -> new TeamDirectory(Map.of(SECOND_TEAM_ID, team)));
    }
}
