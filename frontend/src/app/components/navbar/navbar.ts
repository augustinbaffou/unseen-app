import {Component, input, output} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class NavbarComponent {
  showMarkers = input(true);
  toggleMarkers = output<void>();

  onToggleMarkers() {
    this.toggleMarkers.emit();
  }
}
