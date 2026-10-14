package projekat.servis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.ZadatakCreateDTO;
import projekat.DTO.ZadatakStatusDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.model.Tim;
import projekat.model.Zadatak;
import projekat.repository.ClanOrganizacijeRepository;
import projekat.repository.ClanTimRepository;
import projekat.repository.ProjekatRepository;
import projekat.repository.TimRepository;
import projekat.repository.ZadatakRepository;
import projekat.security.AuthContextServis;

/**
 * Testovi za poslovnu logiku ZadatakServis-a: ovlašćenja pri kreiranju
 * i promeni statusa zadatka, kao i obračun bodova (+10 za urađen, -3 za nije urađen).
 */
@ExtendWith(MockitoExtension.class)
class ZadatakServisTest {

    @Mock private ZadatakRepository zadatakRepository;
    @Mock private ClanOrganizacijeRepository clanRepository;
    @Mock private ProjekatRepository projekatRepository;
    @Mock private TimRepository timRepository;
    @Mock private ClanTimRepository clanTimRepository;
    @Mock private ClanOrganizacijeMapper clanMapper;
    @Mock private AuthContextServis authContextServis;
    @Mock private EmailServis emailServis;

    @InjectMocks
    private ZadatakServis zadatakServis;

    private ClanOrganizacije obicanKorisnik;
    private ClanOrganizacije voditeljTima;

    @BeforeEach
    void setUp() {
        obicanKorisnik = new ClanOrganizacije();
        obicanKorisnik.setId(1);
        obicanKorisnik.setAdmin(false);
        obicanKorisnik.setRukovodilac(false);

        voditeljTima = new ClanOrganizacije();
        voditeljTima.setId(2);
        voditeljTima.setAdmin(false);
        voditeljTima.setRukovodilac(true);
    }

    @Test
    void create_bacaAccessDenied_korisnikNijeRukovodilacNiAdmin() {
        when(authContextServis.getCurrentUser()).thenReturn(obicanKorisnik);

        ZadatakCreateDTO dto = new ZadatakCreateDTO();
        dto.setNaslov("Testiranje modula");
        dto.setDodeljenClanuId(5);

        assertThrows(AccessDeniedException.class, () -> zadatakServis.create(dto));
    }

    @Test
    void create_bacaIllegalArgument_clanNijeClanIzabranogTima() throws Exception {
        when(authContextServis.getCurrentUser()).thenReturn(voditeljTima);

        ClanOrganizacije dodeljenClanu = new ClanOrganizacije();
        dodeljenClanu.setId(7);

        Tim tim = new Tim();
        tim.setId(3);

        when(clanRepository.findById(7)).thenReturn(dodeljenClanu);
        when(timRepository.findById(3)).thenReturn(tim);
        when(clanTimRepository.findByClan_IdAndTim_Id(7, 3)).thenReturn(Optional.empty());

        ZadatakCreateDTO dto = new ZadatakCreateDTO();
        dto.setNaslov("Testiranje modula");
        dto.setDodeljenClanuId(7);
        dto.setTimId(3);

        assertThrows(IllegalArgumentException.class, () -> zadatakServis.create(dto));
    }

    @Test
    void updateStatus_dodeljujeBodove_zadatakUradjen() throws Exception {
        ClanOrganizacije dodeljen = new ClanOrganizacije();
        dodeljen.setId(1);
        dodeljen.setBodovi(0);

        Zadatak zadatak = new Zadatak();
        zadatak.setId(10);
        zadatak.setStatus("Dodeljen");
        zadatak.setDodeljenClanu(dodeljen);
        zadatak.setKreirao(voditeljTima);

        when(authContextServis.getCurrentUser()).thenReturn(dodeljen);
        when(zadatakRepository.findById(10)).thenReturn(zadatak);
        when(clanMapper.toDtoBasic(any())).thenReturn(new ClanOrganizacijeDTO());

        ZadatakStatusDTO statusDTO = new ZadatakStatusDTO();
        statusDTO.setStatus("Uradjen");

        zadatakServis.updateStatus(10, statusDTO);

        assertEquals(10, dodeljen.getBodovi());
        verify(clanRepository).save(dodeljen);
    }

    @Test
    void updateStatus_oduzimaBodove_zadatakVracenNaDodeljen() throws Exception {
        ClanOrganizacije dodeljen = new ClanOrganizacije();
        dodeljen.setId(1);
        dodeljen.setBodovi(10);

        Zadatak zadatak = new Zadatak();
        zadatak.setId(11);
        zadatak.setStatus("Uradjen");
        zadatak.setDodeljenClanu(dodeljen);
        zadatak.setKreirao(voditeljTima);

        when(authContextServis.getCurrentUser()).thenReturn(voditeljTima);
        when(zadatakRepository.findById(11)).thenReturn(zadatak);
        when(clanMapper.toDtoBasic(any())).thenReturn(new ClanOrganizacijeDTO());

        ZadatakStatusDTO statusDTO = new ZadatakStatusDTO();
        statusDTO.setStatus("Dodeljen");

        zadatakServis.updateStatus(11, statusDTO);

        assertEquals(0, dodeljen.getBodovi());
    }

    @Test
    void updateStatus_bacaAccessDenied_korisnikNemaPravoNaIzmenu() throws Exception {
        ClanOrganizacije dodeljen = new ClanOrganizacije();
        dodeljen.setId(1);

        ClanOrganizacije drugiKorisnik = new ClanOrganizacije();
        drugiKorisnik.setId(99);
        drugiKorisnik.setAdmin(false);

        Zadatak zadatak = new Zadatak();
        zadatak.setId(12);
        zadatak.setStatus("Dodeljen");
        zadatak.setDodeljenClanu(dodeljen);
        zadatak.setKreirao(voditeljTima);

        when(authContextServis.getCurrentUser()).thenReturn(drugiKorisnik);
        when(zadatakRepository.findById(12)).thenReturn(zadatak);

        ZadatakStatusDTO statusDTO = new ZadatakStatusDTO();
        statusDTO.setStatus("Uradjen");

        assertThrows(AccessDeniedException.class, () -> zadatakServis.updateStatus(12, statusDTO));
    }
}
