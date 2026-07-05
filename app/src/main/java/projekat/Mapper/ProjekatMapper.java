package projekat.Mapper;

import org.springframework.stereotype.Component;
import projekat.DTO.ProjekatDTO;
import projekat.model.ClanOrganizacije;
import projekat.model.Projekat;
import projekat.DTO.ClanOrganizacijeDTO;

@Component
public class ProjekatMapper implements DtoEntityMapper<ProjekatDTO, Projekat> {

    private final ClanOrganizacijeMapper clanMapper;

    public ProjekatMapper(ClanOrganizacijeMapper clanMapper) {
        this.clanMapper = clanMapper;
    }

    @Override
    public ProjekatDTO toDto(Projekat e) {
        if (e == null) return null;

        ClanOrganizacijeDTO koordinatorDto = null;
        if (e.getOdabraniKoordinator() != null) {
            koordinatorDto = clanMapper.toDtoBasic(e.getOdabraniKoordinator()); // ⬅️ basic
        }

        return new ProjekatDTO(
                e.getId(),
                e.getNaziv(),
                e.getDatumPocetka(),
                e.getDatumZavrsetka(),
                koordinatorDto
        );
    }

    @Override
    public Projekat toEntity(ProjekatDTO t) {
        if (t == null) return null;

        ClanOrganizacije koordinator = null;
        if (t.getOdabraniKoordinator() != null) {
            koordinator = clanMapper.toEntity(t.getOdabraniKoordinator());
        }

        return new Projekat(
                t.getId(),
                t.getNaziv(),
                t.getDatumPocetka(),
                t.getDatumZavrsetka(),
                koordinator
        );
    }

    // Basic mapiranje – ne mapira koordinatora
    public ProjekatDTO toDtoBasic(Projekat e) {
        if (e == null) return null;
        return new ProjekatDTO(
                e.getId(),
                e.getNaziv(),
                e.getDatumPocetka(),
                e.getDatumZavrsetka(),
                null
        );
    }
}
