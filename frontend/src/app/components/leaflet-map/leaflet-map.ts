
import {AfterViewInit, Component, effect, input, OnDestroy} from '@angular/core';
import * as Leaflet from 'leaflet';
import {BarMarker} from '../../commun/bar.model';
import {NANTES_SEARCH_RADIUS, NANTES_CENTER_COORDS} from '../../commun/config';

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
  private markersLayer = new Leaflet.LayerGroup();
  private iconCache = new Map<string, Leaflet.Icon>();

  private readonly NANTES_CENTER: [number, number] = [47.218371, -1.553621];
  private readonly DEFAULT_ZOOM = 14;
  private readonly POPUP_CONFIG = {
    maxWidth: 250,
    className: 'custom-leaflet-popup'
  };

  constructor() {
    effect(() => this.handleMarkersChange());
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
    this.map = Leaflet.map('map').setView(this.NANTES_CENTER, this.DEFAULT_ZOOM);
    this.addTileLayer();
    this.addSearchAreaCircle();
    this.markersLayer.addTo(this.map);
  }

  private updateMarkersOnLayer(markers: BarMarker[]): void {
    this.markersLayer.clearLayers();
    markers.forEach(marker => this.addBarMarker(marker));
  }

  private addTileLayer(): void {
    Leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);
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
    const popupContent = this.createPopupContent(marker);

    Leaflet.marker([marker.lat, marker.lng])
      .addTo(this.markersLayer)
      .setIcon(this.getIconForRank(marker.rank))
      .bindPopup(popupContent, this.POPUP_CONFIG);
  }

  private createPopupContent(marker: BarMarker): string {
    return `
      <div class="p-1">
        <h3 class="text-lg font-bold text-gray-900 mb-1">${marker.name}</h3>
        <p class="text-sm text-gray-600 mb-3">${marker.description || 'Un super endroit pour se détendre entre amis.'}</p>
        <button class="w-full bg-indigo-600 text-white text-xs font-semibold py-2 px-4 rounded-lg hover:bg-indigo-700 transition-colors">
          Voir les détails
        </button>
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
      iconUrl: `markers/custom/${rank}.png`,
      iconSize: [64, 64],
      iconAnchor: [32, 64]
    });
  }
}
