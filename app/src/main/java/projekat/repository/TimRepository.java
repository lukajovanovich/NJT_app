/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import projekat.model.Tim;
import org.springframework.stereotype.Repository;
import projekat.model.Projekat;

/**
 *
 * @author Luka
 */
@Repository
public class TimRepository implements MyAppRepository<Tim, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Tim> findAll() {
        return entityManager.createQuery("SELECT t FROM Tim t", Tim.class).getResultList();
    }

    @Override
    public Tim findById(Integer id) throws Exception {
        Tim t = entityManager.find(Tim.class, id);
        if (t == null) {
            throw new Exception("Tim nije pronađen!");
        }
        return t;
    }

    @Override
    @Transactional
    public void save(Tim entity) {
        if (entity.getId() == 0) { // inicijalno id je 0
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Tim t = entityManager.find(Tim.class, id);
        if (t != null) {
            entityManager.remove(t);
        }
    }

    // 🔹 Custom metoda: traži tim po nazivu i projektu
    public Tim findByNazivAndProjekat(String naziv, Projekat projekat) {
        List<Tim> timovi = entityManager.createQuery(
                "SELECT t FROM Tim t WHERE t.naziv = :naziv AND t.projekat = :projekat", Tim.class)
                .setParameter("naziv", naziv)
                .setParameter("projekat", projekat)
                .getResultList();
        return timovi.isEmpty() ? null : timovi.get(0);
    }

    public List<String> findDistinctNazivi() {
        return entityManager.createQuery(
                "SELECT DISTINCT t.naziv FROM Tim t WHERE t.naziv IS NOT NULL AND t.naziv <> '' ORDER BY t.naziv",
                String.class
        ).getResultList();
    }
}
