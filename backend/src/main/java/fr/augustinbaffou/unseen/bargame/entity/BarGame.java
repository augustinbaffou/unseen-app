package fr.augustinbaffou.unseen.bargame.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.augustinbaffou.unseen.bar.entity.Bar;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Jeu de bar disponible dans un établissement")
@Entity
@Table(name = "bar_games", uniqueConstraints = @UniqueConstraint(name = "uq_bar_games_bar_type", columnNames = {"bar_id", "game_type"}))
public class BarGame {

    @Schema(description = "Identifiant interne", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @Schema(description = "Type de jeu de bar", example = "BABYFOOT")
    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private BarGameType gameType;

    @Schema(description = "Nombre d'exemplaires disponibles", example = "2", minimum = "1")
    @Column(nullable = false)
    private Integer quantity;

    @Schema(description = "Indique si le jeu est en accès gratuit", example = "true")
    @Column(name = "is_free", nullable = false)
    private Boolean isFree;

    @Schema(description = "Note de qualité de l'équipement (0 = mauvais état, 5 = excellent état)", example = "4", minimum = "0", maximum = "5")
    @Column(name = "quality_rating", nullable = false)
    private Integer qualityRating;

    public BarGame() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bar getBar() { return bar; }
    public void setBar(Bar bar) { this.bar = bar; }

    public BarGameType getGameType() { return gameType; }
    public void setGameType(BarGameType gameType) { this.gameType = gameType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }

    public Integer getQualityRating() { return qualityRating; }
    public void setQualityRating(Integer qualityRating) { this.qualityRating = qualityRating; }
}
