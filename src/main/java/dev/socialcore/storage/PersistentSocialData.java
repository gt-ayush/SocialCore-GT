package dev.socialcore.storage;

import dev.socialcore.friends.FriendRelationship;
import dev.socialcore.teams.TeamDirectory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public record PersistentSocialData(Set<FriendRelationship> friendships, TeamDirectory teams) {
    public PersistentSocialData {
        Objects.requireNonNull(friendships, "friendships");
        Objects.requireNonNull(teams, "teams");
        friendships = Collections.unmodifiableSet(new LinkedHashSet<>(friendships));
    }
}
