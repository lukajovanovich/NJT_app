package projekat.servis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import projekat.DTO.AuthResponseDTO;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.repository.PrijavaRepository;
import projekat.repository.ZadatakRepository;
import projekat.security.JwtServis;

/**
 * Testovi za prijavu korisnika: uspešan login sa ispravnom šifrom
 * i odbijanje pristupa kada šifra ne odgovara sačuvanom hešu.
 */
@ExtendWith(MockitoExtension.class)
class ClanOrganizacijeServisTest {

    @Mock private ClanOrganizacijeRepository clanOrganizacijeRepository;
    @Mock private ClanOrganizacijeMapper clanOrganizacijeMapper;
    @Mock private JwtServis jwtServis;
    @Mock private PrijavaRepository prijavaRepository;
    @Mock private ZadatakRepository zadatakRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private ClanOrganizacijeServis clanOrganizacijeServis;

    @BeforeEach
    void setUp() {
        clanOrganizacijeServis = new ClanOrganizacijeServis(
                clanOrganizacijeRepository,
                clanOrganizacijeMapper,
                passwordEncoder,
                jwtServis,
                prijavaRepository,
                zadatakRepository
        );
    }

    @Test
    void login_vracaToken_ispravnaLozinka() {
        ClanOrganizacije korisnik = new ClanOrganizacije();
        korisnik.setId(1);
        korisnik.setEmail("student@fon.bg.ac.rs");
        korisnik.setSifra(passwordEncoder.encode("Lozinka123!"));

        when(clanOrganizacijeRepository.findByEmail("student@fon.bg.ac.rs")).thenReturn(korisnik);
        when(clanOrganizacijeMapper.toDtoBasic(any())).thenReturn(new ClanOrganizacijeDTO());
        when(jwtServis.generateToken(korisnik)).thenReturn("fake-jwt-token");

        AuthResponseDTO odgovor = clanOrganizacijeServis.login("student@fon.bg.ac.rs", "Lozinka123!");

        assertEquals("fake-jwt-token", odgovor.getToken());
    }

    @Test
    void login_bacaAccessDenied_pogresnaLozinka() {
        ClanOrganizacije korisnik = new ClanOrganizacije();
        korisnik.setId(1);
        korisnik.setEmail("student@fon.bg.ac.rs");
        korisnik.setSifra(passwordEncoder.encode("Lozinka123!"));

        when(clanOrganizacijeRepository.findByEmail("student@fon.bg.ac.rs")).thenReturn(korisnik);

        assertThrows(
                AccessDeniedException.class,
                () -> clanOrganizacijeServis.login("student@fon.bg.ac.rs", "PogresnaLozinka")
        );
    }

    @Test
    void login_bacaAccessDenied_korisnikNePostoji() {
        when(clanOrganizacijeRepository.findByEmail("nepostojeci@fon.bg.ac.rs")).thenReturn(null);

        assertThrows(
                AccessDeniedException.class,
                () -> clanOrganizacijeServis.login("nepostojeci@fon.bg.ac.rs", "bilokoja")
        );
    }
}
