package fr.augustinbaffou.unseen.bargame.service;

import fr.augustinbaffou.unseen.bar.entity.Bar;
import fr.augustinbaffou.unseen.bar.repository.BarRepository;
import fr.augustinbaffou.unseen.bargame.entity.BarGame;
import fr.augustinbaffou.unseen.bargame.repository.BarGameRepository;
import fr.augustinbaffou.unseen.bargame.service.dto.BarGameCreateInput;
import fr.augustinbaffou.unseen.commun.exception.ResourceAlreadyExistsException;
import fr.augustinbaffou.unseen.commun.exception.ResourceNotFoundException;
import fr.augustinbaffou.unseen.commun.service.BaseService;
import org.springframework.stereotype.Service;

import static fr.augustinbaffou.unseen.bar.controller.navigation.BarExceptionConstants.FIELD_ID;
import static fr.augustinbaffou.unseen.bar.controller.navigation.BarExceptionConstants.RESOURCE_NAME;
import static fr.augustinbaffou.unseen.bargame.controller.navigation.BarGameExceptionConstants.*;

@Service
public class BarGameCreateService extends BaseService<BarGameCreateInput, BarGame> {

    private final BarRepository barRepository;
    private final BarGameRepository barGameRepository;

    public BarGameCreateService(BarRepository barRepository, BarGameRepository barGameRepository) {
        this.barRepository = barRepository;
        this.barGameRepository = barGameRepository;
    }

    @Override
    public BarGame execute(BarGameCreateInput input) {
        Bar bar = barRepository.findById(input.barId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, FIELD_ID, input.barId()));

        if (barGameRepository.existsByBarIdAndGameType(input.barId(), input.gameType())) {
            throw new ResourceAlreadyExistsException(RESOURCE_NAME, FIELD_GAME_TYPE, input.gameType());
        }

        BarGame barGame = new BarGame();
        barGame.setBar(bar);
        barGame.setGameType(input.gameType());
        barGame.setQuantity(input.quantity());
        barGame.setIsFree(input.isFree());
        barGame.setQualityRating(input.qualityRating());

        return barGameRepository.save(barGame);
    }
}
