import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {NANTES_CENTER_COORDS, NANTES_SEARCH_RADIUS} from '../commun/config';

export interface OverpassElement {
  type: string;
  id: number;
  lat: number;
  lon: number;
  tags: {
    amenity: string;
    name?: string;
    [key: string]: any;
  };
}

export interface OverpassResponse {
  version: number;
  generator: string;
  osm3s: {
    timestamp_osm_base: string;
    timestamp_areas_base: string;
    copyright: string;
  };
  elements: OverpassElement[];
}

@Injectable({
  providedIn: 'root'
})
export class OverpassService {
  private readonly API_URL = 'https://overpass-api.de/api/interpreter';

  private readonly AMENITY_BAR_KEY = 'bar';
  private readonly AMENITY_CAFE_KEY = 'cafe';
  private readonly AMENITY_PUB_KEY = 'pub';

  constructor(private http: HttpClient) {}

  getBarsInNantes(): Observable<OverpassElement[]> {
    const area = `(around:${NANTES_SEARCH_RADIUS},${NANTES_CENTER_COORDS.lat},${NANTES_CENTER_COORDS.lng})`;

    const query = `
      [out:json][timeout:25];
      (
        node["amenity"=${this.AMENITY_BAR_KEY}]${area};
        node["amenity"=${this.AMENITY_CAFE_KEY}]${area};
        node["amenity"=${this.AMENITY_PUB_KEY}]${area};

        way["amenity"=${this.AMENITY_BAR_KEY}]${area};
        way["amenity"=${this.AMENITY_CAFE_KEY}]${area};
        way["amenity"=${this.AMENITY_PUB_KEY}]${area};
      );
      out center;
    `;

    return this.http.post<OverpassResponse>(this.API_URL, query, {
      headers: {
        'Content-Type': 'text/plain'
      }
    }).pipe(
      map(response => response.elements)
    );
  }
}
