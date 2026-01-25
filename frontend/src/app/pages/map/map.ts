import {Component, inject, signal} from '@angular/core';
import {LeafletMapComponent} from '../../components/leaflet-map/leaflet-map';
import {NavbarComponent} from '../../components/navbar/navbar';
import {OverpassService} from '../../services/overpass.service';
import {catchError, Observable, of, shareReplay, tap} from 'rxjs';
import {map} from 'rxjs/operators';
import {BarMarker} from '../../commun/bar.model';
import {toSignal} from '@angular/core/rxjs-interop';
import {BarMarkerConverterService} from '../../services/bar-converter.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  standalone: true,
  imports: [
    LeafletMapComponent,
    NavbarComponent
  ]
})

export class MapComponent {
  showMarkers = signal(true);

  private readonly overpassService = inject(OverpassService);
  private readonly barConverterService = inject(BarMarkerConverterService);

  private handleError = (error: any): Observable<BarMarker[]> => {
    console.error('Erreur lors du chargement des bars:', error);
    return of([]);
  };

  bars = toSignal(
    this.overpassService.getBarsInNantes().pipe(
      map(elements => this.barConverterService.convertToBarMarkers(elements)),
      catchError(this.handleError)
    ),
    { initialValue: [] as BarMarker[] }
  );

  toggleMarkers(): void {
    this.showMarkers.update(value => !value);
  }
}
