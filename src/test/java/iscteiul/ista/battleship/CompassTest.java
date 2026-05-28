package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Coverage for the {@link Compass} enum: the char↔enum mapping is parsed from
 * user input, so any drift between the two would silently misroute ships.
 */
class CompassTest {

    @ParameterizedTest(name = "{0} → ''{1}''")
    @CsvSource({
            "NORTH, n",
            "SOUTH, s",
            "EAST,  e",
            "WEST,  o",
            "UNKNOWN, u"
    })
    @DisplayName("each compass direction exposes its single-char shorthand")
    void directionExposesItsChar(String name, char expected) {
        Compass value = Compass.valueOf(name);
        assertEquals(expected, value.getDirection());
        assertEquals(String.valueOf(expected), value.toString());
    }

    @ParameterizedTest(name = "''{0}'' → {1}")
    @CsvSource({
            "n, NORTH",
            "s, SOUTH",
            "e, EAST",
            "o, WEST"
    })
    @DisplayName("charToCompass parses each canonical bearing letter")
    void charToCompassParsesCanonicalLetters(char input, String expected) {
        assertEquals(Compass.valueOf(expected), Compass.charToCompass(input));
    }

    @Test
    @DisplayName("unrecognised bearing letters fall back to UNKNOWN, not crash")
    void unknownLetterMapsToUnknown() {
        assertEquals(Compass.UNKNOWN, Compass.charToCompass('x'));
        assertEquals(Compass.UNKNOWN, Compass.charToCompass(' '));
        assertEquals(Compass.UNKNOWN, Compass.charToCompass('N')); // uppercase is not the contract
    }
}
