package fr.augustinbaffou.unseen.bar.controller.navigation;

public final class BarApiConstants {

    private BarApiConstants() {}

    // ── Chemins ───────────────────────────────────────────────────────────────

    public static final String BASE_PATH        = "/public/bars";
    public static final String BY_ID_PATH       = "/{id}";
    public static final String BY_OSM_ID_PATH   = "/osm/{osmId}";

    // ── Tag Swagger ───────────────────────────────────────────────────────────

    public static final String TAG_NAME        = "Bars";
    public static final String TAG_DESCRIPTION = "Consultation des établissements référencés dans Unseen";

    // ── GET all ───────────────────────────────────────────────────────────────

    public static final String GET_ALL_SUMMARY     = "Liste tous les bars";
    public static final String GET_ALL_DESCRIPTION = "Retourne l'ensemble des établissements présents en base. La liste peut être vide si aucun import n'a encore été effectué.";

    // ── GET by id ─────────────────────────────────────────────────────────────

    public static final String GET_BY_ID_SUMMARY     = "Récupère un bar par son identifiant interne";
    public static final String GET_BY_ID_DESCRIPTION = "Retourne le bar correspondant à l'identifiant numérique généré en base. Renvoie 404 si l'identifiant est inconnu.";
    public static final String ID_PARAM_DESCRIPTION  = "Identifiant interne du bar";
    public static final String ID_PARAM_EXAMPLE      = "1";

    // ── GET by OSM id ─────────────────────────────────────────────────────────

    public static final String GET_BY_OSM_ID_SUMMARY     = "Récupère un bar par son identifiant OSM";
    public static final String GET_BY_OSM_ID_DESCRIPTION = "Retourne le bar correspondant à l'identifiant OpenStreetMap (format `node/123456789` ou `way/123456789`). Renvoie 404 si l'identifiant est inconnu.";
    public static final String OSM_ID_PARAM_DESCRIPTION  = "Identifiant OpenStreetMap du bar";
    public static final String OSM_ID_PARAM_EXAMPLE      = "node/123456789";

    // ── Descriptions des réponses @ApiResponse ────────────────────────────────

    public static final String RESP_200_LIST       = "Liste retournée avec succès";
    public static final String RESP_200_FOUND      = "Bar trouvé";
    public static final String RESP_400_TYPE       = "L'identifiant fourni n'est pas un entier valide";
    public static final String RESP_404_BY_ID      = "Aucun bar trouvé pour cet identifiant";
    public static final String RESP_404_BY_OSM_ID  = "Aucun bar trouvé pour cet identifiant OSM";
    public static final String RESP_500            = "Erreur interne du serveur";
}
