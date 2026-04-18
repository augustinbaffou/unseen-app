package fr.augustinbaffou.unseen.commun.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Corps de réponse en cas d'erreur")
public record ErrorResponse(

        @Schema(description = "Code HTTP", example = "404")
        int status,

        @Schema(description = "Libellé HTTP", example = "Not Found")
        String error,

        @Schema(description = "Message détaillé", example = "Bar not found with id: '42'")
        String message,

        @Schema(description = "Chemin de la requête", example = "/public/bars/42")
        String path,

        @Schema(description = "Horodatage UTC de l'erreur")
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now());
    }
}
