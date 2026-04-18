import {Component, inject, signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {NavbarComponent} from '../../components/navbar/navbar';
import {BarService} from '../../services/bar.service';
import {Bar, BarDataTrust, BarGame, BarGameType, BarSchedule, BarType} from '../../commun/bar.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {catchError, of, switchMap, tap} from 'rxjs';
import {CommonModule} from '@angular/common';
import {BAR_TYPE_LABELS} from '../bars/bars';

const DATA_TRUST_CONFIG: Record<BarDataTrust, { label: string; color: string; bgColor: string; borderColor: string }> = {
  RAW_OSM:   { label: 'Données publiques',        color: '#8C8C8C', bgColor: '#8C8C8C20', borderColor: '#8C8C8C50' },
  COMMUNITY: { label: 'Validé par la communauté', color: '#6A7D5A', bgColor: '#6A7D5A20', borderColor: '#6A7D5A50' },
  VERIFIED:  { label: 'Vérifié par Unseen',       color: '#5775e2', bgColor: '#5775e220', borderColor: '#5775e250' },
  CLAIMED:   { label: 'Certifié par le gérant',   color: '#d48a73', bgColor: '#d48a7320', borderColor: '#d48a7350' },
};

export const GAME_TYPE_LABELS: Record<BarGameType, string> = {
  BABYFOOT:      'Baby-foot',
  DARTS_PLASTIC: 'Fléchettes plastique',
  DARTS_STEEL:   'Fléchettes acier',
  BILLIARDS:     'Billard',
  PETANQUE:      'Pétanque',
  MOLKKY:        'Mölkky',
  PALET:         'Palet',
  ARCADE:        'Arcade',
  BOARD_GAMES:   'Jeux de société',
  PING_PONG:     'Ping-pong',
  BEER_PONG:     'Beer-pong',
  FLIPPER:       'Flipper',
};

const DAY_LABELS: Record<string, string> = {
  MONDAY: 'Lundi', TUESDAY: 'Mardi', WEDNESDAY: 'Mercredi',
  THURSDAY: 'Jeudi', FRIDAY: 'Vendredi', SATURDAY: 'Samedi', SUNDAY: 'Dimanche'
};

const DAYS_ORDER = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

@Component({
  selector: 'app-bar-detail',
  templateUrl: './bar-detail.html',
  styleUrls: ['./bar-detail.scss'],
  standalone: true,
  imports: [NavbarComponent, CommonModule, RouterLink]
})
export class BarDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly barService = inject(BarService);

  readonly loading = signal(true);
  readonly error = signal(false);
  readonly notFound = signal(false);

  readonly bar = toSignal(
    this.route.paramMap.pipe(
      switchMap(params => {
        const id = Number(params.get('id'));
        this.loading.set(true);
        this.error.set(false);
        this.notFound.set(false);
        return this.barService.getById(id).pipe(
          tap(() => this.loading.set(false)),
          catchError(err => {
            this.loading.set(false);
            if (err.status === 404) this.notFound.set(true);
            else this.error.set(true);
            return of(null);
          })
        );
      })
    ),
    {initialValue: null as Bar | null}
  );

  typeLabel(type: BarType): string { return BAR_TYPE_LABELS[type] ?? type; }
  gameLabel(game: BarGame): string { return GAME_TYPE_LABELS[game.gameType] ?? game.gameType; }
  dayLabel(day: string): string { return DAY_LABELS[day] ?? day; }
  priceLabel(n?: number): string { return n ? '€'.repeat(n) : ''; }

  trustColor(dt: BarDataTrust): string { return DATA_TRUST_CONFIG[dt]?.color ?? '#8C8C8C'; }
  trustBgColor(dt: BarDataTrust): string { return DATA_TRUST_CONFIG[dt]?.bgColor ?? '#8C8C8C20'; }
  trustBorderColor(dt: BarDataTrust): string { return DATA_TRUST_CONFIG[dt]?.borderColor ?? '#8C8C8C50'; }
  trustLabel(dt: BarDataTrust): string { return DATA_TRUST_CONFIG[dt]?.label ?? dt; }

  addressFull(bar: Bar): string {
    return [bar.addrHousenumber, bar.addrStreet, bar.addrCity].filter(Boolean).join(', ');
  }

  barSchedules(bar: Bar): BarSchedule[] {
    return bar.schedules
      .filter(s => s.type === 'BAR')
      .sort((a, b) => DAYS_ORDER.indexOf(a.dayOfWeek) - DAYS_ORDER.indexOf(b.dayOfWeek));
  }

  happyHourSchedules(bar: Bar): BarSchedule[] {
    return bar.schedules
      .filter(s => s.type === 'HAPPY_HOUR')
      .sort((a, b) => DAYS_ORDER.indexOf(a.dayOfWeek) - DAYS_ORDER.indexOf(b.dayOfWeek));
  }

  formatHours(s: BarSchedule): string {
    if (s.is24h) return '24h/24';
    const fmt = (t?: string) => t?.slice(0, 5) ?? '?';
    return `${fmt(s.opensAt)} – ${fmt(s.closesAt)}`;
  }

  allDaySchedules(bar: Bar): { dayKey: string; label: string; schedule: BarSchedule | null; happyHour: BarSchedule | null }[] {
    return DAYS_ORDER.map(dayKey => ({
      dayKey,
      label: DAY_LABELS[dayKey],
      schedule: bar.schedules.find(s => s.type === 'BAR' && s.dayOfWeek === dayKey) ?? null,
      happyHour: bar.schedules.find(s => s.type === 'HAPPY_HOUR' && s.dayOfWeek === dayKey) ?? null,
    }));
  }

  happyHourSummary(bar: Bar): string {
    const hhs = this.happyHourSchedules(bar);
    if (hhs.length === 0) return '';
    const days = hhs.map(s => DAY_LABELS[s.dayOfWeek]).join(', ');
    const times = this.formatHours(hhs[0]);
    const details = hhs[0].happyHourDetails;
    return `${days}, ${times}${details ? ' · ' + details : ''}`;
  }

  readonly todayKey = DAYS_ORDER[new Date().getDay() === 0 ? 6 : new Date().getDay() - 1];

  private timeToMin(t: string): number {
    const [h, m] = t.split(':').map(Number);
    return h * 60 + m;
  }

  private nextOpeningDetail(bar: Bar, fromDayIdx: number): string {
    for (let i = 1; i <= 7; i++) {
      const key = DAYS_ORDER[(fromDayIdx + i) % 7];
      const sched = bar.schedules.find(s => s.type === 'BAR' && s.dayOfWeek === key);
      if (sched?.opensAt) {
        const dayLabel = i === 1 ? 'demain' : DAY_LABELS[key].slice(0, 3).toLowerCase() + '.';
        return `ouvre ${dayLabel} à ${sched.opensAt.slice(0, 5)}`;
      }
    }
    return 'horaires non renseignés';
  }

  barStatus(bar: Bar): { type: string; label: string } {
    const now = new Date();
    const nowMin = now.getHours() * 60 + now.getMinutes();
    const dayIdx = now.getDay() === 0 ? 6 : now.getDay() - 1;
    const todayKey = DAYS_ORDER[dayIdx];
    const yesterdayKey = DAYS_ORDER[(dayIdx + 6) % 7];
    const fmt = (t: string) => t.slice(0, 5);

    const todaySched = bar.schedules.find(s => s.type === 'BAR' && s.dayOfWeek === todayKey) ?? null;
    const yesterdaySched = bar.schedules.find(s => s.type === 'BAR' && s.dayOfWeek === yesterdayKey) ?? null;

    // Fenêtre nocturne de la veille (ex. ouvert 17h→02h, on est à 01h20)
    if (yesterdaySched?.opensAt && yesterdaySched?.closesAt && !yesterdaySched.is24h) {
      const oMin = this.timeToMin(yesterdaySched.opensAt);
      const cMin = this.timeToMin(yesterdaySched.closesAt);
      if (cMin < oMin && nowMin < cMin) {
        const left = cMin - nowMin;
        if (left <= 30) return { type: 'CLOSES_SOON', label: `Ferme dans ${left} min` };
        return { type: 'OPEN', label: `Ouvert en ce moment · ferme à ${fmt(yesterdaySched.closesAt)}` };
      }
    }

    if (!todaySched) return { type: 'CLOSED', label: `Fermé · ${this.nextOpeningDetail(bar, dayIdx)}` };
    if (todaySched.is24h) return { type: 'OPEN', label: 'Ouvert 24h/24' };
    if (!todaySched.opensAt || !todaySched.closesAt) return { type: 'CLOSED', label: `Fermé · ${this.nextOpeningDetail(bar, dayIdx)}` };

    const openMin = this.timeToMin(todaySched.opensAt);
    const closeMin = this.timeToMin(todaySched.closesAt);

    if (closeMin > openMin) {
      // Horaires normaux (ex. 09h→22h)
      if (nowMin >= openMin && nowMin < closeMin) {
        const left = closeMin - nowMin;
        if (left <= 30) return { type: 'CLOSES_SOON', label: `Ferme dans ${left} min` };
        return { type: 'OPEN', label: `Ouvert en ce moment · ferme à ${fmt(todaySched.closesAt)}` };
      }
      if (nowMin < openMin && openMin - nowMin <= 60)
        return { type: 'OPENS_SOON', label: `Fermé · ouvre à ${fmt(todaySched.opensAt)}` };
    } else {
      // Horaires nocturnes (ex. 17h→02h) — côté soirée
      if (nowMin >= openMin) {
        const left = 24 * 60 - nowMin + closeMin;
        if (left <= 30) return { type: 'CLOSES_SOON', label: `Ferme dans ${left} min` };
        return { type: 'OPEN', label: `Ouvert en ce moment · ferme à ${fmt(todaySched.closesAt)}` };
      }
      if (openMin - nowMin <= 60)
        return { type: 'OPENS_SOON', label: `Fermé · ouvre à ${fmt(todaySched.opensAt)}` };
    }

    return { type: 'CLOSED', label: `Fermé · ${this.nextOpeningDetail(bar, dayIdx)}` };
  }

  hhStatus(bar: Bar): { label: string } | null {
    const now = new Date();
    const nowMin = now.getHours() * 60 + now.getMinutes();
    const dayIdx = now.getDay() === 0 ? 6 : now.getDay() - 1;
    const hh = bar.schedules.find(s => s.type === 'HAPPY_HOUR' && s.dayOfWeek === DAYS_ORDER[dayIdx]);
    if (!hh?.opensAt || !hh?.closesAt) return null;
    const openMin = this.timeToMin(hh.opensAt);
    const closeMin = this.timeToMin(hh.closesAt);
    if (nowMin >= openMin && nowMin < closeMin)
      return { label: `Happy hour · jusqu'à ${hh.closesAt.slice(0, 5)}` };
    if (nowMin < openMin && openMin - nowMin <= 30)
      return { label: `Happy hour dans ${openMin - nowMin} min` };
    return null;
  }

  statusColor(type: string): string {
    return ({ OPEN: '#3a7c52', CLOSES_SOON: '#b87333', OPENS_SOON: '#b87333', CLOSED: '#9E9E9E' } as Record<string, string>)[type] ?? '#9E9E9E';
  }

  statusBg(type: string): string {
    return ({ OPEN: '#3a7c5210', CLOSES_SOON: '#b8733310', OPENS_SOON: '#b8733310', CLOSED: '#9E9E9E0D' } as Record<string, string>)[type] ?? '#9E9E9E0D';
  }

  statusBorderColor(type: string): string {
    return ({ OPEN: '#3a7c5225', CLOSES_SOON: '#b8733325', OPENS_SOON: '#b8733325', CLOSED: '#9E9E9E20' } as Record<string, string>)[type] ?? '#9E9E9E20';
  }

  mapsUrl(bar: Bar): string {
    return `https://www.google.com/maps/search/?api=1&query=${bar.lat},${bar.lng}`;
  }

  instagramUrl(handle: string): string {
    return handle.startsWith('http') ? handle : `https://instagram.com/${handle}`;
  }
}
