package fr.augustinbaffou.unseen.bargame.dto;

import fr.augustinbaffou.unseen.bargame.entity.BarGameType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Corps de requête pour créer ou modifier un jeu de bar")
public record BarGameRequest(

        @Schema(description = "Type de jeu", example = "BABYFOOT", requiredMode = Schema.RequiredMode.REQUIRED)
        BarGameType gameType,

        @Schema(description = "Nombre d'exemplaires disponibles", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer quantity,

        @Schema(description = "Indique si le jeu est en accès gratuit", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        Boolean isFree,

        @Schema(description = "Note de qualité de l'équipement (0 = mauvais état, 5 = excellent état)", example = "4", minimum = "0", maximum = "5", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer qualityRating
) {}
