package fr.augustinbaffou.unseen.bar.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type d'établissement")
public enum BarType {

    // ── Boissons ──────────────────────────────────────────────────────────────
    COCKTAIL_BAR,
    BEER_BAR,
    CRAFT_BEER_BAR,
    BREWPUB,
    SPIRITS_BAR,
    WINE_BAR,

    // ── Ambiance / animation ───────────────────────────────────────────────────
    DANCING_BAR,
    LIVE_MUSIC_BAR,
    NIGHTCLUB,
    SPORTS_BAR,
    ARCADE_BAR,
    BOARD_GAME_BAR,
    ESPORTS_BAR,

    // ── Clientèle / style ─────────────────────────────────────────────────────
    PUB,
    STUDENT_BAR,
    LOUNGE_BAR,
    GUINGUETTE,

    // ── Restauration / café ───────────────────────────────────────────────────
    BRASSERIE,
    TAPAS_BAR,
    COFFEE_SHOP_BAR,
    CAFE_TABAC,
    PMU,

    // ── Cadre / espace ────────────────────────────────────────────────────────
    WATERFRONT_BAR,
    TERRACE_BAR,
    ROOFTOP,

    // ── Concept ───────────────────────────────────────────────────────────────
    PET_FRIENDLY
}
