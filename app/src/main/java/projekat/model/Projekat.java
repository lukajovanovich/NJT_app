package projekat.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "projekat")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
public class Projekat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String naziv;

    private Date datumPocetka;
    private Date datumZavrsetka;

    @ManyToOne
    @JoinColumn(name = "koordinator_projekta_id")
    private ClanOrganizacije odabraniKoordinator;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "projekat")
    private List<Tim> timoviNaProjektu;

    public Projekat() {}

    public Projekat(int id, String naziv, Date datumPocetka, Date datumZavrsetka, ClanOrganizacije koordinator) {
        this.id = id;
        this.naziv = naziv;
        this.datumPocetka = datumPocetka;
        this.datumZavrsetka = datumZavrsetka;
        this.odabraniKoordinator = koordinator;
    }

    // Getteri i setteri
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public Date getDatumPocetka() { return datumPocetka; }
    public void setDatumPocetka(Date datumPocetka) { this.datumPocetka = datumPocetka; }

    public Date getDatumZavrsetka() { return datumZavrsetka; }
    public void setDatumZavrsetka(Date datumZavrsetka) { this.datumZavrsetka = datumZavrsetka; }

    public ClanOrganizacije getOdabraniKoordinator() { return odabraniKoordinator; }
    public void setOdabraniKoordinator(ClanOrganizacije odabraniKoordinator) { this.odabraniKoordinator = odabraniKoordinator; }

    public List<Tim> getTimoviNaProjektu() { return timoviNaProjektu; }
    public void setTimoviNaProjektu(List<Tim> timoviNaProjektu) { this.timoviNaProjektu = timoviNaProjektu; }

    @Override
    public String toString() {
        return "Projekat{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", datumPocetka=" + datumPocetka +
                ", datumZavrsetka=" + datumZavrsetka +
                ", odabraniKoordinator=" + odabraniKoordinator +
                '}';
    }
}
