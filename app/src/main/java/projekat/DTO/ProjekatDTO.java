package projekat.DTO;

import jakarta.validation.constraints.*;
import java.sql.Date;

public class ProjekatDTO implements Dto {

    private int id;
    @NotEmpty(message = "Naziv je obavezan.")
    private String Naziv;
    @NotNull(message = "Datum početka je obavezan.")
    private Date DatumPocetka;
    @NotNull(message = "Datum završetka je obavezan.")
    private Date DatumZavrsetka;
    private ClanOrganizacijeDTO OdabraniKoordinator;

    public ProjekatDTO() {}

    public ProjekatDTO(int id, String naziv, Date datumPocetka, Date datumZavrsetka, ClanOrganizacijeDTO odabraniKoordinator) {
        this.id = id;
        this.Naziv = naziv;
        this.DatumPocetka = datumPocetka;
        this.DatumZavrsetka = datumZavrsetka;
        this.OdabraniKoordinator = odabraniKoordinator;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return Naziv; }
    public void setNaziv(String naziv) { this.Naziv = naziv; }

    public Date getDatumPocetka() { return DatumPocetka; }
    public void setDatumPocetka(Date datumPocetka) { this.DatumPocetka = datumPocetka; }

    public Date getDatumZavrsetka() { return DatumZavrsetka; }
    public void setDatumZavrsetka(Date datumZavrsetka) { this.DatumZavrsetka = datumZavrsetka; }

    public ClanOrganizacijeDTO getOdabraniKoordinator() { return OdabraniKoordinator; }
    public void setOdabraniKoordinator(ClanOrganizacijeDTO odabraniKoordinator) { this.OdabraniKoordinator = odabraniKoordinator; }
}
