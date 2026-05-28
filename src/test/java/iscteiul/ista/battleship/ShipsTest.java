package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cross-cutting coverage for the ship hierarchy.
 *
 * Each ship is parameterised by a bearing and a starting position. The shared
 * tests here assert: (a) declared sizes are stable, (b) factory routes the
 * right concrete type, (c) invalid bearings are rejected, and (d) the helpers
 * inherited from {@link Ship} agree with the geometry each subclass lays down.
 */
class ShipsTest {

    @Nested
    @DisplayName("Size contract — one source of truth per ship")
    class Sizes {

        @Test void bargeIsOneCell() {
            assertEquals(1, new Barge(Compass.NORTH, new Position(0, 0)).getSize());
        }

        @Test void caravelIsTwoCells() {
            assertEquals(2, new Caravel(Compass.EAST, new Position(0, 0)).getSize());
        }

        @Test void carrackIsThreeCells() {
            assertEquals(3, new Carrack(Compass.NORTH, new Position(0, 0)).getSize());
        }

        @Test void frigateIsFourCells() {
            assertEquals(4, new Frigate(Compass.SOUTH, new Position(0, 0)).getSize());
        }

        @Test void galleonIsFiveCells() {
            assertEquals(5, new Galleon(Compass.NORTH, new Position(0, 0)).getSize());
        }
    }

    @Nested
    @DisplayName("Ship.buildShip — string → concrete subclass")
    class Factory {

        @Test void barcaBuildsBarge() {
            Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
            assertNotNull(s);
            assertEquals(1, s.getSize());
            assertTrue(s instanceof Barge);
        }

        @Test void caravelaBuildsCaravel() {
            Ship s = Ship.buildShip("caravela", Compass.EAST, new Position(0, 0));
            assertTrue(s instanceof Caravel);
        }

        @Test void nauBuildsCarrack() {
            Ship s = Ship.buildShip("nau", Compass.NORTH, new Position(0, 0));
            assertTrue(s instanceof Carrack);
        }

        @Test void fragataBuildsFrigate() {
            Ship s = Ship.buildShip("fragata", Compass.SOUTH, new Position(0, 0));
            assertTrue(s instanceof Frigate);
        }

        @Test void galeaoBuildsGalleon() {
            Ship s = Ship.buildShip("galeao", Compass.WEST, new Position(0, 0));
            assertTrue(s instanceof Galleon);
        }

        @Test
        @DisplayName("unrecognised kind returns null instead of throwing")
        void unknownKindIsNull() {
            assertNull(Ship.buildShip("submarino", Compass.NORTH, new Position(0, 0)));
        }
    }

    @Nested
    @DisplayName("Invalid bearings are rejected")
    class InvalidBearings {

        @Test
        void caravelRejectsUnknownBearing() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Caravel(Compass.UNKNOWN, new Position(0, 0)));
        }

        @Test
        void carrackRejectsUnknownBearing() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Carrack(Compass.UNKNOWN, new Position(0, 0)));
        }

        @Test
        void frigateRejectsUnknownBearing() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Frigate(Compass.UNKNOWN, new Position(0, 0)));
        }

        @Test
        void galleonRejectsUnknownBearing() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Galleon(Compass.UNKNOWN, new Position(0, 0)));
        }

        @Test
        @DisplayName("Galleon rejects null bearing (AssertionError from Ship's assert, or NPE)")
        void galleonRejectsNullBearing() {
            // With assertions enabled (Surefire default `-ea`), Ship's constructor
            // assert fires first and throws AssertionError; without `-ea` the
            // Galleon constructor's own `if (bearing == null) throw new NPE` runs.
            // Both are acceptable — the contract is "do not silently accept null".
            assertThrows(Throwable.class,
                    () -> new Galleon(null, new Position(0, 0)));
        }
    }

    @Nested
    @DisplayName("Geometry — positions occupied by each ship")
    class Geometry {

        @Test
        @DisplayName("Caravel pointing north occupies two cells in the same column")
        void caravelNorthIsVertical() {
            Caravel c = new Caravel(Compass.NORTH, new Position(2, 5));
            assertEquals(2, c.getPositions().size());
            assertEquals(5, c.getLeftMostPos());
            assertEquals(5, c.getRightMostPos());
            assertEquals(2, c.getTopMostPos());
            assertEquals(3, c.getBottomMostPos());
        }

        @Test
        @DisplayName("Frigate pointing east occupies four cells in the same row")
        void frigateEastIsHorizontal() {
            Frigate f = new Frigate(Compass.EAST, new Position(7, 1));
            assertEquals(4, f.getPositions().size());
            assertEquals(7, f.getTopMostPos());
            assertEquals(7, f.getBottomMostPos());
            assertEquals(1, f.getLeftMostPos());
            assertEquals(4, f.getRightMostPos());
        }

        @Test
        @DisplayName("Galleon pointing north occupies five cells with a T-shape")
        void galleonNorthHasFiveCells() {
            Galleon g = new Galleon(Compass.NORTH, new Position(0, 0));
            assertEquals(5, g.getPositions().size());
        }
    }

    @Nested
    @DisplayName("Behavioural helpers inherited from Ship")
    class BehaviouralHelpers {

        @Test
        @DisplayName("a freshly-built ship is still floating")
        void freshShipFloats() {
            Carrack c = new Carrack(Compass.SOUTH, new Position(0, 0));
            assertTrue(c.stillFloating());
        }

        @Test
        @DisplayName("after every position is shot, the ship sinks")
        void shipSinksWhenAllCellsAreHit() {
            Carrack c = new Carrack(Compass.SOUTH, new Position(0, 0));
            for (IPosition p : c.getPositions()) {
                c.shoot(p);
            }
            assertFalse(c.stillFloating(), "all cells hit ⇒ ship must no longer be floating");
        }

        @Test
        @DisplayName("occupies() recognises every cell the ship lays down")
        void occupiesRecognisesEachCell() {
            Frigate f = new Frigate(Compass.NORTH, new Position(0, 0));
            for (IPosition p : f.getPositions()) {
                assertTrue(f.occupies(p), "should recognise its own cell " + p);
            }
        }

        @Test
        @DisplayName("two adjacent ships count as 'too close'")
        void touchingShipsAreTooClose() {
            // Caravel at (0,0) covers (0,0),(0,1); Barge at (1,1) is diagonally adjacent.
            Caravel left  = new Caravel(Compass.EAST, new Position(0, 0));
            Barge   right = new Barge(Compass.NORTH, new Position(1, 1));
            assertTrue(left.tooCloseTo(right));
            assertTrue(right.tooCloseTo(left));
        }

        @Test
        @DisplayName("ships separated by an empty corridor are not too close")
        void separatedShipsAreNotTooClose() {
            Caravel west = new Caravel(Compass.EAST, new Position(0, 0));    // (0,0),(0,1)
            Barge   east = new Barge(Compass.NORTH, new Position(0, 5));     // (0,5)
            assertFalse(west.tooCloseTo(east));
        }

        @Test
        @DisplayName("toString embeds the category and bearing")
        void toStringExposesCategoryAndBearing() {
            Barge b = new Barge(Compass.NORTH, new Position(0, 0));
            String s = b.toString();
            assertTrue(s.contains("Barca"));
            assertTrue(s.contains("n"));
        }
    }
}
