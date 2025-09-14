import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartsModule as Ng2ChartsModule } from 'ng2-charts'; // Importar o ChartsModule do ng2-charts

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    Ng2ChartsModule // Importar o NgChartsModule do ng2-charts aqui
  ],
  exports: [
    Ng2ChartsModule // Exportar o ChartsModule do ng2-charts
  ]
})
export class ChartsModule { }