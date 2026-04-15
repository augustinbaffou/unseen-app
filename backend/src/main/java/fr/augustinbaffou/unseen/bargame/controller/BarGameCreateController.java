package fr.augustinbaffou.unseen.bargame.controller;

import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameApiConstants;
import fr.augustinbaffou.unseen.bargame.dto.BarGameRequest;
import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.service.BarGameCreateService;
import fr.augustinbaffou.unseen.bargame.service.dto.BarGameCreateInput;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = BarGameApiConstants.TAG_NAME, description = BarGameApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarGameApiConstants.BASE_ADMIN_PATH)
public class BarGameCreateController {

    private final BarGameCreateService barGameCreateService;

    public BarGameCreateController(BarGameCreateService barGameCreateService) {
        this.barGameCreateService = barGameCreateService;
    }

    @Operation(summary = BarGameApiConstants.CREATE_SUMMARY, description = BarGameApiConstants.CREATE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = BarGameApiConstants.RESP_201_CREATED,
                    content = @Content(schema = @Schema(implementation = BarGame.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = BarGameApiConstants.RESP_404_BAR,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = BarGameApiConstants.RESP_409_ALREADY_EXISTS,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarGameApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<BarGame> execute(
            @Parameter(description = BarGameApiConstants.BAR_ID_PARAM_DESCRIPTION, example = BarGameApiConstants.BAR_ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long barId,
            @RequestBody BarGameRequest request
    ) {
        BarGameCreateInput input = new BarGameCreateInput(
                barId,
                request.gameType(),
                request.quantity(),
                request.isFree(),
                request.qualityRating()
        );
        BarGame created = barGameCreateService.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
