package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import projekat.model.Prijava;

@Repository
public class PrijavaRepository implements MyAppRepository<Prijava, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Prijava> findAll() {
        return entityManager.createQuery(
                "SELECT p FROM Prijava p ORDER BY p.datumPrijave DESC",
                Prijava.class
        ).getResultList();
    }

    @Override
    public Prijava findById(Integer id) throws Exception {
        Prijava prijava = entityManager.find(Prijava.class, id);
        if (prijava == null) {
            throw new Exception("Prijava nije pronađena!");
        }
        return prijava;
    }

    @Override
    @Transactional
    public void save(Prijava entity) {
        if (entity.getId() <= 0) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Prijava prijava = entityManager.find(Prijava.class, id);
        if (prijava != null) {
            entityManager.remove(prijava);
        }
    }

    public List<Prijava> findByClanId(int clanId) {
        return entityManager.createQuery(
                "SELECT p FROM Prijava p WHERE p.clan.id = :clanId ORDER BY p.datumPrijave DESC",
                Prijava.class
        )
                .setParameter("clanId", clanId)
                .getResultList();
    }

    public List<Prijava> findByStatus(String status) {
        return entityManager.createQuery(
                "SELECT p FROM Prijava p WHERE p.status = :status ORDER BY p.datumPrijave DESC",
                Prijava.class
        )
                .setParameter("status", status)
                .getResultList();
    }

    public Prijava findAktivnaByClanIdAndTimId(int clanId, int timId, String odbijenStatus) {
        List<Prijava> prijave = entityManager.createQuery(
                """
                SELECT p
                FROM Prijava p
                WHERE p.clan.id = :clanId
                  AND p.tim.id = :timId
                  AND p.status <> :odbijenStatus
                ORDER BY p.datumPrijave DESC
                """,
                Prijava.class
        )
                .setParameter("clanId", clanId)
                .setParameter("timId", timId)
                .setParameter("odbijenStatus", odbijenStatus)
                .getResultList();

        return prijave.isEmpty() ? null : prijave.get(0);
    }
}
