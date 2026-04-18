package fr.augustinbaffou.unseen.bar.controller.navigation;

public final class BarExceptionConstants {

    private BarExceptionConstants() {}

    // ── Nom de la ressource ───────────────────────────────────────────────────

    public static final String RESOURCE_NAME     = "Bar";
    public static final String OSM_RESOURCE_NAME = "OsmBar";

    // ── Noms des champs (utilisés dans ResourceNotFoundException) ─────────────

    public static final String FIELD_ID          = "id";
    public static final String FIELD_OSM_ID      = "osmId";
    public static final String FIELD_COORDINATES = "coordinates";

    // ── Messages de validation ────────────────────────────────────────────────

    public static final String INVALID_OSM_ID_FORMAT =
            "Invalid osmId format, expected 'node/<id>' or 'way/<id>': '%s'";

    public static final String INVALID_OSM_TYPE = "Type must be 'node' or 'way'";
}
