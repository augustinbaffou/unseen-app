package fr.augustinbaffou.unseen.bar.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Niveau de confiance des données de l'établissement")
public enum BarDataTrust {

    @Schema(description = "Niveau 1 — Donnée publique. Fiche importée depuis OpenStreetMap, potentiellement obsolète.")
    RAW_OSM(1),

    @Schema(description = "Niveau 2 — Validé par la communauté. Enrichie en données (photos, tags, horaires) et validée par contributions utilisateurs.")
    COMMUNITY(2),

    @Schema(description = "Niveau 3 — Vérifié par Unseen ; données garanties exactes à une date donnée.")
    VERIFIED(3),

    @Schema(description = "Niveau 4 — Certifié par le gérant. Gérée en direct par l'établissement ; priorité absolue sur toutes les autres sources.")
    CLAIMED(4);

    private final int level;

    BarDataTrust(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
