package projekat.servis;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekat.DTO.AuthResponseDTO;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.security.JwtServis;

@Service
public class ClanOrganizacijeServis {

    private final ClanOrganizacijeRepository clanOrganizacijeRepository;
    private final ClanOrganizacijeMapper clanOrganizacijeMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtServis jwtServis;

    public ClanOrganizacijeServis(
            ClanOrganizacijeRepository clanOrganizacijeRepository,
            ClanOrganizacijeMapper clanOrganizacijeMapper,
            PasswordEncoder passwordEncoder,
            JwtServis jwtServis
    ) {
        this.clanOrganizacijeRepository = clanOrganizacijeRepository;
        this.clanOrganizacijeMapper = clanOrganizacijeMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtServis = jwtServis;
    }

    public List<ClanOrganizacijeDTO> findAll() {
        return clanOrganizacijeRepository.findAll()
                .stream()
                .map(clanOrganizacijeMapper::toDtoBasic)
                .collect(Collectors.toList());
    }

    public ClanOrganizacijeDTO findById(int id) throws Exception {
        return clanOrganizacijeMapper.toDto(clanOrganizacijeRepository.findById(id));
    }

    @Transactional
    public ClanOrganizacijeDTO create(ClanOrganizacijeDTO dto) {
        validateUniqueEmail(dto.getEmail(), 0);

        if (dto.getSifra() == null || dto.getSifra().isBlank()) {
            throw new IllegalArgumentException("Sifra je obavezna.");
        }

        ClanOrganizacije clan = clanOrganizacijeMapper.toEntity(dto);
        clan.setSifra(passwordEncoder.encode(dto.getSifra()));
        clanOrganizacijeRepository.save(clan);
        return clanOrganizacijeMapper.toDtoBasic(clan);
    }

    public void deleteById(int id) {
        clanOrganizacijeRepository.deleteById(id);
    }

    @Transactional
    public ClanOrganizacijeDTO update(ClanOrganizacijeDTO dto) {
        try {
            ClanOrganizacije postojeci = clanOrganizacijeRepository.findById(dto.getId());
            validateUniqueEmail(dto.getEmail(), dto.getId());

            postojeci.setIme(dto.getIme());
            postojeci.setPrezime(dto.getPrezime());
            postojeci.setEmail(dto.getEmail());
            postojeci.setBrojTelefona(dto.getBrojTelefona());
            postojeci.setUo(dto.isUo());
            postojeci.setTeamLeader(dto.isTeamLeader());

            if (dto.getSifra() != null && !dto.getSifra().isBlank()) {
                postojeci.setSifra(passwordEncoder.encode(dto.getSifra()));
            }

            clanOrganizacijeRepository.save(postojeci);
            return clanOrganizacijeMapper.toDtoBasic(postojeci);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    @Transactional
    public AuthResponseDTO login(String email, String sifra) {
        ClanOrganizacije korisnik = clanOrganizacijeRepository.findByEmail(email);
        if (korisnik == null || !passwordMatches(sifra, korisnik.getSifra())) {
            throw new AccessDeniedException("Pogresan email ili sifra.");
        }

        if (korisnik.getSifra() != null && !isEncoded(korisnik.getSifra())) {
            korisnik.setSifra(passwordEncoder.encode(sifra));
            clanOrganizacijeRepository.save(korisnik);
        }

        String token = jwtServis.generateToken(korisnik);
        return new AuthResponseDTO(token, clanOrganizacijeMapper.toDtoBasic(korisnik));
    }

    private void validateUniqueEmail(String email, int currentUserId) {
        ClanOrganizacije postojeci = clanOrganizacijeRepository.findByEmail(email);
        if (postojeci != null && postojeci.getId() != currentUserId) {
            throw new IllegalStateException("Korisnik sa tim emailom vec postoji.");
        }
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }

        if (rawPassword.equals(storedPassword)) {
            return true;
        }

        return isEncoded(storedPassword) && passwordEncoder.matches(rawPassword, storedPassword);
    }

    private boolean isEncoded(String password) {
        return password != null && password.startsWith("$2");
    }
}
