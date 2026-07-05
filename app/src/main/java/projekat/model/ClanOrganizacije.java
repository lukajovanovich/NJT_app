package projekat.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clanOrganizacije")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
public class ClanOrganizacije {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String ime;
    private String prezime;
    private String email;
    private String brojTelefona;
    private boolean uo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String sifra;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "clan_tim",
        joinColumns = @JoinColumn(name = "clan_id"),
        inverseJoinColumns = @JoinColumn(name = "tim_id")
    )
    private List<Tim> timovi;

    public ClanOrganizacije() {
        this.timovi = new ArrayList<>();
    }

    public ClanOrganizacije(int id, String ime, String prezime, String email,
                            String brojTelefona, boolean uo, String sifra, List<Tim> timovi) {
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

    public List<Tim> getTimovi() { return timovi; }
    public void setTimovi(List<Tim> timovi) { this.timovi = timovi; }

    public String getSifra() { return sifra; }
    public void setSifra(String sifra) { this.sifra = sifra; }

    @Override
    public String toString() {
        return "ClanOrganizacije{" +
                "id=" + id +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", email='" + email + '\'' +
                ", brojTelefona='" + brojTelefona + '\'' +
                ", uo=" + uo +
                ", sifra=[PROTECTED]" +
                '}';
    }
}
