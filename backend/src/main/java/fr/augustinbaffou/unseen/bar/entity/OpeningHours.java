package fr.augustinbaffou.unseen.bar.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Schema(description = "Créneau d'ouverture d'un établissement")
@Entity
@Table(name = "opening_hours")
public class OpeningHours {

    @Schema(description = "Identifiant interne", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @Schema(description = "Type de créneau : horaires du bar ou de la cuisine", example = "BAR")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OpeningHoursType type;

    @Schema(description = "Jour de la semaine", example = "MONDAY")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Schema(description = "Heure d'ouverture (00:00 si ouvert toute la journée)", example = "17:00")
    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Schema(description = "Heure de fermeture (00:00 si ouvert toute la journée ; peut être inférieure à opens_at si fermeture après minuit)", example = "02:00")
    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    public OpeningHours() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bar getBar() { return bar; }
    public void setBar(Bar bar) { this.bar = bar; }

    public OpeningHoursType getType() { return type; }
    public void setType(OpeningHoursType type) { this.type = type; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getOpensAt() { return opensAt; }
    public void setOpensAt(LocalTime opensAt) { this.opensAt = opensAt; }

    public LocalTime getClosesAt() { return closesAt; }
    public void setClosesAt(LocalTime closesAt) { this.closesAt = closesAt; }
}
