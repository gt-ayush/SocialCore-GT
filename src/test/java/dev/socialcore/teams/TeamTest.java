package dev.socialcore.teams;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamTest {
    private static final UUID TEAM_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private static final UUID OWNER_ID = UUID.fromString("20000000-0000-0000-0000-000000000001");
    private static final UUID MEMBER_ID = UUID.fromString("20000000-0000-0000-0000-000000000002");

    @Test
    void trimsNameAndStoresAnImmutableRoleSnapshot() {
        Team team = new Team(TEAM_ID, "  Builders  ", OWNER_ID, Map.of(
                OWNER_ID, TeamRole.OWNER,
                MEMBER_ID, TeamRole.MEMBER));

        assertEquals("Builders", team.name());
        assertEquals(2, team.roles().size());
        assertThrows(UnsupportedOperationException.class,
                () -> team.roles().put(UUID.randomUUID(), TeamRole.MEMBER));
    }

    @Test
    void requiresExactlyOneOwnerWithTheOwnerRole() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team(TEAM_ID, "Builders", OWNER_ID, Map.of(MEMBER_ID, TeamRole.MEMBER)));
        assertThrows(IllegalArgumentException.class,
                () -> new Team(TEAM_ID, "Builders", OWNER_ID, Map.of(
                        OWNER_ID, TeamRole.MEMBER)));
        assertThrows(IllegalArgumentException.class,
                () -> new Team(TEAM_ID, "Builders", OWNER_ID, Map.of(
                        OWNER_ID, TeamRole.OWNER,
                        MEMBER_ID, TeamRole.OWNER)));
    }

    @Test
    void rejectsBlankTeamNames() {
        assertThrows(IllegalArgumentException.class,
                () -> new Team(TEAM_ID, "  ", OWNER_ID, Map.of(OWNER_ID, TeamRole.OWNER)));
    }
}
