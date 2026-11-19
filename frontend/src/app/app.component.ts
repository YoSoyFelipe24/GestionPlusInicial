import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, Producto } from './api.service';

@Component({
  selector: 'app-root', standalone: true, imports: [CommonModule, FormsModule],
  template: `
    <main>
      <h1>GestiónPlus <small>Prototipo académico</small></h1>
      <p>Aplicación ficticia para identificar deuda técnica y planear una refactorización.</p>
      <button (click)="cargar()">Actualizar catálogo</button>
      <p *ngIf="error" class="error">{{ error }}</p>
      <table>
        <thead><tr><th>Producto</th><th>Precio COP</th><th>Stock</th></tr></thead>
        <tbody><tr *ngFor="let p of productos"><td>{{p.nombre}}</td><td>{{p.precio | number}}</td><td>{{p.stock}}</td></tr></tbody>
      </table>
      <h2>Venta de ejemplo</h2>
      <label>Cliente ID <input type="number" min="1" [(ngModel)]="clienteId"></label>
      <label>Producto ID <input type="number" min="1" [(ngModel)]="productoId"></label>
      <label>Cantidad <input type="number" min="1" [(ngModel)]="cantidad"></label>
      <button (click)="vender()">Registrar venta</button>
      <pre *ngIf="respuesta">{{ respuesta | json }}</pre>
      <p class="nota">Ejemplo docente. No es una aplicación lista para producción.</p>
    </main>`
})
export class AppComponent implements OnInit {
  private readonly api = inject(ApiService);
  productos: Producto[] = [];
  clienteId = 1; productoId = 1; cantidad = 1;
  error = ''; respuesta: object | null = null;
  ngOnInit() { this.cargar(); }
  cargar() {
    this.api.productos().subscribe({next: datos => {this.productos = datos; this.error = '';},
      error: () => this.error = 'No se pudo conectar con el backend.'});
  }
  vender() {
    this.error = '';
    this.api.registrarVenta(this.clienteId, this.productoId, this.cantidad).subscribe({
      next: datos => {this.respuesta = datos; this.cargar();},
      error: e => { this.error = e.error?.detail || e.error?.message || 'Error al registrar venta'; }
    });
  }
}
