/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.kontroler;

import projekat.DTO.ProjekatDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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
import projekat.servis.ProjekatServis;
/**
 *
 * @author Luka
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/projekat")
public class ProjekatController {

    private final ProjekatServis projekatService;

    public ProjekatController(ProjekatServis projekatService) {
        this.projekatService = projekatService;
    }

    @GetMapping
    public ResponseEntity<List<ProjekatDTO>> getAll() {
        List<ProjekatDTO> all = projekatService.findAll();
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjekatDTO> getById(
            @NotNull @PathVariable("id") int id) {
        try {
            ProjekatDTO dto = projekatService.findById(id);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "ProjekatController exception: " + ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ProjekatDTO> addProjekat(
            @Valid @RequestBody @NotNull ProjekatDTO projekatDto) {
        try {
            ProjekatDTO saved = projekatService.create(projekatDto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while saving projekat: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjekat(@PathVariable("id") int id) {
        try {
            projekatService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while deleting projekat: " + ex.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<ProjekatDTO> updateProjekat(
            @Valid @RequestBody @NotNull ProjekatDTO projekatDto) {
        try {
            ProjekatDTO updated = projekatService.update(projekatDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error while updating projekat: " + ex.getMessage());
        }
    }
}
