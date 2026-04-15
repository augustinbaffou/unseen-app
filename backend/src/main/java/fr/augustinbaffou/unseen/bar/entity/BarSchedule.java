package fr.augustinbaffou.unseen.bar.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Schema(description = "Créneau horaire d'un établissement (ouverture bar, cuisine ou happy hour)")
@Entity
@Table(name = "bar_schedules")
public class BarSchedule {

    @Schema(description = "Identifiant interne", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @Schema(description = "Type de créneau : horaires du bar, de la cuisine ou happy hour", example = "BAR")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BarScheduleType type;

    @Schema(description = "Jour de la semaine", example = "MONDAY")
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Schema(description = "Heure de début (00:00 si ouvert toute la journée)", example = "17:00")
    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Schema(description = "Heure de fin (00:00 si ouvert toute la journée ; peut être inférieure à opens_at si fermeture après minuit)", example = "20:00")
    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    @Schema(description = "Détails de l'offre happy hour (uniquement si type = HAPPY_HOUR)", example = "Pinte à 5€ / Cocktail -30%")
    @Column(name = "happy_hour_details")
    private String happyHourDetails;

    public BarSchedule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Bar getBar() { return bar; }
    public void setBar(Bar bar) { this.bar = bar; }

    public BarScheduleType getType() { return type; }
    public void setType(BarScheduleType type) { this.type = type; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getOpensAt() { return opensAt; }
    public void setOpensAt(LocalTime opensAt) { this.opensAt = opensAt; }

    public LocalTime getClosesAt() { return closesAt; }
    public void setClosesAt(LocalTime closesAt) { this.closesAt = closesAt; }

    public String getHappyHourDetails() { return happyHourDetails; }
    public void setHappyHourDetails(String happyHourDetails) { this.happyHourDetails = happyHourDetails; }
}
