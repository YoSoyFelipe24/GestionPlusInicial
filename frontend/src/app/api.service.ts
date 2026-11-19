import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Producto { id: number; nombre: string; precio: number; stock: number; }

@Injectable({providedIn: 'root'})
export class ApiService {
  private readonly http = inject(HttpClient);
  productos() { return this.http.get<Producto[]>('/api/productos'); }
  registrarVenta(clienteId: number, productoId: number, cantidad: number) {
    return this.http.post<{ventaId:number,total:number}>('/api/ventas', {
      clienteId, items: [{productoId, cantidad}]
    });
  }
  resumen() { return this.http.get<object>('/api/reportes/resumen'); }
}
