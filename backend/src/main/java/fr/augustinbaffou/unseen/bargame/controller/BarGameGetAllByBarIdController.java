package fr.augustinbaffou.unseen.bargame.controller;

import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameApiConstants;
import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.service.BarGameGetAllByBarIdService;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = BarGameApiConstants.TAG_NAME, description = BarGameApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarGameApiConstants.BASE_PUBLIC_PATH)
public class BarGameGetAllByBarIdController {

    private final BarGameGetAllByBarIdService barGameGetAllByBarIdService;

    public BarGameGetAllByBarIdController(BarGameGetAllByBarIdService barGameGetAllByBarIdService) {
        this.barGameGetAllByBarIdService = barGameGetAllByBarIdService;
    }

    @Operation(summary = BarGameApiConstants.GET_ALL_SUMMARY, description = BarGameApiConstants.GET_ALL_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarGameApiConstants.RESP_200_LIST,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BarGame.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarGameApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<BarGame>> execute(
            @Parameter(description = BarGameApiConstants.BAR_ID_PARAM_DESCRIPTION, example = BarGameApiConstants.BAR_ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long barId
    ) {
        return ResponseEntity.ok(barGameGetAllByBarIdService.execute(barId));
    }
}
