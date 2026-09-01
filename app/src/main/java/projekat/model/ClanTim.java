/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.model;

import jakarta.persistence.*;

/**
 *
 * @author Luka
 */
@Entity
@Table(name = "clan_tim", uniqueConstraints = @UniqueConstraint(columnNames = {"clan_id", "tim_id"}))
public class ClanTim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "clan_id")
    private ClanOrganizacije clan;

    @ManyToOne
    @JoinColumn(name = "tim_id")
    private Tim tim;

    public ClanTim() {}

    public ClanTim(ClanOrganizacije clan, Tim tim) {
        this.clan = clan;
        this.tim = tim;
    }

    // Getteri i setteri
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
}
