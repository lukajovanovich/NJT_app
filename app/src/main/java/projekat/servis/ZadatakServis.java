package projekat.servis;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekat.DTO.ZadatakCreateDTO;
import projekat.DTO.ZadatakDTO;
import projekat.DTO.ZadatakStatusDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.model.Projekat;
import projekat.model.Tim;
import projekat.model.Zadatak;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.repository.ClanTimRepository;
import projekat.repository.ProjekatRepository;
import projekat.repository.TimRepository;
import projekat.repository.ZadatakRepository;
import projekat.security.AuthContextServis;

@Service
public class ZadatakServis {

    private static final String STATUS_DODELJEN = "Dodeljen";
    private static final String STATUS_URADJEN = "Uradjen";
    private static final String STATUS_NIJE_URADJEN = "Nije uradjen";

    private static final int POENI_ZA_URADJEN = 10;
    private static final int POENI_ZA_NIJE_URADJEN = -3;

    private final ZadatakRepository zadatakRepository;
    private final ClanOrganizacijeRepository clanRepository;
    private final ProjekatRepository projekatRepository;
    private final TimRepository timRepository;
    private final ClanTimRepository clanTimRepository;
    private final ClanOrganizacijeMapper clanMapper;
    private final AuthContextServis authContextServis;
    private final EmailServis emailServis;

    public ZadatakServis(
            ZadatakRepository zadatakRepository,
            ClanOrganizacijeRepository clanRepository,
            ProjekatRepository projekatRepository,
            TimRepository timRepository,
            ClanTimRepository clanTimRepository,
            ClanOrganizacijeMapper clanMapper,
            AuthContextServis authContextServis,
            EmailServis emailServis
    ) {
        this.zadatakRepository = zadatakRepository;
        this.clanRepository = clanRepository;
        this.projekatRepository = projekatRepository;
        this.timRepository = timRepository;
        this.clanTimRepository = clanTimRepository;
        this.clanMapper = clanMapper;
        this.authContextServis = authContextServis;
        this.emailServis = emailServis;
    }

    @Transactional(readOnly = true)
    public List<ZadatakDTO> findAssignedToCurrentUser() {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        return zadatakRepository.findByDodeljenClanuId(currentUser.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ZadatakDTO> findCreatedByCurrentLeader() {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (!currentUser.isTeamLeader() && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Samo team leader ili admin mogu da vide ove zadatke.");
        }

        return zadatakRepository.findByKreiraoId(currentUser.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ZadatakDTO create(ZadatakCreateDTO dto) throws Exception {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (!currentUser.isTeamLeader() && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Samo team leader ili admin mogu da kreiraju zadatke.");
        }

        ClanOrganizacije dodeljenClanu = clanRepository.findById(dto.getDodeljenClanuId());
        Projekat projekat = dto.getProjekatId() != null ? projekatRepository.findById(dto.getProjekatId()) : null;
        Tim tim = dto.getTimId() != null ? timRepository.findById(dto.getTimId()) : null;

        if (tim != null && clanTimRepository.findByClan_IdAndTim_Id(dodeljenClanu.getId(), tim.getId()).isEmpty()) {
            throw new IllegalArgumentException("Izabrani korisnik nije član izabranog tima.");
        }

        Zadatak zadatak = new Zadatak(
                dto.getNaslov().trim(),
                dto.getOpis(),
                STATUS_DODELJEN,
                dodeljenClanu,
                currentUser,
                projekat,
                tim
        );

        zadatakRepository.save(zadatak);
        emailServis.posaljiObavestenjeONovomZadatku(zadatak);
        return toDto(zadatak);
    }

    @Transactional
    public ZadatakDTO updateStatus(int zadatakId, ZadatakStatusDTO dto) throws Exception {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        Zadatak zadatak = zadatakRepository.findById(zadatakId);

        boolean owner = zadatak.getDodeljenClanu() != null && zadatak.getDodeljenClanu().getId() == currentUser.getId();
        boolean creator = zadatak.getKreirao() != null && zadatak.getKreirao().getId() == currentUser.getId();
        if (!owner && !creator && !currentUser.isAdmin()) {
            throw new AccessDeniedException("Nemate pravo da menjate status ovog zadatka.");
        }

        String stariStatus = zadatak.getStatus();
        String noviStatus = normalizeStatus(dto.getStatus());

        if (!stariStatus.equals(noviStatus)) {
            azurirajBodove(zadatak.getDodeljenClanu(), stariStatus, noviStatus);
        }

        zadatak.setStatus(noviStatus);
        zadatakRepository.save(zadatak);
        return toDto(zadatak);
    }

    private void azurirajBodove(ClanOrganizacije clan, String stariStatus, String noviStatus) {
        if (clan == null) {
            return;
        }

        int delta = 0;

        if (STATUS_URADJEN.equals(noviStatus) && !STATUS_URADJEN.equals(stariStatus)) {
            delta += POENI_ZA_URADJEN;
        } else if (STATUS_URADJEN.equals(stariStatus) && !STATUS_URADJEN.equals(noviStatus)) {
            delta -= POENI_ZA_URADJEN;
        }

        if (STATUS_NIJE_URADJEN.equals(noviStatus) && !STATUS_NIJE_URADJEN.equals(stariStatus)) {
            delta += POENI_ZA_NIJE_URADJEN;
        } else if (STATUS_NIJE_URADJEN.equals(stariStatus) && !STATUS_NIJE_URADJEN.equals(noviStatus)) {
            delta -= POENI_ZA_NIJE_URADJEN;
        }

        if (delta != 0) {
            clan.setBodovi(clan.getBodovi() + delta);
            clanRepository.save(clan);
        }
    }

    private ZadatakDTO toDto(Zadatak zadatak) {
        Integer projekatId = zadatak.getProjekat() != null ? zadatak.getProjekat().getId() : null;
        Integer timId = zadatak.getTim() != null ? zadatak.getTim().getId() : null;

        return new ZadatakDTO(
                zadatak.getId(),
                zadatak.getNaslov(),
                zadatak.getOpis(),
                zadatak.getStatus(),
                projekatId,
                zadatak.getProjekat() != null ? zadatak.getProjekat().getNaziv() : null,
                timId,
                zadatak.getTim() != null ? zadatak.getTim().getNaziv() : null,
                clanMapper.toDtoBasic(zadatak.getDodeljenClanu()),
                clanMapper.toDtoBasic(zadatak.getKreirao()),
                zadatak.getDatumKreiranja()
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
            case "dodeljen" -> STATUS_DODELJEN;
            case "uradjen" -> STATUS_URADJEN;
            case "nije uradjen", "nije_uradjen", "nijeuradjen" -> STATUS_NIJE_URADJEN;
            default -> throw new IllegalArgumentException("Nepoznat status zadatka.");
        };
    }
}
