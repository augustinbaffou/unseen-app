package fr.augustinbaffou.unseen.bar.service.osm;

import fr.augustinbaffou.unseen.bar.controller.navigation.BarExceptionConstants;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.entity.BarSchedule;
import fr.augustinbaffou.unseen.bar.entity.BarScheduleType;
import fr.augustinbaffou.unseen.bar.entity.BarType;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassElement;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

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
            throw new ResourceNotFoundException(
                    BarExceptionConstants.OSM_RESOURCE_NAME,
                    BarExceptionConstants.FIELD_COORDINATES,
                    osmId);
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

        // Types
        bar.setTypes(resolveTypes(tags));

        // Données brutes
        bar.setRawTags(new HashMap<>(tags));

        // Horaires
        List<BarSchedule> slots = new ArrayList<>();
        slots.addAll(OsmBarScheduleParser.parse(tags.get("opening_hours"), BarScheduleType.BAR));
        slots.addAll(OsmBarScheduleParser.parse(tags.get("opening_hours:kitchen"), BarScheduleType.KITCHEN));
        slots.forEach(slot -> slot.setBar(bar));
        bar.setSchedules(slots);

        return bar;
    }

    private Set<BarType> resolveTypes(Map<String, String> tags) {
        Set<BarType> types = EnumSet.noneOf(BarType.class);

        String amenity  = tags.getOrDefault("amenity", "");
        String barTag   = tags.getOrDefault("bar", "");
        String cuisine  = tags.getOrDefault("cuisine", "");

        switch (amenity) {
            case "pub"       -> types.add(BarType.PUB);
            case "nightclub" -> types.add(BarType.NIGHTCLUB);
            case "cafe"      -> types.add(BarType.COFFEE_SHOP_BAR);
            case "biergarten"-> types.add(BarType.BEER_BAR);
        }

        switch (barTag) {
            case "cocktail"  -> types.add(BarType.COCKTAIL_BAR);
            case "wine"      -> types.add(BarType.WINE_BAR);
            case "beer", "craft_beer" -> types.add(BarType.BEER_BAR);
            case "sports"    -> types.add(BarType.SPORTS_BAR);
            case "lounge"    -> types.add(BarType.LOUNGE_BAR);
            case "student"   -> types.add(BarType.STUDENT_BAR);
        }

        if ("tapas".equals(cuisine) || "tapas;bar".equals(cuisine)) {
            types.add(BarType.TAPAS_BAR);
        }

        if ("yes".equals(tags.get("microbrewery")) || "yes".equals(tags.get("brewery"))) {
            types.add(BarType.BREWPUB);
        }
        if ("yes".equals(tags.get("live_music"))) {
            types.add(BarType.LIVE_MUSIC_BAR);
        }
        if ("yes".equals(tags.get("dance"))) {
            types.add(BarType.DANCING_BAR);
        }
        if ("yes".equals(tags.get("outdoor_seating"))) {
            types.add(BarType.TERRACE_BAR);
        }
        if ("yes".equals(tags.get("pmu"))) {
            types.add(BarType.PMU);
        }
        if ("yes".equals(tags.get("pets_allowed"))) {
            types.add(BarType.PET_FRIENDLY);
        }

        return types;
    }

    private String firstNonNull(Map<String, String> tags, String... keys) {
        for (String key : keys) {
            String value = tags.get(key);
            if (value != null) return value;
        }
        return null;
    }
}
