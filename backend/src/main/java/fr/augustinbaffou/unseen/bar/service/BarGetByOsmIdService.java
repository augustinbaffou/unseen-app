package fr.augustinbaffou.unseen.bar.service;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.repository.BarRepository;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BarGetByOsmIdService extends BaseService<String, Optional<Bar>> {

    private final BarRepository barRepository;

    public BarGetByOsmIdService(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    @Override
    public Optional<Bar> execute(String osmId) {
        return barRepository.findByOsmId(osmId);
    }
}
