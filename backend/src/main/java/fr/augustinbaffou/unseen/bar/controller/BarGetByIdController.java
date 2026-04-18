package fr.augustinbaffou.unseen.bar.controller;

import fr.augustinbaffou.unseen.bar.controller.navigation.BarApiConstants;
import fr.augustinbaffou.unseen.bar.controller.navigation.BarExceptionConstants;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.service.BarGetByIdService;
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

@Tag(name = BarApiConstants.TAG_NAME, description = BarApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarApiConstants.BASE_PATH)
public class BarGetByIdController {

    private final BarGetByIdService barGetByIdService;

    public BarGetByIdController(BarGetByIdService barGetByIdService) {
        this.barGetByIdService = barGetByIdService;
    }

    @Operation(summary = BarApiConstants.GET_BY_ID_SUMMARY, description = BarApiConstants.GET_BY_ID_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarApiConstants.RESP_200_FOUND,
                    content = @Content(schema = @Schema(implementation = Bar.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = BarApiConstants.RESP_400_TYPE,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = BarApiConstants.RESP_404_BY_ID,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(BarApiConstants.BY_ID_PATH)
    public ResponseEntity<Bar> execute(
            @Parameter(description = BarApiConstants.ID_PARAM_DESCRIPTION, example = BarApiConstants.ID_PARAM_EXAMPLE, required = true)
            @PathVariable Long id
    ) {
        return barGetByIdService.execute(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(BarExceptionConstants.RESOURCE_NAME, BarExceptionConstants.FIELD_ID, id));
    }
}
