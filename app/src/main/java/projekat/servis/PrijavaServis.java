package projekat.servis;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.ClanTimPregledDTO;
import projekat.DTO.PrijavaCreateDTO;
import projekat.DTO.PrijavaStatusDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.model.Prijava;
import projekat.model.Projekat;
import projekat.model.Tim;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.repository.ClanTimRepository;
import projekat.repository.PrijavaRepository;
import projekat.repository.TimRepository;
import projekat.security.AuthContextServis;

@Service
public class PrijavaServis {

    private static final String STATUS_PRIJAVLJEN = "Prijavljen";
    private static final String STATUS_PRIHVACEN = "Prihvacen";
    private static final String STATUS_ODBIJEN = "Odbijen";

    private final PrijavaRepository prijavaRepo;
    private final ClanOrganizacijeRepository clanRepo;
    private final TimRepository timRepo;
    private final ClanTimRepository clanTimRepo;
    private final ClanTimServis clanTimServis;
    private final ClanOrganizacijeMapper clanMapper;
    private final EmailServis emailServis;
    private final AuthContextServis authContextServis;

    public PrijavaServis(
            PrijavaRepository prijavaRepo,
            ClanOrganizacijeRepository clanRepo,
            TimRepository timRepo,
            ClanTimRepository clanTimRepo,
            ClanTimServis clanTimServis,
            ClanOrganizacijeMapper clanMapper,
            EmailServis emailServis,
            AuthContextServis authContextServis
    ) {
        this.prijavaRepo = prijavaRepo;
        this.clanRepo = clanRepo;
        this.timRepo = timRepo;
        this.clanTimRepo = clanTimRepo;
        this.clanTimServis = clanTimServis;
        this.clanMapper = clanMapper;
        this.emailServis = emailServis;
        this.authContextServis = authContextServis;
    }

    @Transactional(readOnly = true)
    public List<ClanTimPregledDTO> findByClanId(int clanId) {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (!authContextServis.isAdmin(currentUser) && currentUser.getId() != clanId) {
            throw new AccessDeniedException("Nemate pristup tudjim prijavama.");
        }

        return prijavaRepo.findByClanId(clanId).stream()
                .map(this::toPregledDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClanTimPregledDTO> findPending() {
        return prijavaRepo.findByStatus(STATUS_PRIJAVLJEN).stream()
                .map(this::toPregledDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClanTimPregledDTO create(PrijavaCreateDTO dto) throws Exception {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (currentUser.getId() != dto.getClanId() && !authContextServis.isAdmin(currentUser)) {
            throw new AccessDeniedException("Mozete kreirati samo svoju prijavu.");
        }

        ClanOrganizacije clan = clanRepo.findById(dto.getClanId());
        Tim tim = timRepo.findById(dto.getTimId());

        Prijava postojecaPrijava = prijavaRepo.findAktivnaByClanIdAndTimId(
                dto.getClanId(),
                dto.getTimId(),
                STATUS_ODBIJEN
        );

        if (postojecaPrijava != null || clanTimRepo.findByClan_IdAndTim_Id(dto.getClanId(), dto.getTimId()).isPresent()) {
            throw new IllegalStateException("Vec imate aktivnu prijavu za ovaj tim.");
        }

        Prijava prijava = new Prijava(clan, tim, STATUS_PRIJAVLJEN);
        prijavaRepo.save(prijava);

        emailServis.posaljiAdminObavestenjeONovojPrijavi(prijava);

        return toPregledDto(prijava);
    }

    @Transactional
    public ClanTimPregledDTO updateStatus(int prijavaId, PrijavaStatusDTO dto) throws Exception {
        Prijava prijava = prijavaRepo.findById(prijavaId);
        String normalizedStatus = normalizeStatus(dto.getStatus());

        if (!STATUS_PRIHVACEN.equals(normalizedStatus) && !STATUS_ODBIJEN.equals(normalizedStatus)) {
            throw new IllegalArgumentException("Status mora biti Prihvacen ili Odbijen.");
        }

        prijava.setStatus(normalizedStatus);
        prijavaRepo.save(prijava);

        if (STATUS_PRIHVACEN.equals(normalizedStatus)) {
            clanTimServis.poveziClanaSaTimom(prijava.getClan().getId(), prijava.getTim().getId());
        }

        emailServis.posaljiKorisnikuStatusPrijave(prijava);

        return toPregledDto(prijava);
    }

    private ClanTimPregledDTO toPregledDto(Prijava prijava) {
        Tim tim = prijava.getTim();
        Projekat projekat = tim != null ? tim.getProjekat() : null;

        List<ClanOrganizacijeDTO> clanoviTima = new ArrayList<>();
        if (tim != null && STATUS_PRIHVACEN.equals(prijava.getStatus()) && tim.getClanoviTima() != null) {
            clanoviTima = new ArrayList<>(tim.getClanoviTima().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            ClanOrganizacije::getId,
                            clanMapper::toDtoBasic,
                            (first, second) -> first,
                            LinkedHashMap::new
                    ))
                    .values());
        } else if (prijava.getClan() != null) {
            clanoviTima.add(clanMapper.toDtoBasic(prijava.getClan()));
        }

        return new ClanTimPregledDTO(
                prijava.getId(),
                prijava.getClan().getId(),
                tim != null ? tim.getId() : 0,
                projekat != null ? projekat.getId() : null,
                projekat != null ? projekat.getNaziv() : "Nepoznat projekat",
                tim != null ? tim.getNaziv() : "Nepoznat tim",
                prijava.getStatus(),
                clanMapper.toDtoBasic(prijava.getClan()),
                clanoviTima
        );
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status je obavezan.");
        }

        String normalized = Normalizer.normalize(status, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT);

        return switch (normalized) {
            case "prijavljen" -> STATUS_PRIJAVLJEN;
            case "prihvacen" -> STATUS_PRIHVACEN;
            case "odbijen" -> STATUS_ODBIJEN;
            default -> throw new IllegalArgumentException("Nepoznat status prijave.");
        };
    }
}
