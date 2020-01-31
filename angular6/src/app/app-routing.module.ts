import { NgModule } from '@angular/core';
import { CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';


import { ClusterListComponent } from './feature/cluster/cluster-list/cluster-list.component';
import { AddClusterComponent } from './feature/cluster/add-cluster/add-cluster.component';
import { ManageClusterComponent } from './feature/cluster/manage-cluster/manage-cluster.component';



const routes: Routes = [
	{ path:'clusters', component: ClusterListComponent},
	{ path:'add-cluster/:action', component: AddClusterComponent},
	{ path:'manage-cluster/:id',component:ManageClusterComponent},
	{ path: '', redirectTo: '/clusters', pathMatch: 'full' },
];


@NgModule({
	imports:[
		CommonModule,
		RouterModule.forRoot(routes, { useHash: true })
	],
	exports:[RouterModule],
	providers: [
		
  ]
	,
	declarations:[]
})

export class AppRoutingModule { }