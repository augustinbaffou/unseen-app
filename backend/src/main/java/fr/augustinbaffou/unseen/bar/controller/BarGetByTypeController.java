package fr.augustinbaffou.unseen.bar.controller;

import fr.augustinbaffou.unseen.bar.controller.navigation.BarApiConstants;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.entity.BarType;
import fr.augustinbaffou.unseen.bar.service.BarGetByTypeService;
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

@Tag(name = BarApiConstants.TAG_NAME, description = BarApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarApiConstants.BASE_PATH)
public class BarGetByTypeController {

    private final BarGetByTypeService barGetByTypeService;

    public BarGetByTypeController(BarGetByTypeService barGetByTypeService) {
        this.barGetByTypeService = barGetByTypeService;
    }

    @Operation(summary = BarApiConstants.GET_BY_TYPE_SUMMARY, description = BarApiConstants.GET_BY_TYPE_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarApiConstants.RESP_200_LIST_BY_TYPE,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Bar.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = BarApiConstants.RESP_400_TYPE,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping(BarApiConstants.BY_TYPE_PATH)
    public ResponseEntity<List<Bar>> execute(
            @Parameter(description = BarApiConstants.TYPE_PARAM_DESCRIPTION, example = BarApiConstants.TYPE_PARAM_EXAMPLE, required = true)
            @PathVariable BarType type
    ) {
        return ResponseEntity.ok(barGetByTypeService.execute(type));
    }
}
