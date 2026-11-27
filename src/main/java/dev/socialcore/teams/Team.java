package dev.socialcore.teams;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record Team(UUID id, String name, UUID ownerId, Map<UUID, TeamRole> roles) {
    public Team {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(roles, "roles");

        name = name.strip();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Team name must not be blank");
        }

        Map<UUID, TeamRole> roleSnapshot = new LinkedHashMap<>();
        roles.forEach((playerId, role) -> roleSnapshot.put(
                Objects.requireNonNull(playerId, "team member UUID"),
                Objects.requireNonNull(role, "team member role")));

        if (roleSnapshot.get(ownerId) != TeamRole.OWNER) {
            throw new IllegalArgumentException("Team owner must have the OWNER role");
        }
        long ownerCount = roleSnapshot.values().stream()
                .filter(role -> role == TeamRole.OWNER)
                .count();
        if (ownerCount != 1) {
            throw new IllegalArgumentException("Team must have exactly one owner");
        }

        roles = Collections.unmodifiableMap(roleSnapshot);
    }
}
