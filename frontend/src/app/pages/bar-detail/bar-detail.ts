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
    return `${s.opensAt ?? '?'} – ${s.closesAt ?? '?'}`;
  }

  allDaySchedules(bar: Bar): { dayKey: string; label: string; schedule: BarSchedule | null }[] {
    return DAYS_ORDER.map(dayKey => ({
      dayKey,
      label: DAY_LABELS[dayKey],
      schedule: bar.schedules.find(s => s.type === 'BAR' && s.dayOfWeek === dayKey) ?? null,
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

  mapsUrl(bar: Bar): string {
    return `https://www.google.com/maps/search/?api=1&query=${bar.lat},${bar.lng}`;
  }

  instagramUrl(handle: string): string {
    return handle.startsWith('http') ? handle : `https://instagram.com/${handle}`;
  }
}
