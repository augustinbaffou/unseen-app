package fr.augustinbaffou.unseen.bargame.controller;

import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameApiConstants;
import fr.augustinbaffou.unseen.bargame.service.BarGameDeleteService;
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
public class BarGameDeleteController {

    private final BarGameDeleteService barGameDeleteService;

    public BarGameDeleteController(BarGameDeleteService barGameDeleteService) {
        this.barGameDeleteService = barGameDeleteService;
    }

    @Operation(summary = BarGameApiConstants.DELETE_SUMMARY, description = BarGameApiConstants.DELETE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = BarGameApiConstants.RESP_204_DELETED
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
    @DeleteMapping(BarGameApiConstants.BY_ID_PATH)
    public ResponseEntity<Void> execute(
            @Parameter(description = BarGameApiConstants.BAR_ID_PARAM_DESCRIPTION, example = BarGameApiConstants.BAR_ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long barId,
            @Parameter(description = BarGameApiConstants.ID_PARAM_DESCRIPTION, example = BarGameApiConstants.ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long id
    ) {
        barGameDeleteService.execute(id);
        return ResponseEntity.noContent().build();
    }
}
