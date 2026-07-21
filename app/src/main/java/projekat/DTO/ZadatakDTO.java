package projekat.DTO;

import java.time.LocalDateTime;

public class ZadatakDTO implements Dto {

    private int id;
    private String naslov;
    private String opis;
    private String status;
    private Integer projekatId;
    private String projekatNaziv;
    private Integer timId;
    private String timNaziv;
    private ClanOrganizacijeDTO dodeljenClanu;
    private ClanOrganizacijeDTO kreirao;
    private LocalDateTime datumKreiranja;

    public ZadatakDTO() {
    }

    public ZadatakDTO(
            int id,
            String naslov,
            String opis,
            String status,
            Integer projekatId,
            String projekatNaziv,
            Integer timId,
            String timNaziv,
            ClanOrganizacijeDTO dodeljenClanu,
            ClanOrganizacijeDTO kreirao,
            LocalDateTime datumKreiranja
    ) {
        this.id = id;
        this.naslov = naslov;
        this.opis = opis;
        this.status = status;
        this.projekatId = projekatId;
        this.projekatNaziv = projekatNaziv;
        this.timId = timId;
        this.timNaziv = timNaziv;
        this.dodeljenClanu = dodeljenClanu;
        this.kreirao = kreirao;
        this.datumKreiranja = datumKreiranja;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getProjekatId() {
        return projekatId;
    }

    public void setProjekatId(Integer projekatId) {
        this.projekatId = projekatId;
    }

    public String getProjekatNaziv() {
        return projekatNaziv;
    }

    public void setProjekatNaziv(String projekatNaziv) {
        this.projekatNaziv = projekatNaziv;
    }

    public Integer getTimId() {
        return timId;
    }

    public void setTimId(Integer timId) {
        this.timId = timId;
    }

    public String getTimNaziv() {
        return timNaziv;
    }

    public void setTimNaziv(String timNaziv) {
        this.timNaziv = timNaziv;
    }

    public ClanOrganizacijeDTO getDodeljenClanu() {
        return dodeljenClanu;
    }

    public void setDodeljenClanu(ClanOrganizacijeDTO dodeljenClanu) {
        this.dodeljenClanu = dodeljenClanu;
    }

    public ClanOrganizacijeDTO getKreirao() {
        return kreirao;
    }

    public void setKreirao(ClanOrganizacijeDTO kreirao) {
        this.kreirao = kreirao;
    }

    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
}
