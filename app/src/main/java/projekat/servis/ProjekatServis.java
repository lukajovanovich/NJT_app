package projekat.servis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import projekat.DTO.ProjekatDTO;
import projekat.Mapper.ProjekatMapper;
import projekat.model.Projekat;
import projekat.model.Tim;
import projekat.repository.ProjekatRepository;
import projekat.repository.TimRepository;

@Service
public class ProjekatServis {

    private final ProjekatRepository projekatRepository;
    private final ProjekatMapper projekatMapper;
    private final TimRepository timRepository;
    private final String defaultTeamNamesRaw;

    public ProjekatServis(
            ProjekatRepository projekatRepository,
            ProjekatMapper projekatMapper,
            TimRepository timRepository,
            @Value("${app.default-team-names:Analiza,Dizajn,Razvoj,Testiranje}") String defaultTeamNamesRaw
    ) {
        this.projekatRepository = projekatRepository;
        this.projekatMapper = projekatMapper;
        this.timRepository = timRepository;
        this.defaultTeamNamesRaw = defaultTeamNamesRaw;
    }

    public List<ProjekatDTO> findAll() {
        return projekatRepository.findAll()
                .stream()
                .map(projekatMapper::toDto)
                .collect(Collectors.toList());
    }

    public ProjekatDTO findById(int id) throws Exception {
        return projekatMapper.toDto(projekatRepository.findById(id));
    }

    @Transactional
    public ProjekatDTO create(ProjekatDTO dto) {
        Projekat projekat = projekatMapper.toEntity(dto);
        projekatRepository.save(projekat);
        kreirajPodrazumevaneTimove(projekat);
        return projekatMapper.toDto(projekat);
    }

    public void deleteById(int id) {
        projekatRepository.deleteById(id);
    }

    @Transactional
    public ProjekatDTO update(ProjekatDTO dto) {
        try {
            Projekat postojeci = projekatRepository.findById(dto.getId());
            postojeci.setNaziv(dto.getNaziv());
            postojeci.setDatumPocetka(dto.getDatumPocetka());
            postojeci.setDatumZavrsetka(dto.getDatumZavrsetka());
            postojeci.setOdabraniKoordinator(
                    dto.getOdabraniKoordinator() != null
                            ? projekatMapper.toEntity(dto).getOdabraniKoordinator()
                            : null
            );
            projekatRepository.save(postojeci);
            return projekatMapper.toDto(postojeci);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private void kreirajPodrazumevaneTimove(Projekat projekat) {
        for (String nazivTima : resolveTemplateTeamNames()) {
            if (timRepository.findByNazivAndProjekat(nazivTima, projekat) == null) {
                Tim tim = new Tim(0, nazivTima, null, projekat, new ArrayList<>());
                timRepository.save(tim);
            }
        }
    }

    private List<String> resolveTemplateTeamNames() {
        List<String> postojeciNazivi = timRepository.findDistinctNazivi();
        if (!postojeciNazivi.isEmpty()) {
            return postojeciNazivi;
        }

        return new ArrayList<>(new LinkedHashSet<>(Arrays.stream(defaultTeamNamesRaw.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toList())));
    }
}
