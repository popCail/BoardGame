import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetworkValidatorTest {

    @Test
    void parseAddress_acceptsValidIpv4AndFourDigitPort() {
        assertArrayEquals(new String[]{"192.168.1.1", "7777"},
                NetworkValidator.parseAddress("192.168.1.1:7777"));
        assertArrayEquals(new String[]{"127.0.0.1", "8080"},
                NetworkValidator.parseAddress("127.0.0.1:8080"));
    }

    @Test
    void parseAddress_rejectsNullMissingColonOrBadIpPort() {
        assertNull(NetworkValidator.parseAddress(null));
        assertNull(NetworkValidator.parseAddress("192.168.1.1"));
        assertNull(NetworkValidator.parseAddress("999.1.1.1:7777"));
        assertNull(NetworkValidator.parseAddress("192.168.1.1:77"));
        assertNull(NetworkValidator.parseAddress("192.168.1.1:77777"));
        assertNull(NetworkValidator.parseAddress("abc:7777"));
    }

    @Test
    void isValidIpv4_and_isValidPort() {
        assertTrue(NetworkValidator.isValidIpv4("0.0.0.0"));
        assertTrue(NetworkValidator.isValidIpv4("255.255.255.255"));
        assertFalse(NetworkValidator.isValidIpv4("256.0.0.1"));
        assertFalse(NetworkValidator.isValidIpv4(null));

        assertTrue(NetworkValidator.isValidPort("0000"));
        assertTrue(NetworkValidator.isValidPort("9999"));
        assertFalse(NetworkValidator.isValidPort("999"));
        assertFalse(NetworkValidator.isValidPort(null));
    }
}
