/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.DTO;

import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import projekat.model.ClanOrganizacije;

/**
 *
 * @author Milos
 */
public class TimDTO implements Dto{
    private int id;

    @NotEmpty(message = "Naziv je obavezan.")
    private String naziv;

    @Positive(message = "Koordinator mora biti validan ID.")
    private ClanOrganizacijeDTO odabraniKoordinator;

    @NotNull(message = "Projekat ID je obavezan.")
    private ProjekatDTO projekat;

    private List<ClanOrganizacijeDTO> clanoviTima = new ArrayList<>();

    public TimDTO() {
    }

    public TimDTO(int id, String naziv, ClanOrganizacijeDTO odabraniKoordinator, ProjekatDTO projekatId, List<ClanOrganizacijeDTO> clanoviTimaIds) {
        this.id = id;
        this.naziv = naziv;
        this.odabraniKoordinator = odabraniKoordinator;
        this.projekat = projekatId;
        this.clanoviTima = clanoviTimaIds;
    }

    // Getteri i setteri
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public ClanOrganizacijeDTO getOdabraniKoordinator() { return odabraniKoordinator; }
    public void setOdabraniKoordinator(ClanOrganizacijeDTO odabraniKoordinator) { this.odabraniKoordinator = odabraniKoordinator; }

    public ProjekatDTO getProjekat() { return projekat; }
    public void setProjekat(ProjekatDTO projekat) { this.projekat = projekat; }

    public List<ClanOrganizacijeDTO> getClanoviTimaIds() { return clanoviTima; }
    public void setClanoviTimaIds(List<ClanOrganizacijeDTO> clanoviTimaIds) { this.clanoviTima = clanoviTimaIds; }
}