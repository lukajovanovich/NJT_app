package projekat.DTO;

import java.util.ArrayList;
import java.util.List;

public class ClanTimPregledDTO implements Dto {
    private int id;
    private int clanId;
    private int timId;
    private Integer projekatId;
    private String naziv;
    private String tim;
    private String status;
    private ClanOrganizacijeDTO clan;
    private List<ClanOrganizacijeDTO> clanoviTima = new ArrayList<>();

    public ClanTimPregledDTO() {
    }

    public ClanTimPregledDTO(
            int id,
            int clanId,
            int timId,
            Integer projekatId,
            String naziv,
            String tim,
            String status,
            ClanOrganizacijeDTO clan,
            List<ClanOrganizacijeDTO> clanoviTima
    ) {
        this.id = id;
        this.clanId = clanId;
        this.timId = timId;
        this.projekatId = projekatId;
        this.naziv = naziv;
        this.tim = tim;
        this.status = status;
        this.clan = clan;
        this.clanoviTima = clanoviTima;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClanId() { return clanId; }
    public void setClanId(int clanId) { this.clanId = clanId; }

    public int getTimId() { return timId; }
    public void setTimId(int timId) { this.timId = timId; }

    public Integer getProjekatId() { return projekatId; }
    public void setProjekatId(Integer projekatId) { this.projekatId = projekatId; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public String getTim() { return tim; }
    public void setTim(String tim) { this.tim = tim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ClanOrganizacijeDTO getClan() { return clan; }
    public void setClan(ClanOrganizacijeDTO clan) { this.clan = clan; }

    public List<ClanOrganizacijeDTO> getClanoviTima() { return clanoviTima; }
    public void setClanoviTima(List<ClanOrganizacijeDTO> clanoviTima) { this.clanoviTima = clanoviTima; }
}
