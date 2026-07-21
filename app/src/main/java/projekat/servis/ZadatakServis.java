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
import projekat.repository.ProjekatRepository;
import projekat.repository.TimRepository;
import projekat.repository.ZadatakRepository;
import projekat.security.AuthContextServis;

@Service
public class ZadatakServis {

    private static final String STATUS_DODELJEN = "Dodeljen";
    private static final String STATUS_URADJEN = "Uradjen";
    private static final String STATUS_NIJE_URADJEN = "Nije uradjen";

    private final ZadatakRepository zadatakRepository;
    private final ClanOrganizacijeRepository clanRepository;
    private final ProjekatRepository projekatRepository;
    private final TimRepository timRepository;
    private final ClanOrganizacijeMapper clanMapper;
    private final AuthContextServis authContextServis;

    public ZadatakServis(
            ZadatakRepository zadatakRepository,
            ClanOrganizacijeRepository clanRepository,
            ProjekatRepository projekatRepository,
            TimRepository timRepository,
            ClanOrganizacijeMapper clanMapper,
            AuthContextServis authContextServis
    ) {
        this.zadatakRepository = zadatakRepository;
        this.clanRepository = clanRepository;
        this.projekatRepository = projekatRepository;
        this.timRepository = timRepository;
        this.clanMapper = clanMapper;
        this.authContextServis = authContextServis;
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
        if (!currentUser.isTeamLeader() && !currentUser.isUo()) {
            throw new AccessDeniedException("Samo team leader ili admin mogu da vide ove zadatke.");
        }

        return zadatakRepository.findByKreiraoId(currentUser.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ZadatakDTO create(ZadatakCreateDTO dto) throws Exception {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        if (!currentUser.isTeamLeader() && !currentUser.isUo()) {
            throw new AccessDeniedException("Samo team leader ili admin mogu da kreiraju zadatke.");
        }

        ClanOrganizacije dodeljenClanu = clanRepository.findById(dto.getDodeljenClanuId());
        Projekat projekat = dto.getProjekatId() != null ? projekatRepository.findById(dto.getProjekatId()) : null;
        Tim tim = dto.getTimId() != null ? timRepository.findById(dto.getTimId()) : null;

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
        return toDto(zadatak);
    }

    @Transactional
    public ZadatakDTO updateStatus(int zadatakId, ZadatakStatusDTO dto) throws Exception {
        ClanOrganizacije currentUser = authContextServis.getCurrentUser();
        Zadatak zadatak = zadatakRepository.findById(zadatakId);

        boolean owner = zadatak.getDodeljenClanu() != null && zadatak.getDodeljenClanu().getId() == currentUser.getId();
        boolean creator = zadatak.getKreirao() != null && zadatak.getKreirao().getId() == currentUser.getId();
        if (!owner && !creator && !currentUser.isUo()) {
            throw new AccessDeniedException("Nemate pravo da menjate status ovog zadatka.");
        }

        zadatak.setStatus(normalizeStatus(dto.getStatus()));
        zadatakRepository.save(zadatak);
        return toDto(zadatak);
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
