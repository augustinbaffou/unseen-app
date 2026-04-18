package fr.augustinbaffou.unseen.bargame.service.dto;

import fr.augustinbaffou.unseen.bargame.entity.BarGameType;

public record BarGameUpdateInput(
        Long id,
        BarGameType gameType,
        Integer quantity,
        Boolean isFree,
        Integer qualityRating
) {}
