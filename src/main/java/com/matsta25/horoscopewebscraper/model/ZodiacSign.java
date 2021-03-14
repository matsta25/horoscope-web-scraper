package com.matsta25.horoscopewebscraper.model;

public enum ZodiacSign {
    Aries("baran"),
    Taurus("byk"),
    Gemini("bliznieta"),
    Cancer("rak"),
    Leo("lew"),
    Virgo("panna"),
    Libra("waga"),
    Scorpio("skorpion"),
    Sagittarius("strzelec"),
    Capricorn("koziorozec"),
    Aquarius("wodnik"),
    Pisces("ryby");


    public final String label;

    private ZodiacSign(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
