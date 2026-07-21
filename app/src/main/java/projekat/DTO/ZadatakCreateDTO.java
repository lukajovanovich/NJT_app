package projekat.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ZadatakCreateDTO implements Dto {

    @NotBlank(message = "Naslov zadatka je obavezan.")
    private String naslov;

    private String opis;

    @NotNull(message = "Clan kome se dodeljuje zadatak je obavezan.")
    @Positive(message = "Clan ID mora biti pozitivan.")
    private Integer dodeljenClanuId;

    private Integer projekatId;
    private Integer timId;

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

    public Integer getDodeljenClanuId() {
        return dodeljenClanuId;
    }

    public void setDodeljenClanuId(Integer dodeljenClanuId) {
        this.dodeljenClanuId = dodeljenClanuId;
    }

    public Integer getProjekatId() {
        return projekatId;
    }

    public void setProjekatId(Integer projekatId) {
        this.projekatId = projekatId;
    }

    public Integer getTimId() {
        return timId;
    }

    public void setTimId(Integer timId) {
        this.timId = timId;
    }
}
