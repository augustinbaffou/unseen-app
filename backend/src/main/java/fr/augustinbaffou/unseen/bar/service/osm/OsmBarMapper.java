package fr.augustinbaffou.unseen.bar.service.osm;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.entity.BarSchedule;
import fr.augustinbaffou.unseen.bar.entity.BarScheduleType;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassElement;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mappe un élément Overpass vers l'entité Bar.
 *
 * Priorité des champs de contact (convention OSM) :
 *   contact:website > website > url
 *   contact:instagram > instagram
 *   contact:facebook > facebook
 */
@Component
public class OsmBarMapper {

    public Bar toBar(String osmId, OverpassElement element) {
        Map<String, String> tags = element.tags() != null ? element.tags() : Map.of();

        Double lat = element.effectiveLat();
        Double lng = element.effectiveLon();
        if (lat == null || lng == null) {
            throw new ResourceNotFoundException("OsmBar", "coordinates", osmId);
        }

        Bar bar = new Bar();
        bar.setOsmId(osmId);
        bar.setLat(lat);
        bar.setLng(lng);

        // Identité
        bar.setName(tags.getOrDefault("name", osmId));
        bar.setAltName(tags.get("alt_name"));
        bar.setWasName(tags.get("was:name"));
        bar.setDescription(tags.get("description"));

        // Terrasse / intérieur
        bar.setOutdoorSeating(tags.get("outdoor_seating"));
        bar.setIndoorSeating(tags.get("indoor_seating"));

        // Contact (résolution par priorité)
        bar.setWebsite(firstNonNull(tags, "contact:website", "website", "url"));
        bar.setInstagram(firstNonNull(tags, "contact:instagram", "instagram"));
        bar.setFacebook(firstNonNull(tags, "contact:facebook", "facebook"));

        // Adresse
        bar.setAddrHousenumber(tags.get("addr:housenumber"));
        bar.setAddrStreet(tags.get("addr:street"));
        bar.setAddrCity(tags.get("addr:city"));

        // Données brutes
        bar.setRawTags(Map.copyOf(tags));

        // Horaires
        List<BarSchedule> slots = new ArrayList<>();
        slots.addAll(OsmBarScheduleParser.parse(tags.get("opening_hours"), BarScheduleType.BAR));
        slots.addAll(OsmBarScheduleParser.parse(tags.get("opening_hours:kitchen"), BarScheduleType.KITCHEN));
        slots.forEach(slot -> slot.setBar(bar));
        bar.setSchedules(slots);

        return bar;
    }

    private String firstNonNull(Map<String, String> tags, String... keys) {
        for (String key : keys) {
            String value = tags.get(key);
            if (value != null) return value;
        }
        return null;
    }
}
