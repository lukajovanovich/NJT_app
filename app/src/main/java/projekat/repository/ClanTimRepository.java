package projekat.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import projekat.model.ClanTim;

@Repository
public interface ClanTimRepository extends JpaRepository<ClanTim, Integer> {
    List<ClanTim> findByClan_Id(int clanId);
    Optional<ClanTim> findByClan_IdAndTim_Id(int clanId, int timId);
}
