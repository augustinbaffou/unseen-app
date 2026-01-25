import {BarMarker} from '../commun/bar.model';
import {OverpassElement} from './overpass.service';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BarMarkerConverterService {
  private readonly POSSIBLE_RANKS = ['S', 'A', 'B', 'C', 'D'] as const;
  private readonly RANK_PROBABILITY = 0.75;

  convertToBarMarkers(elements: OverpassElement[]): BarMarker[] {
    return elements.map(element => ({
      lat: element.lat,
      lng: element.lon,
      name: element.tags.name || 'Bar sans nom',
      rank: this.generateRandomRank(),
      description: this.buildDescription(element.tags)
    }));
  }

  private generateRandomRank(): string {
    if (Math.random() < this.RANK_PROBABILITY) {
      return 'NA';
    }

    const randomIndex = Math.floor(Math.random() * this.POSSIBLE_RANKS.length);
    return this.POSSIBLE_RANKS[randomIndex];
  }

  private buildDescription(tags: Record<string, any>): string {
    const descriptionParts: string[] = [];

    this.addAddressIfAvailable(tags, descriptionParts);
    this.addOpeningHoursIfAvailable(tags, descriptionParts);
    this.addPhoneIfAvailable(tags, descriptionParts);

    return descriptionParts.length > 0
      ? descriptionParts.join(' • ')
      : 'Un super endroit pour se détendre entre amis.';
  }

  private addAddressIfAvailable(tags: Record<string, any>, parts: string[]): void {
    if (tags['contact:street'] && tags['contact:housenumber']) {
      parts.push(`${tags['contact:housenumber']} ${tags['contact:street']}`);
    }
  }

  private addOpeningHoursIfAvailable(tags: Record<string, any>, parts: string[]): void {
    if (tags['opening_hours']) {
      parts.push(`Horaires: ${tags['opening_hours']}`);
    }
  }

  private addPhoneIfAvailable(tags: Record<string, any>, parts: string[]): void {
    if (tags['contact:phone']) {
      parts.push(`Tél: ${tags['contact:phone']}`);
    }
  }
}
