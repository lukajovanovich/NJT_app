package projekat.servis;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.Mapper.ClanOrganizacijeMapper;
import projekat.model.ClanOrganizacije;
import projekat.repository.ClanOrganizacijeRepository;

@Service
public class ClanOrganizacijeServis {

    private final ClanOrganizacijeRepository clanOrganizacijeRepository;
    private final ClanOrganizacijeMapper clanOrganizacijeMapper;

    @Autowired
    public ClanOrganizacijeServis(ClanOrganizacijeRepository clanOrganizacijeRepository,
                                  ClanOrganizacijeMapper clanOrganizacijeMapper) {
        this.clanOrganizacijeRepository = clanOrganizacijeRepository;
        this.clanOrganizacijeMapper = clanOrganizacijeMapper;
    }

    public List<ClanOrganizacijeDTO> findAll() {
        return clanOrganizacijeRepository.findAll()
                .stream()
                .map(clanOrganizacijeMapper::toDto)
                .collect(Collectors.toList());
    }

    public ClanOrganizacijeDTO findById(int id) throws Exception {
        return clanOrganizacijeMapper.toDto(clanOrganizacijeRepository.findById(id));
    }

    public ClanOrganizacijeDTO create(ClanOrganizacijeDTO dto) {
        ClanOrganizacije clan = clanOrganizacijeMapper.toEntity(dto);
        clanOrganizacijeRepository.save(clan);
        return clanOrganizacijeMapper.toDto(clan);
    }

    public void deleteById(int id) {
        clanOrganizacijeRepository.deleteById(id);
    }

    public ClanOrganizacijeDTO update(ClanOrganizacijeDTO dto) {
        ClanOrganizacije updated = clanOrganizacijeMapper.toEntity(dto);
        clanOrganizacijeRepository.save(updated);
        return clanOrganizacijeMapper.toDto(updated);
    }

    public ClanOrganizacijeDTO login(String email, String sifra) {
        ClanOrganizacije korisnik = clanOrganizacijeRepository.findByEmailAndSifra(email, sifra);
        return clanOrganizacijeMapper.toDtoBasic(korisnik);
    }
}
