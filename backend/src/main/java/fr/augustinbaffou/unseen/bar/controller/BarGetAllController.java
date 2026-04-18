package fr.augustinbaffou.unseen.bar.controller;

import fr.augustinbaffou.unseen.bar.controller.navigation.BarApiConstants;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.service.BarGetAllService;
import fr.augustinbaffou.unseen.commun.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = BarApiConstants.TAG_NAME, description = BarApiConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(BarApiConstants.BASE_PATH)
public class BarGetAllController {

    private final BarGetAllService barGetAllService;

    public BarGetAllController(BarGetAllService barGetAllService) {
        this.barGetAllService = barGetAllService;
    }

    @Operation(summary = BarApiConstants.GET_ALL_SUMMARY, description = BarApiConstants.GET_ALL_DESCRIPTION)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = BarApiConstants.RESP_200_LIST,
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Bar.class)))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = BarApiConstants.RESP_500,
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<Bar>> execute() {
        return ResponseEntity.ok(barGetAllService.execute());
    }
}
