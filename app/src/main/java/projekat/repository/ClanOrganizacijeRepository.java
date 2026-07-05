/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import projekat.model.ClanOrganizacije;

/**
 *
 * @author Luka
 */
@Repository
public class ClanOrganizacijeRepository implements MyAppRepository<ClanOrganizacije, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ClanOrganizacije> findAll() {
        return entityManager.createQuery("SELECT co FROM ClanOrganizacije co", ClanOrganizacije.class)
                .getResultList();
    }

    @Override
    public ClanOrganizacije findById(Integer id) throws Exception {
        ClanOrganizacije co = entityManager.find(ClanOrganizacije.class, id);
        if (co == null) {
            throw new Exception("Clan organizacije nije pronađen!");
        }
        return co;
    }

    @Override
    @Transactional
    public void save(ClanOrganizacije entity) {
        if (entity.getId() == -1) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        ClanOrganizacije co = entityManager.find(ClanOrganizacije.class, id);
        if (co != null) {
            entityManager.remove(co);
        }
    }

    // ✅ DODATO: metoda za login proveru
    public ClanOrganizacije findByEmailAndSifra(String email, String sifra) {
        List<ClanOrganizacije> result = entityManager.createQuery(
            "SELECT c FROM ClanOrganizacije c WHERE c.email = :email AND c.sifra = :sifra",
            ClanOrganizacije.class)
            .setParameter("email", email)
            .setParameter("sifra", sifra)
            .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }
}