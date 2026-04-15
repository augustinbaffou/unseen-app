package fr.augustinbaffou.unseen.bar.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Schema(description = "Établissement (bar, café, pub) importé depuis OpenStreetMap")
@Entity
@Table(name = "bars", uniqueConstraints = @UniqueConstraint(name = "uq_bars_osm_id", columnNames = "osm_id"))
public class Bar {

    @Schema(description = "Identifiant interne", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Identifiant OpenStreetMap (node ou way)", example = "node/123456789")
    @Column(name = "osm_id", nullable = false)
    private String osmId;

    @Schema(description = "Latitude WGS84", example = "47.2182")
    @Column(nullable = false)
    private Double lat;

    @Schema(description = "Longitude WGS84", example = "-1.5536")
    @Column(nullable = false)
    private Double lng;

    // ── Identité ─────────────────────────────────────────────────────────────

    @Schema(description = "Nom de l'établissement", example = "Le Nid")
    @Column(nullable = false)
    private String name;

    @Schema(description = "Nom alternatif (tag OSM alt_name)", example = "Le Nid - Bar panoramique")
    @Column(name = "alt_name")
    private String altName;

    @Schema(description = "Ancien nom (tag OSM was:name)", example = "Bar de la Tour")
    @Column(name = "was_name")
    private String wasName;

    @Schema(description = "Description libre de l'établissement")
    @Column(columnDefinition = "TEXT")
    private String description;

    // ── Tarification ─────────────────────────────────────────────────────────

    @Schema(description = "Tranche de prix (1 = €, 2 = €€, 3 = €€€, 4 = €€€€)", example = "2", minimum = "1", maximum = "4")
    @Column(name = "price_range")
    private Integer priceRange;

    // ── Horaires ─────────────────────────────────────────────────────────────

    @Schema(description = "Créneaux horaires du bar (ouverture, cuisine, happy hours)")
    @OneToMany(mappedBy = "bar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BarSchedule> schedules = new ArrayList<>();

    // ── Terrasse / intérieur ──────────────────────────────────────────────────
    // Valeur brute OSM conservée (yes / no / terrace / pedestrian_zone / …)

    @Schema(description = "Présence d'une terrasse extérieure (valeur brute OSM)", example = "yes", allowableValues = {"yes", "no", "terrace", "sidewalk", "pedestrian_zone"})
    @Column(name = "outdoor_seating")
    private String outdoorSeating;

    @Schema(description = "Présence d'une salle intérieure (valeur brute OSM)", example = "yes", allowableValues = {"yes", "no"})
    @Column(name = "indoor_seating")
    private String indoorSeating;

    // ── Contact ───────────────────────────────────────────────────────────────
    // Résolu par priorité : contact:X > X > url (voir BarMapper)

    @Schema(description = "Compte Instagram (URL ou handle)", example = "https://instagram.com/le_nid_nantes")
    private String instagram;

    @Schema(description = "Page Facebook (URL)", example = "https://facebook.com/lenidnantes")
    private String facebook;

    @Schema(description = "Site web officiel", example = "https://lenid.fr")
    private String website;

    // ── Adresse ───────────────────────────────────────────────────────────────

    @Schema(description = "Numéro de rue", example = "1")
    @Column(name = "addr_housenumber")
    private String addrHousenumber;

    @Schema(description = "Nom de la rue", example = "Place du Bouffay")
    @Column(name = "addr_street")
    private String addrStreet;

    @Schema(description = "Ville", example = "Nantes")
    @Column(name = "addr_city")
    private String addrCity;

    // ── Données brutes OSM ───────────────────────────────────────────────────
    // Contient tous les tags d'origine ; sert de filet de sécurité si on veut
    // extraire d'autres champs sans re-importer.

    @Schema(description = "Tags OSM bruts complets au format clé/valeur")
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_tags", columnDefinition = "jsonb")
    private Map<String, Object> rawTags;

    // ── Constructeurs ─────────────────────────────────────────────────────────

    public Bar() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOsmId() { return osmId; }
    public void setOsmId(String osmId) { this.osmId = osmId; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAltName() { return altName; }
    public void setAltName(String altName) { this.altName = altName; }

    public String getWasName() { return wasName; }
    public void setWasName(String wasName) { this.wasName = wasName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPriceRange() { return priceRange; }
    public void setPriceRange(Integer priceRange) { this.priceRange = priceRange; }

    public List<BarSchedule> getSchedules() { return schedules; }
    public void setSchedules(List<BarSchedule> schedules) { this.schedules = schedules; }

    public String getOutdoorSeating() { return outdoorSeating; }
    public void setOutdoorSeating(String outdoorSeating) { this.outdoorSeating = outdoorSeating; }

    public String getIndoorSeating() { return indoorSeating; }
    public void setIndoorSeating(String indoorSeating) { this.indoorSeating = indoorSeating; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getAddrHousenumber() { return addrHousenumber; }
    public void setAddrHousenumber(String addrHousenumber) { this.addrHousenumber = addrHousenumber; }

    public String getAddrStreet() { return addrStreet; }
    public void setAddrStreet(String addrStreet) { this.addrStreet = addrStreet; }

    public String getAddrCity() { return addrCity; }
    public void setAddrCity(String addrCity) { this.addrCity = addrCity; }

    public Map<String, Object> getRawTags() { return rawTags; }
    public void setRawTags(Map<String, Object> rawTags) { this.rawTags = rawTags; }
}
