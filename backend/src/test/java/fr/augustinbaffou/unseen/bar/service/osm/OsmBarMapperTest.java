package fr.augustinbaffou.unseen.bar.service.osm;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.entity.BarScheduleType;
import fr.augustinbaffou.unseen.bar.entity.BarType;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassCenter;
import fr.augustinbaffou.unseen.bar.service.osm.dto.OverpassElement;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OsmBarMapperTest {

    private OsmBarMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OsmBarMapper();
    }

    // ── Coordonnées manquantes → exception ───────────────────────────────────

    @Test
    void toBar_missingCoordinates_throwsResourceNotFoundException() {
        OverpassElement element = new OverpassElement("node", 1L, null, null, null, Map.of());

        assertThatThrownBy(() -> mapper.toBar("node/1", element))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void toBar_nodeWithCoordinates_setsLatLng() {
        OverpassElement element = new OverpassElement("node", 1L, 47.22, -1.55, null, Map.of("name", "Le Bar"));

        Bar bar = mapper.toBar("node/1", element);

        assertThat(bar.getLat()).isEqualTo(47.22);
        assertThat(bar.getLng()).isEqualTo(-1.55);
    }

    @Test
    void toBar_wayWithCenter_setsLatLngFromCenter() {
        OverpassCenter center = new OverpassCenter(47.22, -1.55);
        OverpassElement element = new OverpassElement("way", 1L, null, null, center, Map.of("name", "Le Bar"));

        Bar bar = mapper.toBar("way/1", element);

        assertThat(bar.getLat()).isEqualTo(47.22);
        assertThat(bar.getLng()).isEqualTo(-1.55);
    }

    // ── Identité ──────────────────────────────────────────────────────────────

    @Test
    void toBar_nameTagPresent_usesName() {
        Bar bar = toBarWithTags(Map.of("name", "Le Nid"));
        assertThat(bar.getName()).isEqualTo("Le Nid");
    }

    @Test
    void toBar_nameTagAbsent_fallsBackToOsmId() {
        Bar bar = toBarWithTags(Map.of());
        assertThat(bar.getName()).isEqualTo("node/42");
    }

    @Test
    void toBar_osmIdSet() {
        Bar bar = toBarWithTags(Map.of());
        assertThat(bar.getOsmId()).isEqualTo("node/42");
    }

    @Test
    void toBar_optionalIdentityFields() {
        Bar bar = toBarWithTags(Map.of(
                "name", "Le Nid",
                "alt_name", "Le Nid panoramique",
                "was:name", "Ancien Bar",
                "description", "Un bar sympa"
        ));

        assertThat(bar.getAltName()).isEqualTo("Le Nid panoramique");
        assertThat(bar.getWasName()).isEqualTo("Ancien Bar");
        assertThat(bar.getDescription()).isEqualTo("Un bar sympa");
    }

    @Test
    void toBar_nullTagsMap_doesNotThrow() {
        OverpassElement element = new OverpassElement("node", 42L, 47.22, -1.55, null, null);
        Bar bar = mapper.toBar("node/42", element);
        assertThat(bar.getName()).isEqualTo("node/42");
    }

    // ── Terrasse / intérieur ──────────────────────────────────────────────────

    @Test
    void toBar_seatingTags_mapped() {
        Bar bar = toBarWithTags(Map.of(
                "outdoor_seating", "yes",
                "indoor_seating", "no"
        ));

        assertThat(bar.getOutdoorSeating()).isEqualTo("yes");
        assertThat(bar.getIndoorSeating()).isEqualTo("no");
    }

    // ── Contact (priorité) ────────────────────────────────────────────────────

    @Test
    void toBar_contact_websitePriorityOverWebsite() {
        Bar bar = toBarWithTags(Map.of(
                "contact:website", "https://contact.example.com",
                "website", "https://website.example.com",
                "url", "https://url.example.com"
        ));
        assertThat(bar.getWebsite()).isEqualTo("https://contact.example.com");
    }

    @Test
    void toBar_contact_websiteFallsBackToWebsite() {
        Bar bar = toBarWithTags(Map.of(
                "website", "https://website.example.com",
                "url", "https://url.example.com"
        ));
        assertThat(bar.getWebsite()).isEqualTo("https://website.example.com");
    }

    @Test
    void toBar_contact_websiteFallsBackToUrl() {
        Bar bar = toBarWithTags(Map.of("url", "https://url.example.com"));
        assertThat(bar.getWebsite()).isEqualTo("https://url.example.com");
    }

    @Test
    void toBar_contact_instagramPriority() {
        Bar bar = toBarWithTags(Map.of(
                "contact:instagram", "@contact_ig",
                "instagram", "@ig"
        ));
        assertThat(bar.getInstagram()).isEqualTo("@contact_ig");
    }

    @Test
    void toBar_contact_facebookPriority() {
        Bar bar = toBarWithTags(Map.of(
                "contact:facebook", "https://fb.com/contact",
                "facebook", "https://fb.com/fb"
        ));
        assertThat(bar.getFacebook()).isEqualTo("https://fb.com/contact");
    }

    @Test
    void toBar_contact_nullWhenAbsent() {
        Bar bar = toBarWithTags(Map.of());
        assertThat(bar.getWebsite()).isNull();
        assertThat(bar.getInstagram()).isNull();
        assertThat(bar.getFacebook()).isNull();
    }

    // ── Adresse ───────────────────────────────────────────────────────────────

    @Test
    void toBar_addressTags_mapped() {
        Bar bar = toBarWithTags(Map.of(
                "addr:housenumber", "1",
                "addr:street", "Place du Bouffay",
                "addr:city", "Nantes"
        ));

        assertThat(bar.getAddrHousenumber()).isEqualTo("1");
        assertThat(bar.getAddrStreet()).isEqualTo("Place du Bouffay");
        assertThat(bar.getAddrCity()).isEqualTo("Nantes");
    }

    // ── Types ─────────────────────────────────────────────────────────────────

    @Test
    void toBar_amenityPub_addsPub() {
        assertThat(toBarWithTags(Map.of("amenity", "pub")).getTypes()).contains(BarType.PUB);
    }

    @Test
    void toBar_amenityNightclub_addsNightclub() {
        assertThat(toBarWithTags(Map.of("amenity", "nightclub")).getTypes()).contains(BarType.NIGHTCLUB);
    }

    @Test
    void toBar_amenityCafe_addsCoffeeShopBar() {
        assertThat(toBarWithTags(Map.of("amenity", "cafe")).getTypes()).contains(BarType.COFFEE_SHOP_BAR);
    }

    @Test
    void toBar_amenityBiergarten_addsBeerBar() {
        assertThat(toBarWithTags(Map.of("amenity", "biergarten")).getTypes()).contains(BarType.BEER_BAR);
    }

    @Test
    void toBar_barTagCocktail_addsCocktailBar() {
        assertThat(toBarWithTags(Map.of("bar", "cocktail")).getTypes()).contains(BarType.COCKTAIL_BAR);
    }

    @Test
    void toBar_barTagWine_addsWineBar() {
        assertThat(toBarWithTags(Map.of("bar", "wine")).getTypes()).contains(BarType.WINE_BAR);
    }

    @Test
    void toBar_barTagBeer_addsBeerBar() {
        assertThat(toBarWithTags(Map.of("bar", "beer")).getTypes()).contains(BarType.BEER_BAR);
    }

    @Test
    void toBar_barTagCraftBeer_addsBeerBar() {
        assertThat(toBarWithTags(Map.of("bar", "craft_beer")).getTypes()).contains(BarType.BEER_BAR);
    }

    @Test
    void toBar_barTagSports_addsSportsBar() {
        assertThat(toBarWithTags(Map.of("bar", "sports")).getTypes()).contains(BarType.SPORTS_BAR);
    }

    @Test
    void toBar_barTagLounge_addsLoungeBar() {
        assertThat(toBarWithTags(Map.of("bar", "lounge")).getTypes()).contains(BarType.LOUNGE_BAR);
    }

    @Test
    void toBar_barTagStudent_addsStudentBar() {
        assertThat(toBarWithTags(Map.of("bar", "student")).getTypes()).contains(BarType.STUDENT_BAR);
    }

    @Test
    void toBar_cuisineTapas_addsTapasBar() {
        assertThat(toBarWithTags(Map.of("cuisine", "tapas")).getTypes()).contains(BarType.TAPAS_BAR);
    }

    @Test
    void toBar_cuisineTapasBar_addsTapasBar() {
        assertThat(toBarWithTags(Map.of("cuisine", "tapas;bar")).getTypes()).contains(BarType.TAPAS_BAR);
    }

    @Test
    void toBar_microbreweryYes_addsBrewpub() {
        assertThat(toBarWithTags(Map.of("microbrewery", "yes")).getTypes()).contains(BarType.BREWPUB);
    }

    @Test
    void toBar_breweryYes_addsBrewpub() {
        assertThat(toBarWithTags(Map.of("brewery", "yes")).getTypes()).contains(BarType.BREWPUB);
    }

    @Test
    void toBar_liveMusicYes_addsLiveMusicBar() {
        assertThat(toBarWithTags(Map.of("live_music", "yes")).getTypes()).contains(BarType.LIVE_MUSIC_BAR);
    }

    @Test
    void toBar_danceYes_addsDancingBar() {
        assertThat(toBarWithTags(Map.of("dance", "yes")).getTypes()).contains(BarType.DANCING_BAR);
    }

    @Test
    void toBar_outdoorSeatingYes_addsTerraceBar() {
        assertThat(toBarWithTags(Map.of("outdoor_seating", "yes")).getTypes()).contains(BarType.TERRACE_BAR);
    }

    @Test
    void toBar_pmuYes_addsPmu() {
        assertThat(toBarWithTags(Map.of("pmu", "yes")).getTypes()).contains(BarType.PMU);
    }

    @Test
    void toBar_petsAllowedYes_addsPetFriendly() {
        assertThat(toBarWithTags(Map.of("pets_allowed", "yes")).getTypes()).contains(BarType.PET_FRIENDLY);
    }

    @Test
    void toBar_multipleTypeTags_addsAllMatchingTypes() {
        Bar bar = toBarWithTags(Map.of(
                "amenity", "pub",
                "live_music", "yes",
                "outdoor_seating", "yes"
        ));

        assertThat(bar.getTypes()).containsExactlyInAnyOrder(BarType.PUB, BarType.LIVE_MUSIC_BAR, BarType.TERRACE_BAR);
    }

    @Test
    void toBar_noTypeTags_emptyTypes() {
        assertThat(toBarWithTags(Map.of()).getTypes()).isEmpty();
    }

    // ── Horaires ──────────────────────────────────────────────────────────────

    @Test
    void toBar_openingHours_schedulesLinkedToBar() {
        Bar bar = toBarWithTags(Map.of("opening_hours", "Mo-Fr 18:00-23:00"));

        assertThat(bar.getSchedules()).hasSize(5);
        assertThat(bar.getSchedules()).allMatch(s -> s.getBar() == bar);
        assertThat(bar.getSchedules()).allMatch(s -> s.getType() == BarScheduleType.BAR);
    }

    @Test
    void toBar_kitchenHours_schedulesHaveKitchenType() {
        Bar bar = toBarWithTags(Map.of("opening_hours:kitchen", "Mo-Fr 12:00-14:00"));

        assertThat(bar.getSchedules()).hasSize(5);
        assertThat(bar.getSchedules()).allMatch(s -> s.getType() == BarScheduleType.KITCHEN);
    }

    @Test
    void toBar_bothBarAndKitchenHours_allSchedulesMerged() {
        Bar bar = toBarWithTags(Map.of(
                "opening_hours", "Mo 18:00-23:00",
                "opening_hours:kitchen", "Mo 12:00-14:00"
        ));

        assertThat(bar.getSchedules()).hasSize(2);
        assertThat(bar.getSchedules())
                .extracting(s -> s.getType())
                .containsExactlyInAnyOrder(BarScheduleType.BAR, BarScheduleType.KITCHEN);
    }

    @Test
    void toBar_noOpeningHours_emptySchedules() {
        assertThat(toBarWithTags(Map.of()).getSchedules()).isEmpty();
    }

    // ── Données brutes ────────────────────────────────────────────────────────

    @Test
    void toBar_rawTagsCopied() {
        Bar bar = toBarWithTags(Map.of("name", "Le Nid", "amenity", "bar"));

        assertThat(bar.getRawTags()).containsEntry("name", "Le Nid");
        assertThat(bar.getRawTags()).containsEntry("amenity", "bar");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Bar toBarWithTags(Map<String, String> tags) {
        OverpassElement element = new OverpassElement("node", 42L, 47.22, -1.55, null, tags);
        return mapper.toBar("node/42", element);
    }
}
