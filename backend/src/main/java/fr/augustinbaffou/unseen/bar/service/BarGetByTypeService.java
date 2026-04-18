package fr.augustinbaffou.unseen.bar.service;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.entity.BarType;
import fr.augustinbaffou.unseen.bar.repository.BarRepository;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarGetByTypeService extends BaseService<BarType, List<Bar>> {

    private final BarRepository barRepository;

    public BarGetByTypeService(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    @Override
    public List<Bar> execute(BarType type) {
        return barRepository.findByTypesContains(type);
    }
}
