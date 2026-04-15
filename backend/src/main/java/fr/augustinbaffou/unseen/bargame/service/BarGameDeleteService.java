package fr.augustinbaffou.unseen.bargame.service;

import fr.augustinbaffou.unseen.bargame.repository.BarGameRepository;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import static fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameExceptionConstants.*;

@Service
public class BarGameDeleteService extends BaseService<Long, Void> {

    private final BarGameRepository barGameRepository;

    public BarGameDeleteService(BarGameRepository barGameRepository) {
        this.barGameRepository = barGameRepository;
    }

    @Override
    public Void execute(Long id) {
        if (!barGameRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURCE_NAME, FIELD_ID, id);
        }
        barGameRepository.deleteById(id);
        return null;
    }
}
