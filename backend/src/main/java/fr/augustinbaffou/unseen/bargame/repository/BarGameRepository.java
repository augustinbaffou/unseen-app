package fr.augustinbaffou.unseen.bargame.repository;

import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.entity.BarGameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarGameRepository extends JpaRepository<BarGame, Long> {
    List<BarGame> findByBarId(Long barId);
    boolean existsByBarIdAndGameType(Long barId, BarGameType gameType);
}
