package fr.augustinbaffou.unseen.bar.controller;

import fr.augustinbaffou.unseen.bar.controller.navigation.BarApiConstants;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.service.BarImportByOsmIdService;
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

@Tag(name = BarApiConstants.TAG_NAME, description = BarApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarApiConstants.BASE_ADMIN_PATH)
public class BarImportByOsmIdController {

    private final BarImportByOsmIdService barImportByOsmIdService;

    public BarImportByOsmIdController(BarImportByOsmIdService barImportByOsmIdService) {
        this.barImportByOsmIdService = barImportByOsmIdService;
    }

    @Operation(summary = BarApiConstants.IMPORT_OSM_SUMMARY, description = BarApiConstants.IMPORT_OSM_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = BarApiConstants.RESP_201_CREATED,
                    content = @Content(schema = @Schema(implementation = Bar.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = BarApiConstants.RESP_404_OSM_NOT_FOUND,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = BarApiConstants.RESP_409_ALREADY_EXISTS,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping(BarApiConstants.IMPORT_OSM_PATH)
    public ResponseEntity<Bar> execute(
            @Parameter(description = BarApiConstants.OSM_TYPE_PARAM_DESCRIPTION, example = BarApiConstants.OSM_TYPE_PARAM_EXAMPLE, required = true)
            @PathVariable String type,
            @Parameter(description = BarApiConstants.OSM_NUMERIC_ID_PARAM_DESCRIPTION, example = BarApiConstants.OSM_NUMERIC_ID_PARAM_EXAMPLE, required = true)
            @PathVariable String id
    ) {
        Bar bar = barImportByOsmIdService.execute(type + "/" + id);
        return ResponseEntity.status(HttpStatus.CREATED).body(bar);
    }
}
