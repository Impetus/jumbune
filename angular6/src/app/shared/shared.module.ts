import { JumbuneFilterPipe } from './filter.pipe';
import { KeysPipe } from './keyvalue.pipe';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImpureKeysPipe } from './impurekeyvalue.pipe';
import {RouterModule} from '@angular/router';

import {
  MatButtonModule,
  MatCardModule,
  MatListModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
} from '@angular/material';



@NgModule({
imports: [MatButtonModule,
  MatCardModule,
  MatListModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule],
  
exports: [
  MatButtonModule,
  MatCardModule,
  MatListModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule
  ],
})
export class MyOwnCustomMaterialModule { }

@NgModule({
  imports: [
    CommonModule,
    MyOwnCustomMaterialModule,
    RouterModule    
  ],
  exports: [
    CommonModule,
    KeysPipe,
    ImpureKeysPipe,
    JumbuneFilterPipe    
  ],
  declarations: [KeysPipe,ImpureKeysPipe,JumbuneFilterPipe]
})
export class SharedModule { }
