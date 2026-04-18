import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Bar} from '../commun/bar.model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BarService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/public/bars`;

  getAll(): Observable<Bar[]> {
    return this.http.get<Bar[]>(this.baseUrl);
  }

  getById(id: number): Observable<Bar> {
    return this.http.get<Bar>(`${this.baseUrl}/${id}`);
  }
}
