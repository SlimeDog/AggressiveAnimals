package dev.ratas.aggressiveanimals.config.messaging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessagesUtilTest {

    @Test
    public void test_formatWorkOneDecimalDouble() {
        double d = 0.1D;
        String expected = "0.1";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkOneDecimalFloat() {
        float f = 0.3F;
        String expected = "0.3";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

    @Test
    public void test_formatWorkOneDecimalDoubleEnd0() {
        double d = 0.10D;
        String expected = "0.1";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkOneDecimalFloatEnd0() {
        float f = 0.30F;
        String expected = "0.3";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

    @Test
    public void test_formatWorkOneDecimalDoubleEnd00() {
        double d = 0.100D;
        String expected = "0.1";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkOneDecimalFloatEnd00() {
        float f = 0.300F;
        String expected = "0.3";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

    @Test
    public void test_formatWorkOneDecimalDouble2() {
        double d = 22.1D;
        String expected = "22.1";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkOneDecimalFloat2() {
        float f = 10.3F;
        String expected = "10.3";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

    @Test
    public void test_formatWorkTwoDecimalDouble() {
        double d = 0.15D;
        String expected = "0.15";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkTwoDecimalFloat() {
        float f = 0.31F;
        String expected = "0.31";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

    @Test
    public void test_formatWorkTwoDecimalDouble2() {
        double d = 22.17D;
        String expected = "22.17";
        Assertions.assertEquals(expected, Messages.formatDouble(d));
    }

    @Test
    public void test_formatWorkTwoDecimalFloat2() {
        float f = 10.39F;
        String expected = "10.39";
        Assertions.assertEquals(expected, Messages.formatDouble(f));
    }

}
