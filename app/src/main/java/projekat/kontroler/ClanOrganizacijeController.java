package projekat.kontroler;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
import projekat.DTO.AuthResponseDTO;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.LoginDTO;
import projekat.servis.ClanOrganizacijeServis;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/clan-organizacije")
public class ClanOrganizacijeController {

    private final ClanOrganizacijeServis clanService;

    public ClanOrganizacijeController(ClanOrganizacijeServis clanService) {
        this.clanService = clanService;
    }

    @GetMapping
    public ResponseEntity<List<ClanOrganizacijeDTO>> getAll() {
        return new ResponseEntity<>(clanService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/rang-lista")
    public ResponseEntity<List<ClanOrganizacijeDTO>> getRangLista() {
        return new ResponseEntity<>(clanService.findRangLista(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClanOrganizacijeDTO> getById(@NotNull @PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(clanService.findById(id), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ClanOrganizacijeController exception: " + ex.getMessage()
            );
        }
    }

    @PostMapping
    public ResponseEntity<ClanOrganizacijeDTO> addClan(@Valid @RequestBody @NotNull ClanOrganizacijeDTO clanDto) {
        try {
            ClanOrganizacijeDTO saved = clanService.create(clanDto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error while saving clan organizacije: " + ex.getMessage()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClan(@PathVariable("id") int id) {
        try {
            clanService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error while deleting clan organizacije: " + ex.getMessage()
            );
        }
    }

    @PutMapping
    public ResponseEntity<ClanOrganizacijeDTO> updateClan(@Valid @RequestBody @NotNull ClanOrganizacijeDTO clanDto) {
        try {
            ClanOrganizacijeDTO updated = clanService.update(clanDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Error while updating clan organizacije: " + ex.getMessage()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            return ResponseEntity.ok(clanService.login(loginDTO.getEmail(), loginDTO.getSifra()));
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Greska na serveru: " + ex.getMessage()
            );
        }
    }
}
