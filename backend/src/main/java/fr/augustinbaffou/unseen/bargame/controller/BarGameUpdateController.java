package fr.augustinbaffou.unseen.bargame.controller;

import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameApiConstants;
import fr.augustinbaffou.unseen.bargame.dto.BarGameRequest;
import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.service.BarGameUpdateService;
import fr.augustinbaffou.unseen.bargame.service.dto.BarGameUpdateInput;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = BarGameApiConstants.TAG_NAME, description = BarGameApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarGameApiConstants.BASE_ADMIN_PATH)
public class BarGameUpdateController {

    private final BarGameUpdateService barGameUpdateService;

    public BarGameUpdateController(BarGameUpdateService barGameUpdateService) {
        this.barGameUpdateService = barGameUpdateService;
    }

    @Operation(summary = BarGameApiConstants.UPDATE_SUMMARY, description = BarGameApiConstants.UPDATE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarGameApiConstants.RESP_200_UPDATED,
                    content = @Content(schema = @Schema(implementation = BarGame.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = BarGameApiConstants.RESP_400_TYPE,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = BarGameApiConstants.RESP_404_BY_ID,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarGameApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping(BarGameApiConstants.BY_ID_PATH)
    public ResponseEntity<BarGame> execute(
            @Parameter(description = BarGameApiConstants.BAR_ID_PARAM_DESCRIPTION, example = BarGameApiConstants.BAR_ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long barId,
            @Parameter(description = BarGameApiConstants.ID_PARAM_DESCRIPTION, example = BarGameApiConstants.ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long id,
            @RequestBody BarGameRequest request
    ) {
        BarGameUpdateInput input = new BarGameUpdateInput(
                id,
                request.gameType(),
                request.quantity(),
                request.isFree(),
                request.qualityRating()
        );
        return ResponseEntity.ok(barGameUpdateService.execute(input));
    }
}
