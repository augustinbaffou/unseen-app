package fr.augustinbaffou.unseen.bargame.service;

import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.repository.BarGameRepository;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BarGameGetByIdService extends BaseService<Long, Optional<BarGame>> {

    private final BarGameRepository barGameRepository;

    public BarGameGetByIdService(BarGameRepository barGameRepository) {
        this.barGameRepository = barGameRepository;
    }

    @Override
    public Optional<BarGame> execute(Long id) {
        return barGameRepository.findById(id);
    }
}
