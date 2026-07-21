/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import projekat.model.Projekat;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Milos
 */
@Repository
public class ProjekatRepository implements MyAppRepository<Projekat, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Projekat> findAll() {
        return entityManager.createQuery("SELECT p FROM Projekat p", Projekat.class).getResultList();
    }

    @Override
    public Projekat findById(Integer id) throws Exception {
        Projekat projekat = entityManager.find(Projekat.class, id);
        if (projekat == null) {
            throw new Exception("Projekat nije pronađen!");
        }
        return projekat;
    }

    @Override
    @Transactional
    public void save(Projekat entity) {
        if (entity.getId() <= 0) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Projekat projekat = entityManager.find(Projekat.class, id);
        if (projekat != null) {
            entityManager.remove(projekat);
        }
    }
}
