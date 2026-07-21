package projekat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "zadatak")
public class Zadatak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String naslov;

    @Column(length = 2000)
    private String opis;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "dodeljen_clanu_id", nullable = false)
    private ClanOrganizacije dodeljenClanu;

    @ManyToOne
    @JoinColumn(name = "kreirao_id", nullable = false)
    private ClanOrganizacije kreirao;

    @ManyToOne
    @JoinColumn(name = "projekat_id")
    private Projekat projekat;

    @ManyToOne
    @JoinColumn(name = "tim_id")
    private Tim tim;

    @Column(name = "datum_kreiranja", nullable = false)
    private LocalDateTime datumKreiranja;

    public Zadatak() {
    }

    public Zadatak(
            String naslov,
            String opis,
            String status,
            ClanOrganizacije dodeljenClanu,
            ClanOrganizacije kreirao,
            Projekat projekat,
            Tim tim
    ) {
        this.naslov = naslov;
        this.opis = opis;
        this.status = status;
        this.dodeljenClanu = dodeljenClanu;
        this.kreirao = kreirao;
        this.projekat = projekat;
        this.tim = tim;
    }

    @PrePersist
    void onCreate() {
        if (datumKreiranja == null) {
            datumKreiranja = LocalDateTime.now();
        }
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

    public ClanOrganizacije getDodeljenClanu() {
        return dodeljenClanu;
    }

    public void setDodeljenClanu(ClanOrganizacije dodeljenClanu) {
        this.dodeljenClanu = dodeljenClanu;
    }

    public ClanOrganizacije getKreirao() {
        return kreirao;
    }

    public void setKreirao(ClanOrganizacije kreirao) {
        this.kreirao = kreirao;
    }

    public Projekat getProjekat() {
        return projekat;
    }

    public void setProjekat(Projekat projekat) {
        this.projekat = projekat;
    }

    public Tim getTim() {
        return tim;
    }

    public void setTim(Tim tim) {
        this.tim = tim;
    }

    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
}
