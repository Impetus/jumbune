import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { FormsModule,ReactiveFormsModule }   from '@angular/forms';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CdkTableModule } from '@angular/cdk/table';
import { SharedModule } from './../shared/shared.module';
import { ToastyModule } from 'ngx-toasty';
import { ClusterListComponent } from '../feature/cluster/cluster-list/cluster-list.component';
import { AddClusterComponent } from '../feature/cluster/add-cluster/add-cluster.component';
import { headerComponent } from './../shared/header.component';
import { ManageClusterComponent } from '../feature/cluster/manage-cluster/manage-cluster.component';

import { 
  MatAutocompleteModule,
  MatButtonModule,
  MatCardModule,
  MatListModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
  MatInputModule,
  MatRadioModule,
  MatCheckboxModule,
  MatDividerModule,
  MatMenuModule,
  MatButtonToggleModule,
  MatProgressBarModule,
  MatSortModule,
  MatTableModule,
  MatTabsModule,
  MatTooltipModule,
  MatSelectModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatChipsModule,
  MatDialogModule,
  MatProgressSpinnerModule
} from '@angular/material';

@NgModule({
exports: [
  MatAutocompleteModule,
  CdkTableModule,
  MatButtonModule,
  MatCardModule,
  MatListModule,
  MatIconModule,
  MatSidenavModule,
  MatToolbarModule,
  MatInputModule,
  MatRadioModule,
  MatCheckboxModule,
  MatDividerModule,
  MatMenuModule,
  MatButtonToggleModule,
  MatProgressBarModule,
  MatSortModule,
  MatTableModule,
  MatTabsModule,
  MatTooltipModule,
  MatSelectModule,
  MatSliderModule,
  MatSlideToggleModule,
  MatChipsModule,
  MatDialogModule,
  MatProgressSpinnerModule
  ],
})
export class MyOwnCustomMaterialModule { }

@NgModule({
  imports: [
    CommonModule,
    MyOwnCustomMaterialModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    NgxChartsModule,
    SharedModule,
    BrowserAnimationsModule,
    ToastyModule.forRoot()
  ],
  exports: [ClusterListComponent
  ],
  declarations: [
    ClusterListComponent,
    AddClusterComponent,
    headerComponent,
    ManageClusterComponent
  ],
  entryComponents:[]
})
export class CoreModule { }
