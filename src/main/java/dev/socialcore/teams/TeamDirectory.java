package dev.socialcore.teams;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record TeamDirectory(Map<UUID, Team> teams) {
    public TeamDirectory {
        Objects.requireNonNull(teams, "teams");

        Map<UUID, Team> teamSnapshot = new LinkedHashMap<>();
        Set<UUID> assignedPlayers = new HashSet<>();
        teams.forEach((teamId, team) -> {
            Objects.requireNonNull(teamId, "team UUID");
            Objects.requireNonNull(team, "team");
            if (!teamId.equals(team.id())) {
                throw new IllegalArgumentException("Team map key must match the team UUID");
            }
            for (UUID playerId : team.roles().keySet()) {
                if (!assignedPlayers.add(playerId)) {
                    throw new IllegalArgumentException("A player cannot belong to multiple teams");
                }
            }
            teamSnapshot.put(teamId, team);
        });

        teams = Collections.unmodifiableMap(teamSnapshot);
    }
}
