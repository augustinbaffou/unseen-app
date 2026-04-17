export type BarMarker = {
  name: string;
  lat: number;
  lng: number;
  rank: string;
  description?: string;
};

// ── Backend Bar model ────────────────────────────────────────────────────────

export type BarType =
  | 'COCKTAIL_BAR' | 'BEER_BAR' | 'CRAFT_BEER_BAR' | 'BREWPUB' | 'SPIRITS_BAR' | 'WINE_BAR'
  | 'DANCING_BAR' | 'LIVE_MUSIC_BAR' | 'NIGHTCLUB' | 'SPORTS_BAR' | 'ARCADE_BAR' | 'BOARD_GAME_BAR' | 'ESPORTS_BAR'
  | 'PUB' | 'STUDENT_BAR' | 'LOUNGE_BAR' | 'GUINGUETTE'
  | 'BRASSERIE' | 'TAPAS_BAR' | 'COFFEE_SHOP_BAR' | 'CAFE_TABAC' | 'PMU'
  | 'WATERFRONT_BAR' | 'TERRACE_BAR' | 'ROOFTOP'
  | 'PET_FRIENDLY';

export type BarDataTrust = 'RAW_OSM' | 'COMMUNITY' | 'VERIFIED' | 'CLAIMED';

export interface BarSchedule {
  id: number;
  type: 'BAR' | 'KITCHEN' | 'HAPPY_HOUR';
  dayOfWeek: string;
  opensAt: string;
  closesAt: string;
  happyHourDetails?: string;
}

export interface Bar {
  id: number;
  osmId: string;
  lat: number;
  lng: number;
  name: string;
  altName?: string;
  wasName?: string;
  description?: string;
  priceRange?: number;
  schedules: BarSchedule[];
  games: any[];
  dataTrust: BarDataTrust;
  types: BarType[];
  outdoorSeating?: string;
  indoorSeating?: string;
  instagram?: string;
  facebook?: string;
  website?: string;
  addrHousenumber?: string;
  addrStreet?: string;
  addrCity?: string;
}
