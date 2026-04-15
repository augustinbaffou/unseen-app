package fr.augustinbaffou.unseen.bar.service.osm;

import fr.augustinbaffou.unseen.bar.entity.OpeningHours;
import fr.augustinbaffou.unseen.bar.entity.OpeningHoursType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Parseur best-effort du format OSM opening_hours.
 *
 * Supporte :
 *   - 24/7
 *   - Plages de jours : Mo-Fr 08:00-20:00
 *   - Jours séparés par virgule : Mo,We,Fr 10:00-20:00
 *   - Créneaux multiples dans la journée : Mo-Fr 08:00-12:00, 14:00-22:00
 *   - Règles multiples : Mo-Fr 08:00-20:00; Sa-Su 12:00-22:00
 *   - Fermeture après minuit : closes_at < opens_at (ex: Sa 22:00-02:00)
 *   - Règles "off" : ignorées (pas de ligne créée)
 *
 * Les chaînes non reconnues sont silencieusement ignorées.
 */
public final class OsmOpeningHoursParser {

    private OsmOpeningHoursParser() {}

    private static final List<String> DAY_ORDER = List.of("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su");

    private static final Map<String, DayOfWeek> DAY_MAP = Map.of(
            "Mo", DayOfWeek.MONDAY,
            "Tu", DayOfWeek.TUESDAY,
            "We", DayOfWeek.WEDNESDAY,
            "Th", DayOfWeek.THURSDAY,
            "Fr", DayOfWeek.FRIDAY,
            "Sa", DayOfWeek.SATURDAY,
            "Su", DayOfWeek.SUNDAY
    );

    public static List<OpeningHours> parse(String raw, OpeningHoursType type) {
        if (raw == null || raw.isBlank()) return List.of();

        String trimmed = raw.trim();

        if ("24/7".equals(trimmed)) {
            return Arrays.stream(DayOfWeek.values())
                    .map(day -> buildSlot(type, day, LocalTime.MIDNIGHT, LocalTime.MIDNIGHT))
                    .toList();
        }

        List<OpeningHours> result = new ArrayList<>();
        for (String rule : trimmed.split(";")) {
            result.addAll(parseRule(rule.trim(), type));
        }
        return result;
    }

    private static List<OpeningHours> parseRule(String rule, OpeningHoursType type) {
        if (rule.endsWith("off")) return List.of();

        int spaceIdx = rule.indexOf(' ');
        if (spaceIdx < 0) return List.of();

        String dayPart  = rule.substring(0, spaceIdx).trim();
        String timePart = rule.substring(spaceIdx + 1).trim();

        List<DayOfWeek> days      = expandDays(dayPart);
        List<LocalTime[]> slots   = parseTimeSlots(timePart);

        List<OpeningHours> result = new ArrayList<>();
        for (DayOfWeek day : days) {
            for (LocalTime[] slot : slots) {
                result.add(buildSlot(type, day, slot[0], slot[1]));
            }
        }
        return result;
    }

    private static List<DayOfWeek> expandDays(String dayPart) {
        List<DayOfWeek> result = new ArrayList<>();
        for (String segment : dayPart.split(",")) {
            segment = segment.trim();
            if (segment.contains("-")) {
                String[] range = segment.split("-", 2);
                int start = DAY_ORDER.indexOf(range[0].trim());
                int end   = DAY_ORDER.indexOf(range[1].trim());
                if (start >= 0 && end >= 0 && start <= end) {
                    for (int i = start; i <= end; i++) {
                        result.add(DAY_MAP.get(DAY_ORDER.get(i)));
                    }
                }
            } else {
                DayOfWeek day = DAY_MAP.get(segment);
                if (day != null) result.add(day);
            }
        }
        return result;
    }

    private static List<LocalTime[]> parseTimeSlots(String timePart) {
        List<LocalTime[]> result = new ArrayList<>();
        for (String slot : timePart.split(",")) {
            slot = slot.trim();
            String[] times = slot.split("-", 2);
            if (times.length == 2) {
                try {
                    LocalTime open  = LocalTime.parse(times[0].trim());
                    LocalTime close = LocalTime.parse(times[1].trim());
                    result.add(new LocalTime[]{open, close});
                } catch (Exception ignored) {
                    // créneau non reconnu, on l'ignore silencieusement
                }
            }
        }
        return result;
    }

    private static OpeningHours buildSlot(OpeningHoursType type, DayOfWeek day, LocalTime open, LocalTime close) {
        OpeningHours slot = new OpeningHours();
        slot.setType(type);
        slot.setDayOfWeek(day);
        slot.setOpensAt(open);
        slot.setClosesAt(close);
        return slot;
    }
}
