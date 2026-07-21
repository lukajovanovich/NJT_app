package projekat.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PrijavaCreateDTO implements Dto {

    @NotNull(message = "Clan ID je obavezan.")
    @Positive(message = "Clan ID mora biti pozitivan broj.")
    private Integer clanId;

    @NotNull(message = "Tim ID je obavezan.")
    @Positive(message = "Tim ID mora biti pozitivan broj.")
    private Integer timId;

    public Integer getClanId() {
        return clanId;
    }

    public void setClanId(Integer clanId) {
        this.clanId = clanId;
    }

    public Integer getTimId() {
        return timId;
    }

    public void setTimId(Integer timId) {
        this.timId = timId;
    }
}
