package dev.socialcore.friends;

import java.util.Objects;
import java.util.UUID;

public record FriendRelationship(UUID firstPlayerId, UUID secondPlayerId) {
    public FriendRelationship {
        Objects.requireNonNull(firstPlayerId, "firstPlayerId");
        Objects.requireNonNull(secondPlayerId, "secondPlayerId");
        if (firstPlayerId.equals(secondPlayerId)) {
            throw new IllegalArgumentException("A player cannot be friends with themselves");
        }

        if (firstPlayerId.compareTo(secondPlayerId) > 0) {
            UUID previousFirstPlayerId = firstPlayerId;
            firstPlayerId = secondPlayerId;
            secondPlayerId = previousFirstPlayerId;
        }
    }

    public boolean contains(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return this.firstPlayerId.equals(playerId) || this.secondPlayerId.equals(playerId);
    }

    public UUID otherPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        if (this.firstPlayerId.equals(playerId)) {
            return this.secondPlayerId;
        }
        if (this.secondPlayerId.equals(playerId)) {
            return this.firstPlayerId;
        }
        throw new IllegalArgumentException("Player is not part of this friend relationship");
    }
}
