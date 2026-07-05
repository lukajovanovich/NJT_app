package projekat.Mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.TimDTO;
import projekat.model.ClanOrganizacije;
import projekat.model.Projekat;
import projekat.model.Tim;

@Component
public class TimMapper implements DtoEntityMapper<TimDTO, Tim> {

    private final ClanOrganizacijeMapper clanMapper;
    private final ProjekatMapper projekatMapper;

    public TimMapper(ClanOrganizacijeMapper clanMapper, ProjekatMapper projekatMapper) {
        this.clanMapper = clanMapper;
        this.projekatMapper = projekatMapper;
    }

    @Override
    public TimDTO toDto(Tim e) {
        if (e == null) return null;

        List<ClanOrganizacijeDTO> clanoviDto = null;
        if (e.getClanoviTima() != null) {
            clanoviDto = mapDistinctClanovi(e.getClanoviTima());
        }

        return new TimDTO(
                e.getId(),
                e.getNaziv(),
                clanMapper.toDtoBasic(e.getOdabraniKoordinator()),
                projekatMapper.toDtoBasic(e.getProjekat()),
                clanoviDto
        );
    }

    @Override
    public Tim toEntity(TimDTO t) {
        if (t == null) return null;

        List<ClanOrganizacije> clanovi = null;
        if (t.getClanoviTimaIds() != null) {
            clanovi = t.getClanoviTimaIds().stream()
                    .filter(Objects::nonNull)
                    .map(clanMapper::toEntity)
                    .collect(Collectors.toList());
        }

        ClanOrganizacije koordinator = t.getOdabraniKoordinator() != null
                ? clanMapper.toEntity(t.getOdabraniKoordinator())
                : null;

        Projekat projekat = t.getProjekat() != null
                ? projekatMapper.toEntity(t.getProjekat())
                : null;

        return new Tim(
                t.getId(),
                t.getNaziv(),
                koordinator,
                projekat,
                clanovi
        );
    }

    public TimDTO toDtoBasic(Tim e) {
        if (e == null) return null;

        return new TimDTO(
                e.getId(),
                e.getNaziv(),
                clanMapper.toDtoBasic(e.getOdabraniKoordinator()),
                projekatMapper.toDtoBasic(e.getProjekat()),
                null
        );
    }

    private List<ClanOrganizacijeDTO> mapDistinctClanovi(List<ClanOrganizacije> clanovi) {
        return new ArrayList<>(clanovi.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        ClanOrganizacije::getId,
                        clanMapper::toDtoBasic,
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values());
    }
}
