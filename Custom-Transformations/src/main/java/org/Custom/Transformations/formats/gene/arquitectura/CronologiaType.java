package org.Custom.Transformations.formats.gene.arquitectura;

import org.Custom.Transformations.formats.gene.common.Cronologia;

import java.util.HashMap;

public class CronologiaType extends HashMap<String, Cronologia> {
    public CronologiaType(){
        this.put("01.", new Cronologia("01.", -650, -50, "Ferro-Ibèric"));
        this.put("02.", new Cronologia("02.", -600, -50, "Grec"));
        this.put("03.", new Cronologia("03.", -218, 476, "Romà"));
        this.put("03.01.", new Cronologia("03.01.", 301, 400, "IV"));
        this.put("03.02.", new Cronologia("03.02.", 401, 500, "V"));
        this.put("04.", new Cronologia("04.", 500, 1450, "Medieval"));
        this.put("04.01.", new Cronologia("04.01.", 501, 600, "VI"));
        this.put("04.02.", new Cronologia("04.02.", 601, 700, "VII"));
        this.put("04.03.", new Cronologia("04.03.", 701, 800, "VIII"));
        this.put("04.04.", new Cronologia("04.04.", 801, 900, "IX"));
        this.put("04.05.", new Cronologia("04.05.", 901, 1000, "X"));
        this.put("04.06.", new Cronologia("04.06.", 1001, 1100, "XI"));
        this.put("04.07.", new Cronologia("04.07.", 1101, 1200, "XII"));
        this.put("04.08.", new Cronologia("04.08.", 1201, 1300, "XIII"));
        this.put("04.09.", new Cronologia("04.09.", 1301, 1400, "XIV"));
        this.put("04.10.", new Cronologia("04.10.", 1401, 1500, "XV"));
        this.put("05.", new Cronologia("05.", 1501, 1600, "XVI"));
        this.put("06.", new Cronologia("06.", 1601, 1700, "XVII"));
        this.put("07.", new Cronologia("07.", 1701, 1800, "XVIII"));
        this.put("07.00.", new Cronologia("07.00.", 1701, 1725, "XVIII Inici"));
        this.put("07.02.", new Cronologia("07.02.", 1726, 1775, "XVIII Mitjan"));
        this.put("07.03.", new Cronologia("07.03.", 1776, 1800, "XVIII Final"));
        this.put("08.", new Cronologia("08.", 1801, 1900, "XIX"));
        this.put("08.01.", new Cronologia("08.01.", 1801, 1825, "XIX Inici"));
        this.put("08.02.", new Cronologia("08.02.", 1826, 1875, "XIX Mitjan"));
        this.put("08.03.", new Cronologia("08.03.", 1876, 1900, "XIX Final"));
        this.put("09.", new Cronologia("09.", 1901, 2000, "XX"));
        this.put("09.01.", new Cronologia("09.01.", 1901, 1925, "XX Inici"));
        this.put("09.02.", new Cronologia("09.02.", 1926, 1975, "XX Mitjan"));
        this.put("09.03.", new Cronologia("09.03.", 1976, 2000, "XX Final"));
        this.put("10.", new Cronologia("10.", 2001, 2100, "XXI"));
        this.put("10.01.", new Cronologia("10.01.", 2001, 2025, "XXI Inici"));
        this.put("10.02.", new Cronologia("10.02.", 2026, 2075, "XXI Mitjan"));
        this.put("10.03.", new Cronologia("10.03.", 2076, 2100, "XXI Final"));
    }
}
