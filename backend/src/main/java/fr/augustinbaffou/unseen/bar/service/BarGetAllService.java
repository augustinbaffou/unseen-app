package fr.augustinbaffou.unseen.bar.service;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.repository.BarRepository;
import fr.augustinbaffou.unseen.commun.service.BaseQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BarGetAllService extends BaseQueryService<List<Bar>> {

    private final BarRepository barRepository;

    public BarGetAllService(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    @Override
    public List<Bar> execute() {
        return barRepository.findAll();
    }
}
