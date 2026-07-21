package projekat.kontroler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import projekat.DTO.ZadatakCreateDTO;
import projekat.DTO.ZadatakDTO;
import projekat.DTO.ZadatakStatusDTO;
import projekat.servis.ZadatakServis;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/zadatak")
public class ZadatakController {

    private final ZadatakServis zadatakServis;

    public ZadatakController(ZadatakServis zadatakServis) {
        this.zadatakServis = zadatakServis;
    }

    @GetMapping("/my/assigned")
    public ResponseEntity<List<ZadatakDTO>> getMyTasks() {
        return new ResponseEntity<>(zadatakServis.findAssignedToCurrentUser(), HttpStatus.OK);
    }

    @GetMapping("/leader/created")
    public ResponseEntity<List<ZadatakDTO>> getLeaderTasks() {
        return new ResponseEntity<>(zadatakServis.findCreatedByCurrentLeader(), HttpStatus.OK);
    }

    @PostMapping("/leader")
    public ResponseEntity<ZadatakDTO> createTask(@Valid @RequestBody @NotNull ZadatakCreateDTO dto) {
        try {
            return new ResponseEntity<>(zadatakServis.create(dto), HttpStatus.CREATED);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/my/{id}/status")
    public ResponseEntity<ZadatakDTO> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody @NotNull ZadatakStatusDTO dto
    ) {
        try {
            return new ResponseEntity<>(zadatakServis.updateStatus(id, dto), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
