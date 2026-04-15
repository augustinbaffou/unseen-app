package fr.augustinbaffou.unseen.bargame.controller;

import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameApiConstants;
import fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameExceptionConstants;
import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.service.BarGameGetByIdService;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = BarGameApiConstants.TAG_NAME, description = BarGameApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarGameApiConstants.BASE_PUBLIC_PATH)
public class BarGameGetByIdController {

    private final BarGameGetByIdService barGameGetByIdService;

    public BarGameGetByIdController(BarGameGetByIdService barGameGetByIdService) {
        this.barGameGetByIdService = barGameGetByIdService;
    }

    @Operation(summary = BarGameApiConstants.GET_BY_ID_SUMMARY, description = BarGameApiConstants.GET_BY_ID_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarGameApiConstants.RESP_200_FOUND,
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
    @GetMapping(BarGameApiConstants.BY_ID_PATH)
    public ResponseEntity<BarGame> execute(
            @Parameter(description = BarGameApiConstants.BAR_ID_PARAM_DESCRIPTION, example = BarGameApiConstants.BAR_ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long barId,
            @Parameter(description = BarGameApiConstants.ID_PARAM_DESCRIPTION, example = BarGameApiConstants.ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long id
    ) {
        return barGameGetByIdService.execute(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(BarGameExceptionConstants.RESOURCE_NAME, BarGameExceptionConstants.FIELD_ID, id));
    }
}
