package projekat.servis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.ClanTimPregledDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.model.ClanTim;
import projekat.model.Projekat;
import projekat.model.Tim;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.repository.ClanTimRepository;
import projekat.repository.TimRepository;
import projekat.security.AuthContextServis;

@Service
public class ClanTimServis {

    private final ClanTimRepository clanTimRepo;
    private final ClanOrganizacijeRepository clanRepo;
    private final TimRepository timRepo;
    private final ClanOrganizacijeMapper clanMapper;
    private final AuthContextServis authContextServis;

    public ClanTimServis(
            ClanTimRepository clanTimRepo,
            ClanOrganizacijeRepository clanRepo,
            TimRepository timRepo,
            ClanOrganizacijeMapper clanMapper,
            AuthContextServis authContextServis
    ) {
        this.clanTimRepo = clanTimRepo;
        this.clanRepo = clanRepo;
        this.timRepo = timRepo;
        this.clanMapper = clanMapper;
        this.authContextServis = authContextServis;
    }

    @Transactional(readOnly = true)
    public List<ClanTimPregledDTO> findByClanId(int clanId) {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (!authContextServis.isAdmin(currentUser) && currentUser.getId() != clanId) {
            throw new AccessDeniedException("Nemate pristup tudjim timovima.");
        }

        return new ArrayList<>(clanTimRepo.findByClan_Id(clanId).stream()
                .filter(veza -> veza.getClan() != null && veza.getTim() != null)
                .collect(Collectors.toMap(
                        veza -> veza.getTim().getId(),
                        this::toPregledDto,
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values());
    }

    @Transactional
    public ClanTim poveziClanaSaTimom(int clanId, int timId) throws Exception {
        ClanOrganizacije clan = clanRepo.findById(clanId);
        Tim tim = timRepo.findById(timId);
        if (clan == null || tim == null) {
            throw new RuntimeException("Clan ili tim ne postoji!");
        }

        ClanTim postojecaVeza = clanTimRepo.findByClan_IdAndTim_Id(clanId, timId).orElse(null);
        if (postojecaVeza != null) {
            return postojecaVeza;
        }

        ClanTim veza = new ClanTim(clan, tim);
        return clanTimRepo.save(veza);
    }

    private ClanTimPregledDTO toPregledDto(ClanTim veza) {
        Tim tim = veza.getTim();
        Projekat projekat = tim.getProjekat();

        List<ClanOrganizacijeDTO> clanoviTima = new ArrayList<>();
        if (tim.getClanoviTima() != null) {
            clanoviTima = new ArrayList<>(tim.getClanoviTima().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            ClanOrganizacije::getId,
                            clanMapper::toDtoBasic,
                            (first, second) -> first,
                            LinkedHashMap::new
                    ))
                    .values());
        }

        return new ClanTimPregledDTO(
                veza.getId(),
                veza.getClan().getId(),
                tim.getId(),
                projekat != null ? projekat.getId() : null,
                projekat != null ? projekat.getNaziv() : "Nepoznat projekat",
                tim.getNaziv(),
                "Prihvacen",
                clanMapper.toDtoBasic(veza.getClan()),
                clanoviTima
        );
    }
}
