package dev.socialcore.friends;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FriendRelationshipTest {
    private static final UUID FIRST_PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID SECOND_PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void canonicalizesPlayerOrderForSymmetricRelationships() {
        FriendRelationship forward = new FriendRelationship(FIRST_PLAYER, SECOND_PLAYER);
        FriendRelationship reverse = new FriendRelationship(SECOND_PLAYER, FIRST_PLAYER);

        assertEquals(forward, reverse);
    }

    @Test
    void rejectsSelfFriendship() {
        assertThrows(IllegalArgumentException.class,
                () -> new FriendRelationship(FIRST_PLAYER, FIRST_PLAYER));
    }

    @Test
    void resolvesTheOtherPlayerOnlyForRelationshipMembers() {
        FriendRelationship relationship = new FriendRelationship(FIRST_PLAYER, SECOND_PLAYER);

        assertTrue(relationship.contains(FIRST_PLAYER));
        assertEquals(SECOND_PLAYER, relationship.otherPlayer(FIRST_PLAYER));
        assertFalse(relationship.contains(UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class,
                () -> relationship.otherPlayer(UUID.randomUUID()));
    }
}
