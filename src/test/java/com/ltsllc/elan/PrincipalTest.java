package com.ltsllc.elan;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrincipalTest {
    @Test
    public void constructor() {
        Principal principal  = new Principal("fred");
        assert (principal.hasSameName("fred"));
    }
}