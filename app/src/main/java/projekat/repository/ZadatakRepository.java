package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import projekat.model.Zadatak;

@Repository
public class ZadatakRepository implements MyAppRepository<Zadatak, Integer> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Zadatak> findAll() {
        return entityManager.createQuery(
                "SELECT z FROM Zadatak z ORDER BY z.datumKreiranja DESC",
                Zadatak.class
        ).getResultList();
    }

    @Override
    public Zadatak findById(Integer id) throws Exception {
        Zadatak zadatak = entityManager.find(Zadatak.class, id);
        if (zadatak == null) {
            throw new Exception("Zadatak nije pronađen.");
        }
        return zadatak;
    }

    @Override
    @Transactional
    public void save(Zadatak entity) {
        if (entity.getId() <= 0) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Zadatak zadatak = entityManager.find(Zadatak.class, id);
        if (zadatak != null) {
            entityManager.remove(zadatak);
        }
    }

    public List<Zadatak> findByDodeljenClanuId(int clanId) {
        return entityManager.createQuery(
                """
                SELECT z
                FROM Zadatak z
                WHERE z.dodeljenClanu.id = :clanId
                ORDER BY z.datumKreiranja DESC
                """,
                Zadatak.class
        )
                .setParameter("clanId", clanId)
                .getResultList();
    }

    public List<Zadatak> findByKreiraoId(int clanId) {
        return entityManager.createQuery(
                """
                SELECT z
                FROM Zadatak z
                WHERE z.kreirao.id = :clanId
                ORDER BY z.datumKreiranja DESC
                """,
                Zadatak.class
        )
                .setParameter("clanId", clanId)
                .getResultList();
    }
}
