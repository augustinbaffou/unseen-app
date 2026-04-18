
import {AfterViewInit, Component, effect, input, OnDestroy} from '@angular/core';
import * as Leaflet from 'leaflet';
import {Router} from '@angular/router';
import {BarMarker} from '../../commun/bar.model';
import {NANTES_SEARCH_RADIUS, NANTES_CENTER_COORDS} from '../../commun/config';
import {ThemeService} from '../../services/theme.service';

const TILE_URLS = {
  light: 'https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png',
  dark:  'https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png'
} as const;

const TILE_OPTIONS: Leaflet.TileLayerOptions = {
  attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> © <a href="https://carto.com/">CARTO</a>',
  subdomains: 'abcd'
};

@Component({
  selector: 'app-leaflet-map',
  templateUrl: './leaflet-map.html',
  styleUrls: ['./leaflet-map.scss'],
  standalone: true
})
export class LeafletMapComponent implements AfterViewInit, OnDestroy {
  markers = input<BarMarker[]>([]);
  showMarkers = input(true);

  private map!: Leaflet.Map;
  private tileLayer!: Leaflet.TileLayer;
  private markersLayer = new Leaflet.LayerGroup();
  private iconCache = new Map<string, Leaflet.Icon>();

  private readonly DEFAULT_ZOOM = 14;
  private readonly POPUP_CONFIG = {
    maxWidth: 250,
    className: 'custom-leaflet-popup'
  };

  constructor(private themeService: ThemeService, private router: Router) {
    effect(() => this.handleMarkersChange());
    effect(() => this.handleThemeChange());
  }

  ngAfterViewInit(): void {
    this.initializeMap();
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private handleMarkersChange(): void {
    const currentMarkers = this.markers();
    const isVisible = this.showMarkers();

    if (!this.map) return;

    if (isVisible) {
      this.markersLayer.addTo(this.map);
      this.updateMarkersOnLayer(currentMarkers);
    } else {
      this.markersLayer.remove();
    }
  }

  private initializeMap(): void {
    this.map = Leaflet.map('map').setView([NANTES_CENTER_COORDS.lat, NANTES_CENTER_COORDS.lng], this.DEFAULT_ZOOM);
    this.addTileLayer();
    this.addSearchAreaCircle();
    this.markersLayer.addTo(this.map);
  }

  private updateMarkersOnLayer(markers: BarMarker[]): void {
    this.markersLayer.clearLayers();
    markers.forEach(marker => this.addBarMarker(marker));
  }

  private handleThemeChange(): void {
    const dark = this.themeService.isDark();
    if (!this.map) return;
    this.tileLayer?.remove();
    this.tileLayer = Leaflet.tileLayer(dark ? TILE_URLS.dark : TILE_URLS.light, TILE_OPTIONS).addTo(this.map);
  }

  private addTileLayer(): void {
    const dark = this.themeService.isDark();
    this.tileLayer = Leaflet.tileLayer(dark ? TILE_URLS.dark : TILE_URLS.light, TILE_OPTIONS).addTo(this.map);
  }

  private addSearchAreaCircle(): void {
    const center: [number, number] = [NANTES_CENTER_COORDS.lat, NANTES_CENTER_COORDS.lng];

    Leaflet.circle(center, {
      radius: NANTES_SEARCH_RADIUS,
      color: 'oklch(27.4% 0.072 132.109)',
      weight: 1,
      fillOpacity: 0
    }).addTo(this.map);
  }

  private addBarMarker(marker: BarMarker): void {
    const leafletMarker = Leaflet.marker([marker.lat, marker.lng])
      .addTo(this.markersLayer)
      .setIcon(this.getIconForRank(marker.rank))
      .bindPopup(this.createPopupContent(marker), this.POPUP_CONFIG);

    if (marker.id) {
      leafletMarker.on('popupopen', () => {
        document.getElementById(`bar-popup-btn-${marker.id}`)
          ?.addEventListener('click', () => this.router.navigate(['/bars', marker.id]));
      });
    }
  }

  private createPopupContent(marker: BarMarker): string {
    const btn = marker.id
      ? `<button id="bar-popup-btn-${marker.id}" class="w-full bg-terracotta text-white text-xs font-semibold py-2 px-4 rounded-lg hover:opacity-90 transition-opacity mt-3">Voir les détails</button>`
      : '';
    return `
      <div class="p-1">
        <h3 class="text-base font-bold text-gray-900 mb-1">${marker.name}</h3>
        ${marker.description ? `<p class="text-xs text-gray-500">${marker.description}</p>` : ''}
        ${btn}
      </div>
    `;
  }

  private getIconForRank(rank: string): Leaflet.Icon {
    if (!this.iconCache.has(rank)) {
      this.iconCache.set(rank, this.createIcon(rank));
    }
    return this.iconCache.get(rank)!;
  }

  private createIcon(rank: string): Leaflet.Icon {
    return Leaflet.icon({
      iconUrl: `markers/light-svg/marker-pin-${rank}.svg`,
      iconSize: [40, 56],
      iconAnchor: [0, 56]
    });
  }
}
