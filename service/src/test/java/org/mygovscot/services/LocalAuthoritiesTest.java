package org.mygovscot.services;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

public class LocalAuthoritiesTest {

    private static final LocalAuthorities authorities = new LocalAuthorities();

    @BeforeClass
    public static void setUp() throws IOException {
        authorities.load();
    }

    @Test
    public void thereAre32LocalAuthorities() {
        assertEquals(32, authorities.authorities.size());
    }

    @Test
    public void edinburghLA() throws IOException {
        LA authority = authorities.getAuthority(230);
        assertEquals("City of Edinburgh", authority.getName());
        URI.create(authority.getLink()).toURL();
    }

    @Test
    public void allLocalAuthoritiesHaveANameAndLink() throws IOException {
        for (LA authority : authorities.authorities.values()) {
            assertNotEmpty(authority.getName());
            assertNotEmpty(authority.getLink());
            URI.create(authority.getLink()).toURL();
        }
    }

    private static void assertNotEmpty(String string) {
        assertNotNull(string);
        assertTrue(string.length() > 1);
    }

}
