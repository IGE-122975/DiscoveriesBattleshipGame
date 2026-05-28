package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Coverage for {@link Position} — the smallest building block of the board.
 * <p>
 * Position is a value-with-state: the (row, column) pair identifies it, but
 * its lifecycle is tracked through {@code isOccupied} / {@code isHit} flags.
 * These tests pin both halves of the contract.
 */
class PositionTest {

    @Test
    @DisplayName("constructor stores row/column and starts free + unhurt")
    void newPositionIsFreeAndUnhurt() {
        Position p = new Position(3, 5);
        assertEquals(3, p.getRow());
        assertEquals(5, p.getColumn());
        assertFalse(p.isOccupied(), "fresh position should not be occupied");
        assertFalse(p.isHit(), "fresh position should not be hit");
    }

    @Test
    @DisplayName("occupy() flips only the occupied flag")
    void occupyOnlyTouchesOccupiedFlag() {
        Position p = new Position(0, 0);
        p.occupy();
        assertTrue(p.isOccupied());
        assertFalse(p.isHit(), "occupy() must not mark the position as hit");
    }

    @Test
    @DisplayName("shoot() flips only the hit flag")
    void shootOnlyTouchesHitFlag() {
        Position p = new Position(0, 0);
        p.shoot();
        assertTrue(p.isHit());
        assertFalse(p.isOccupied(), "shoot() must not mark the position as occupied");
    }

    @Test
    @DisplayName("equals compares (row, column) only — ignores flags and identity")
    void equalsIgnoresStateAndIdentity() {
        Position a = new Position(2, 7);
        Position b = new Position(2, 7);
        Position c = new Position(2, 8);

        assertEquals(a, b, "same coordinates must be equal");
        assertNotEquals(a, c, "different column must break equality");
        assertNotEquals(a, "(2,7)", "equality must reject non-IPosition objects");

        b.occupy();
        b.shoot();
        assertEquals(a, b, "equals must depend on coordinates only, not on flags");
    }

    @Test
    @DisplayName("equal positions yield equal hash codes")
    void hashCodeIsConsistentWithEquals() {
        Position a = new Position(4, 4);
        Position b = new Position(4, 4);
        // We don't require unequal positions to have unequal hashes (that's a
        // collision-rate property), only the equals→hashCode invariant.
        if (a.equals(b)) {
            assertEquals(a.hashCode(), b.hashCode());
        }
    }

    @Test
    @DisplayName("isAdjacentTo is true for the 8 neighbours and the position itself")
    void isAdjacentRecognisesNeighbours() {
        Position centre = new Position(5, 5);
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                Position neighbour = new Position(5 + dr, 5 + dc);
                assertTrue(centre.isAdjacentTo(neighbour),
                        "neighbour (" + (5 + dr) + "," + (5 + dc) + ") should be adjacent");
            }
        }
    }

    @Test
    @DisplayName("isAdjacentTo is false beyond the 8 neighbours")
    void isAdjacentRejectsDistantCells() {
        Position centre = new Position(5, 5);
        assertFalse(centre.isAdjacentTo(new Position(5, 7)), "two cells east is not adjacent");
        assertFalse(centre.isAdjacentTo(new Position(7, 5)), "two cells south is not adjacent");
        assertFalse(centre.isAdjacentTo(new Position(3, 3)), "diagonal two-away is not adjacent");
    }

    @Test
    @DisplayName("toString includes row and column")
    void toStringContainsRowAndColumn() {
        Position p = new Position(9, 1);
        String s = p.toString();
        assertTrue(s.contains("9"), "toString should mention the row");
        assertTrue(s.contains("1"), "toString should mention the column");
    }
}
