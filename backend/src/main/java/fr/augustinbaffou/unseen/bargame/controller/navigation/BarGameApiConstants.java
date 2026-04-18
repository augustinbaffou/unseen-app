package fr.augustinbaffou.unseen.bargame.controller.navigation;

public final class BarGameApiConstants {

    private BarGameApiConstants() {}

    // ── Chemins ───────────────────────────────────────────────────────────────

    public static final String BASE_PUBLIC_PATH = "/public/bars/{barId}/games";
    public static final String BASE_ADMIN_PATH  = "/admin/bars/{barId}/games";
    public static final String BY_ID_PATH       = "/{id}";

    // ── Tag Swagger ───────────────────────────────────────────────────────────

    public static final String TAG_NAME        = "Jeux de bar";
    public static final String TAG_DESCRIPTION = "Gestion des jeux disponibles dans les établissements";

    // ── GET all by bar ────────────────────────────────────────────────────────

    public static final String GET_ALL_SUMMARY     = "Liste les jeux d'un bar";
    public static final String GET_ALL_DESCRIPTION = "Retourne l'ensemble des jeux disponibles dans l'établissement identifié. La liste peut être vide si aucun jeu n'est référencé.";

    // ── GET by id ─────────────────────────────────────────────────────────────

    public static final String GET_BY_ID_SUMMARY     = "Récupère un jeu par son identifiant";
    public static final String GET_BY_ID_DESCRIPTION = "Retourne le jeu correspondant à l'identifiant numérique. Renvoie 404 si l'identifiant est inconnu.";

    // ── POST create ───────────────────────────────────────────────────────────

    public static final String CREATE_SUMMARY     = "Ajoute un jeu à un bar";
    public static final String CREATE_DESCRIPTION = "Crée une entrée jeu pour l'établissement identifié. Renvoie 409 si ce type de jeu est déjà référencé pour ce bar.";

    // ── PUT update ────────────────────────────────────────────────────────────

    public static final String UPDATE_SUMMARY     = "Met à jour un jeu de bar";
    public static final String UPDATE_DESCRIPTION = "Modifie les informations (quantité, gratuité, qualité) d'un jeu existant. Renvoie 404 si l'identifiant est inconnu.";

    // ── DELETE ────────────────────────────────────────────────────────────────

    public static final String DELETE_SUMMARY     = "Supprime un jeu d'un bar";
    public static final String DELETE_DESCRIPTION = "Retire le jeu identifié de l'établissement. Renvoie 404 si l'identifiant est inconnu.";

    // ── Paramètres ────────────────────────────────────────────────────────────

    public static final String BAR_ID_PARAM_DESCRIPTION = "Identifiant interne du bar";
    public static final String BAR_ID_PARAM_EXAMPLE     = "1";
    public static final String ID_PARAM_DESCRIPTION     = "Identifiant interne du jeu";
    public static final String ID_PARAM_EXAMPLE         = "1";

    // ── Descriptions des réponses @ApiResponse ────────────────────────────────

    public static final String RESP_200_LIST           = "Liste retournée avec succès";
    public static final String RESP_200_FOUND          = "Jeu trouvé";
    public static final String RESP_200_UPDATED        = "Jeu mis à jour avec succès";
    public static final String RESP_201_CREATED        = "Jeu créé avec succès";
    public static final String RESP_204_DELETED        = "Jeu supprimé avec succès";
    public static final String RESP_400_TYPE           = "L'identifiant fourni n'est pas un entier valide";
    public static final String RESP_404_BAR            = "Aucun bar trouvé pour cet identifiant";
    public static final String RESP_404_BY_ID          = "Aucun jeu trouvé pour cet identifiant";
    public static final String RESP_409_ALREADY_EXISTS = "Ce type de jeu est déjà référencé pour ce bar";
    public static final String RESP_500                = "Erreur interne du serveur";
}
