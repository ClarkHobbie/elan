package com.ltsllc.elan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElanTest {

    @Test
    void main() {
        Elan elan = new Elan();
        String[] args = {};
        Elan.in = System.in;
        Elan.out = System.out;
        elan.main(args);
    }
}