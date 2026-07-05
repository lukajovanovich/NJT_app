package projekat.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

public class ClanOrganizacijeDTO implements Dto {
    private int id;

    @NotEmpty(message = "Ime je obavezno.")
    private String ime;

    @NotEmpty(message = "Prezime je obavezno.")
    private String prezime;

    @Email(message = "Email nije validan.")
    private String email;

    @NotEmpty(message = "Broj telefona je obavezan.")
    private String brojTelefona;

    private boolean uo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String sifra;

    private List<TimDTO> timovi = new ArrayList<>();

    public ClanOrganizacijeDTO() {}

    public ClanOrganizacijeDTO(
            int id,
            String ime,
            String prezime,
            String email,
            String brojTelefona,
            boolean uo,
            String sifra,
            List<TimDTO> timovi
    ) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.brojTelefona = brojTelefona;
        this.uo = uo;
        this.sifra = sifra;
        this.timovi = timovi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBrojTelefona() { return brojTelefona; }
    public void setBrojTelefona(String brojTelefona) { this.brojTelefona = brojTelefona; }

    public boolean isUo() { return uo; }
    public void setUo(boolean uo) { this.uo = uo; }

    public String getSifra() { return sifra; }
    public void setSifra(String sifra) { this.sifra = sifra; }

    public List<TimDTO> getTimoviIds() { return timovi; }
    public void setTimoviIds(List<TimDTO> timovi) { this.timovi = timovi; }
}
