package projekat.kontroler;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import projekat.DTO.ClanTimPregledDTO;
import projekat.model.ClanTim;
import projekat.servis.ClanTimServis;

@RestController
@RequestMapping("/api/clanTim")
@CrossOrigin(origins = "http://localhost:4200")
public class ClanTimController {

    @Autowired
    private ClanTimServis clanTimServis;

    @GetMapping("/byClanDTO/{clanId}")
    public List<ClanTimPregledDTO> findByClanId(@PathVariable int clanId) {
        try {
            return clanTimServis.findByClanId(clanId);
        } catch (AccessDeniedException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
    }

    @PostMapping("/povezi")
    public ClanTim poveziClanaSaTimom(@RequestParam int clanId, @RequestParam int timId) throws Exception {
        return clanTimServis.poveziClanaSaTimom(clanId, timId);
    }
}
