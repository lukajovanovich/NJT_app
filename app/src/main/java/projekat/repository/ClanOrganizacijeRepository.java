package projekat.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import projekat.model.ClanOrganizacije;

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
        if (entity.getId() <= 0) {
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

    public ClanOrganizacije findByEmailAndSifra(String email, String sifra) {
        List<ClanOrganizacije> result = entityManager.createQuery(
                "SELECT c FROM ClanOrganizacije c WHERE c.email = :email AND c.sifra = :sifra",
                ClanOrganizacije.class
        )
                .setParameter("email", email)
                .setParameter("sifra", sifra)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public ClanOrganizacije findByEmail(String email) {
        List<ClanOrganizacije> result = entityManager.createQuery(
                "SELECT c FROM ClanOrganizacije c WHERE c.email = :email",
                ClanOrganizacije.class
        )
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public List<String> findAdminEmails() {
        return entityManager.createQuery(
                """
                SELECT c.email
                FROM ClanOrganizacije c
                WHERE c.admin = true
                  AND c.email IS NOT NULL
                  AND c.email <> ''
                """,
                String.class
        ).getResultList();
    }
}
