package projekat.kontroler;

import projekat.DTO.TimDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import projekat.servis.TimServis;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/tim")
public class TimController {

    private final TimServis timService;

    public TimController(TimServis timService) {
        this.timService = timService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all Tim entities.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = TimDTO.class),
                 mediaType = "application/json")
    })
    public ResponseEntity<List<TimDTO>> getAll() {
        List<TimDTO> all = timService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a Tim by ID.")
    public ResponseEntity<TimDTO> getById(
            @NotNull(message = "Should not be null or empty.")
            @PathVariable("id") int id) { // ⬅️ promenjeno u int
        try {
            TimDTO dto = timService.findById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "TimController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new Tim entity.")
    @ApiResponse(responseCode = "201", content = {
        @Content(schema = @Schema(implementation = TimDTO.class),
                 mediaType = "application/json")
    })
    public ResponseEntity<TimDTO> addTim(
            @Valid @RequestBody @NotNull TimDTO timDto) {
        try {
            TimDTO saved = timService.create(timDto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while saving tim: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Tim by ID.")
    @ApiResponse(responseCode = "204", description = "Tim deleted successfully.")
    public ResponseEntity<Void> deleteTim(
            @PathVariable("id") int id) { // ⬅️ promenjeno u int
        try {
            timService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while deleting tim: " + ex.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "Update an existing Tim entity.")
    @ApiResponse(responseCode = "200", content = {
        @Content(schema = @Schema(implementation = TimDTO.class),
                 mediaType = "application/json")
    })
    public ResponseEntity<TimDTO> updateTim(
            @Valid @RequestBody @NotNull TimDTO timDto) {
        try {
            TimDTO updated = timService.update(timDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while updating tim: " + ex.getMessage());
        }
    }
}
