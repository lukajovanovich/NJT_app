package projekat.Mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import projekat.DTO.ClanOrganizacijeDTO;
import projekat.DTO.TimDTO;
import projekat.model.ClanOrganizacije;
import projekat.model.Tim;

@Component
public class ClanOrganizacijeMapper implements DtoEntityMapper<ClanOrganizacijeDTO, ClanOrganizacije> {

    private final TimMapper timMapper;

    public ClanOrganizacijeMapper(@Lazy TimMapper timMapper) {
        this.timMapper = timMapper;
    }

    @Override
    public ClanOrganizacijeDTO toDto(ClanOrganizacije e) {
        if (e == null) return null;

        List<TimDTO> timoviDto = null;
        if (e.getTimovi() != null) {
            timoviDto = mapDistinctTimovi(e.getTimovi());
        }

        return new ClanOrganizacijeDTO(
                e.getId(),
                e.getIme(),
                e.getPrezime(),
                e.getEmail(),
                e.getBrojTelefona(),
                e.isUo(),
                null,
                timoviDto
        );
    }

    @Override
    public ClanOrganizacije toEntity(ClanOrganizacijeDTO c) {
        if (c == null) return null;

        List<Tim> timovi = null;
        if (c.getTimoviIds() != null) {
            timovi = c.getTimoviIds().stream()
                    .filter(Objects::nonNull)
                    .map(timMapper::toEntity)
                    .collect(Collectors.toList());
        }

        return new ClanOrganizacije(
                c.getId(),
                c.getIme(),
                c.getPrezime(),
                c.getEmail(),
                c.getBrojTelefona(),
                c.isUo(),
                c.getSifra(),
                timovi
        );
    }

    public ClanOrganizacijeDTO toDtoBasic(ClanOrganizacije e) {
        if (e == null) return null;

        return new ClanOrganizacijeDTO(
                e.getId(),
                e.getIme(),
                e.getPrezime(),
                e.getEmail(),
                e.getBrojTelefona(),
                e.isUo(),
                null,
                null
        );
    }

    private List<TimDTO> mapDistinctTimovi(List<Tim> timovi) {
        return new ArrayList<>(timovi.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Tim::getId,
                        timMapper::toDtoBasic,
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values());
    }
}
