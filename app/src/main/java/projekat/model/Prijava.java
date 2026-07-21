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
@Table(name = "prijava")
public class Prijava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "clan_id", nullable = false)
    private ClanOrganizacije clan;

    @ManyToOne
    @JoinColumn(name = "tim_id", nullable = false)
    private Tim tim;

    @Column(nullable = false)
    private String status;

    @Column(name = "datum_prijave", nullable = false)
    private LocalDateTime datumPrijave;

    public Prijava() {
    }

    public Prijava(ClanOrganizacije clan, Tim tim, String status) {
        this.clan = clan;
        this.tim = tim;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        if (datumPrijave == null) {
            datumPrijave = LocalDateTime.now();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClanOrganizacije getClan() {
        return clan;
    }

    public void setClan(ClanOrganizacije clan) {
        this.clan = clan;
    }

    public Tim getTim() {
        return tim;
    }

    public void setTim(Tim tim) {
        this.tim = tim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDatumPrijave() {
        return datumPrijave;
    }

    public void setDatumPrijave(LocalDateTime datumPrijave) {
        this.datumPrijave = datumPrijave;
    }
}
