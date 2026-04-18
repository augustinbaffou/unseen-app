package fr.augustinbaffou.unseen.bargame.service;

import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.repository.BarGameRepository;
import fr.augustinbaffou.unseen.bargame.service.dto.BarGameUpdateInput;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import static fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameExceptionConstants.*;

@Service
public class BarGameUpdateService extends BaseService<BarGameUpdateInput, BarGame> {

    private final BarGameRepository barGameRepository;

    public BarGameUpdateService(BarGameRepository barGameRepository) {
        this.barGameRepository = barGameRepository;
    }

    @Override
    public BarGame execute(BarGameUpdateInput input) {
        BarGame barGame = barGameRepository.findById(input.id())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, FIELD_ID, input.id()));

        barGame.setGameType(input.gameType());
        barGame.setQuantity(input.quantity());
        barGame.setIsFree(input.isFree());
        barGame.setQualityRating(input.qualityRating());

        return barGameRepository.save(barGame);
    }
}
