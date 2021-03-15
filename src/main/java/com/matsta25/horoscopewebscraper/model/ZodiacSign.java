package com.matsta25.horoscopewebscraper.model;

public enum ZodiacSign {
    Virgo("panna"),
    Libra("waga"),
    Scorpio("skorpion"),
    Sagittarius("strzelec"),
    Capricorn("koziorozec"),
    Aquarius("wodnik"),
    Pisces("ryby"),
    Aries("baran"),
    Taurus("byk"),
    Gemini("bliznieta"),
    Cancer("rak"),
    Leo("lew");


    public final String label;

    private ZodiacSign(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
