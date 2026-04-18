package fr.augustinbaffou.unseen.bargame.service.dto;

import fr.augustinbaffou.unseen.bargame.entity.BarGameType;

public record BarGameCreateInput(
        Long barId,
        BarGameType gameType,
        Integer quantity,
        Boolean isFree,
        Integer qualityRating
) {}
