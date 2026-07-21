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
import projekat.DTO.ClanTimPregledDTO;
import projekat.DTO.PrijavaCreateDTO;
import projekat.DTO.PrijavaStatusDTO;
import projekat.servis.PrijavaServis;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/prijava")
public class PrijavaController {

    private final PrijavaServis prijavaServis;

    public PrijavaController(PrijavaServis prijavaServis) {
        this.prijavaServis = prijavaServis;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ClanTimPregledDTO>> getPending() {
        return new ResponseEntity<>(prijavaServis.findPending(), HttpStatus.OK);
    }

    @GetMapping("/byClan/{clanId}")
    public ResponseEntity<List<ClanTimPregledDTO>> getByClan(@PathVariable int clanId) {
        try {
            return new ResponseEntity<>(prijavaServis.findByClanId(clanId), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ClanTimPregledDTO> create(@Valid @RequestBody @NotNull PrijavaCreateDTO dto) {
        try {
            return new ResponseEntity<>(prijavaServis.create(dto), HttpStatus.CREATED);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ClanTimPregledDTO> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody @NotNull PrijavaStatusDTO dto
    ) {
        try {
            return new ResponseEntity<>(prijavaServis.updateStatus(id, dto), HttpStatus.OK);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }
}
