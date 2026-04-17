package fr.augustinbaffou.unseen.bar.service.osm;

import fr.augustinbaffou.unseen.bar.entity.BarSchedule;
import fr.augustinbaffou.unseen.bar.entity.BarScheduleType;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OsmBarScheduleParserTest {

    // ── Entrées vides ─────────────────────────────────────────────────────────

    @Test
    void parse_null_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse(null, BarScheduleType.BAR)).isEmpty();
    }

    @Test
    void parse_blank_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse("   ", BarScheduleType.BAR)).isEmpty();
    }

    // ── 24/7 ──────────────────────────────────────────────────────────────────

    @Test
    void parse_24h7_returns7SlotsAllIs24h() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("24/7", BarScheduleType.BAR);

        assertThat(slots).hasSize(7);
        assertThat(slots).allMatch(BarSchedule::is24h);
        assertThat(slots).allMatch(s -> s.getOpensAt() == null);
        assertThat(slots).allMatch(s -> s.getClosesAt() == null);
        assertThat(slots).allMatch(s -> s.getType() == BarScheduleType.BAR);
    }

    @Test
    void parse_24h7_containsAllDaysOfWeek() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("24/7", BarScheduleType.BAR);
        assertThat(slots)
                .extracting(BarSchedule::getDayOfWeek)
                .containsExactlyInAnyOrder(DayOfWeek.values());
    }

    // ── Règle simple ──────────────────────────────────────────────────────────

    @Test
    void parse_singleDay_returnsOneSlot() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo 18:00-23:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(1);
        BarSchedule slot = slots.get(0);
        assertThat(slot.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(slot.getOpensAt()).isEqualTo(LocalTime.of(18, 0));
        assertThat(slot.getClosesAt()).isEqualTo(LocalTime.of(23, 0));
        assertThat(slot.is24h()).isFalse();
        assertThat(slot.getType()).isEqualTo(BarScheduleType.BAR);
    }

    // ── Plage de jours ────────────────────────────────────────────────────────

    @Test
    void parse_dayRange_returnsOneSlotPerDay() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo-Fr 08:00-20:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(5);
        assertThat(slots).extracting(BarSchedule::getDayOfWeek)
                .containsExactly(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
    }

    // ── Plage avec wraparound ─────────────────────────────────────────────────

    @Test
    void parse_wraparoundRange_SaToMo_returns3Slots() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Sa-Mo 10:00-02:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(3);
        assertThat(slots).extracting(BarSchedule::getDayOfWeek)
                .containsExactly(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY);
    }

    // ── Jours séparés par virgule ─────────────────────────────────────────────

    @Test
    void parse_commaSeparatedDays_returnsOneSlotPerDay() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo,We,Fr 10:00-20:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(3);
        assertThat(slots).extracting(BarSchedule::getDayOfWeek)
                .containsExactly(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
    }

    // ── Créneaux multiples dans la journée ────────────────────────────────────

    @Test
    void parse_multipleTimeSlotsPerDay_returnsAllSlots() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo-Fr 08:00-12:00,14:00-22:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(10); // 5 jours × 2 créneaux
        assertThat(slots).filteredOn(s -> s.getDayOfWeek() == DayOfWeek.MONDAY)
                .extracting(BarSchedule::getOpensAt)
                .containsExactly(LocalTime.of(8, 0), LocalTime.of(14, 0));
    }

    // ── Règles multiples séparées par ";" ─────────────────────────────────────

    @Test
    void parse_multipleRules_returnsAllSlots() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo-Fr 08:00-20:00; Sa-Su 12:00-22:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(7);
    }

    // ── Type kitchen transmis ─────────────────────────────────────────────────

    @Test
    void parse_kitchenType_slotsHaveKitchenType() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo 12:00-14:00", BarScheduleType.KITCHEN);

        assertThat(slots).hasSize(1);
        assertThat(slots.get(0).getType()).isEqualTo(BarScheduleType.KITCHEN);
    }

    // ── Règle "off" ignorée ───────────────────────────────────────────────────

    @Test
    void parse_offRule_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse("Mo off", BarScheduleType.BAR)).isEmpty();
    }

    @Test
    void parse_mixedOffAndNormal_onlyNormalReturned() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Mo-Fr 08:00-20:00; Sa off", BarScheduleType.BAR);

        assertThat(slots).hasSize(5);
        assertThat(slots).noneMatch(s -> s.getDayOfWeek() == DayOfWeek.SATURDAY);
    }

    // ── Fermeture après minuit ────────────────────────────────────────────────

    @Test
    void parse_closesAfterMidnight_opensAtBeforeClosesAt() {
        List<BarSchedule> slots = OsmBarScheduleParser.parse("Sa 22:00-02:00", BarScheduleType.BAR);

        assertThat(slots).hasSize(1);
        BarSchedule slot = slots.get(0);
        assertThat(slot.getOpensAt()).isEqualTo(LocalTime.of(22, 0));
        assertThat(slot.getClosesAt()).isEqualTo(LocalTime.of(2, 0));
    }

    // ── Format invalide silencieusement ignoré ────────────────────────────────

    @Test
    void parse_invalidTimeFormat_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse("Mo 8h-22h", BarScheduleType.BAR)).isEmpty();
    }

    @Test
    void parse_unknownDayCode_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse("XX 08:00-20:00", BarScheduleType.BAR)).isEmpty();
    }

    @Test
    void parse_noSpaceBetweenDayAndTime_returnsEmpty() {
        assertThat(OsmBarScheduleParser.parse("Mo08:00-20:00", BarScheduleType.BAR)).isEmpty();
    }
}
