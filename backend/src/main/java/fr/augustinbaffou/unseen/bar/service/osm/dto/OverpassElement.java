package fr.augustinbaffou.unseen.bar.service.osm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OverpassElement(
        String type,
        long id,
        Double lat,
        Double lon,
        OverpassCenter center,
        Map<String, String> tags
) {
    /** Retourne la latitude, qu'il s'agisse d'un node (lat directe) ou d'un way (center). */
    public Double effectiveLat() {
        return lat != null ? lat : (center != null ? center.lat() : null);
    }

    /** Retourne la longitude, qu'il s'agisse d'un node (lon directe) ou d'un way (center). */
    public Double effectiveLon() {
        return lon != null ? lon : (center != null ? center.lon() : null);
    }
}
