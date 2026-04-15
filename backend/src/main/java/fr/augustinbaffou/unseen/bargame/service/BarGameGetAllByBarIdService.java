package fr.augustinbaffou.unseen.bargame.service;

import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.repository.BarGameRepository;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarGameGetAllByBarIdService extends BaseService<Long, List<BarGame>> {

    private final BarGameRepository barGameRepository;

    public BarGameGetAllByBarIdService(BarGameRepository barGameRepository) {
        this.barGameRepository = barGameRepository;
    }

    @Override
    public List<BarGame> execute(Long barId) {
        return barGameRepository.findByBarId(barId);
    }
}
