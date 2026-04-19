import {Component, computed, inject, signal} from '@angular/core';
import {NavbarComponent} from '../../components/navbar/navbar';
import {BarService} from '../../services/bar.service';
import {Bar, BarDataTrust, BarType} from '../../commun/bar.model';
import {BAR_TYPE_LABELS} from '../../commun/bar-labels';
import {toSignal} from '@angular/core/rxjs-interop';
import {catchError, of, tap} from 'rxjs';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';

const DATA_TRUST_CONFIG: Record<BarDataTrust, { label: string; color: string; bgColor: string; borderColor: string }> = {
  RAW_OSM:   { label: 'Données publiques',        color: '#8C8C8C', bgColor: '#8C8C8C20', borderColor: '#8C8C8C50' },
  COMMUNITY: { label: 'Validé par la communauté', color: '#6A7D5A', bgColor: '#6A7D5A20', borderColor: '#6A7D5A50' },
  VERIFIED:  { label: 'Vérifié par Unseen',       color: '#5775e2', bgColor: '#5775e220', borderColor: '#5775e250' },
  CLAIMED:   { label: 'Certifié par le gérant',   color: '#d48a73', bgColor: '#d48a7320', borderColor: '#d48a7350' },
};

const ACTIVE_FILTER_STYLE = { color: '#5775e2', bgColor: '#5775e220', borderColor: '#5775e250' };

@Component({
  selector: 'app-bars',
  templateUrl: './bars.html',
  styleUrls: ['./bars.scss'],
  standalone: true,
  imports: [NavbarComponent, CommonModule, FormsModule, RouterLink]
})
export class BarsComponent {
  private readonly barService = inject(BarService);

  readonly searchQuery = signal('');
  readonly selectedType = signal<BarType | null>(null);
  readonly loaded = signal(false);
  readonly error = signal(false);

  readonly skeletons = Array.from({length: 9});

  readonly allBars = toSignal(
    this.barService.getAll().pipe(
      tap(() => this.loaded.set(true)),
      catchError(() => {
        this.loaded.set(true);
        this.error.set(true);
        return of([] as Bar[]);
      })
    ),
    {initialValue: [] as Bar[]}
  );

  readonly availableTypes = computed<BarType[]>(() => {
    const types = new Set<BarType>();
    this.allBars().forEach(bar => bar.types.forEach(t => types.add(t)));
    return Array.from(types).sort((a, b) =>
      BAR_TYPE_LABELS[a].localeCompare(BAR_TYPE_LABELS[b])
    );
  });

  readonly filteredBars = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const type = this.selectedType();
    return this.allBars().filter(bar => {
      const matchesQuery = !query || bar.name.toLowerCase().includes(query);
      const matchesType = !type || bar.types.includes(type);
      return matchesQuery && matchesType;
    });
  });

  typeLabel(type: BarType): string {
    return BAR_TYPE_LABELS[type] ?? type;
  }

  priceLabel(priceRange: number | undefined): string {
    if (!priceRange) return '';
    return '€'.repeat(priceRange);
  }

  addressLabel(bar: Bar): string {
    const parts = [bar.addrHousenumber, bar.addrStreet].filter(Boolean);
    return parts.length ? parts.join(' ') : (bar.addrCity ?? '');
  }

  readonly activeFilterStyle = ACTIVE_FILTER_STYLE;

  trustColor(dataTrust: BarDataTrust): string {
    return DATA_TRUST_CONFIG[dataTrust]?.color ?? '#8C8C8C';
  }

  trustBgColor(dataTrust: BarDataTrust): string {
    return DATA_TRUST_CONFIG[dataTrust]?.bgColor ?? '#8C8C8C20';
  }

  trustBorderColor(dataTrust: BarDataTrust): string {
    return DATA_TRUST_CONFIG[dataTrust]?.borderColor ?? '#8C8C8C50';
  }

  trustLabel(dataTrust: BarDataTrust): string {
    return DATA_TRUST_CONFIG[dataTrust]?.label ?? dataTrust;
  }

  selectType(type: BarType): void {
    this.selectedType.update(current => current === type ? null : type);
  }

  onSearchChange(value: string): void {
    this.searchQuery.set(value);
  }

  resetFilters(): void {
    this.searchQuery.set('');
    this.selectedType.set(null);
  }
}
