package fr.augustinbaffou.unseen.bar.repository;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {

    Optional<Bar> findByOsmId(String osmId);

    boolean existsByOsmId(String osmId);
}
