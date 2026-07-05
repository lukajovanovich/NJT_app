package projekat.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tim")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
public class Tim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String naziv;

    @ManyToOne
    @JoinColumn(name = "koordinator_tima_id") // posebna kolona, NE ista kao u Projekat
    private ClanOrganizacije odabraniKoordinator;

    @ManyToOne
    @JoinColumn(name= "projekat_id")
    private Projekat projekat;

    @ManyToMany(mappedBy = "timovi", fetch = FetchType.LAZY,
                cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<ClanOrganizacije> clanoviTima = new ArrayList<>();

    public Tim() {
    }

    public Tim(int id, String naziv, ClanOrganizacije odabraniKoordinator, Projekat projekat, List<ClanOrganizacije> clanoviTima) {
        this.id = id;
        this.naziv = naziv;
        this.odabraniKoordinator = odabraniKoordinator;
        this.projekat = projekat;
        this.clanoviTima = clanoviTima;
    }

    // Getteri i setteri
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public ClanOrganizacije getOdabraniKoordinator() { return odabraniKoordinator; }
    public void setOdabraniKoordinator(ClanOrganizacije odabraniKoordinator) { this.odabraniKoordinator = odabraniKoordinator; }

    public Projekat getProjekat() { return projekat; }
    public void setProjekat(Projekat projekat) { this.projekat = projekat; }

    public List<ClanOrganizacije> getClanoviTima() { return clanoviTima; }
    public void setClanoviTima(List<ClanOrganizacije> clanoviTima) { this.clanoviTima = clanoviTima; }

    @Override
    public String toString() {
        return "Tim{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", odabraniKoordinator=" + odabraniKoordinator +
                ", projekat=" + (projekat != null ? projekat.getNaziv() : "null") +
                '}';
    }
}
