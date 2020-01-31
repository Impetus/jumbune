import { Observable } from 'rxjs/Observable';
import { CommonHttpClientService } from './../../shared/common-http-client.service';
import { Injectable } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import { throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';
@Injectable({
	providedIn: 'root'
})

export class ClusterService {

	constructor(private commonhttp: CommonHttpClientService) { }


	getClusterList(): Observable<any> {
		const options: HttpHeaders = new HttpHeaders({
			"Content-Type": "application/x-www-form-urlencoded"
		});
		return this.commonhttp.get<any>('/apis/clusteranalysis/clusters-list', {
			headers: options
		}).pipe(
			retry(1),
			catchError(this.handleError)
		);
	}

	getClusterConfiguration(clusterName): Observable<any> {
		return this.commonhttp.get<any>('/apis/adminconfig/clusterconfiguration/' + clusterName);
	}

	getRUMQueuesList(clusterName): Observable<any> {
		return this.commonhttp.get<any>('/apis/adminconfig/cluster-leaf-queues-list/' + clusterName);
	}

	saveConfigurationData(adminConfiguration) {
		return this.commonhttp.save('/apis/adminconfig/saveclusterconfigurations', adminConfiguration);
	}

	getEditClusterDetails(clusterName) {
		return this.commonhttp.get<any>('/apis/cluster/' + clusterName);
	}

	getIsMaprDistribution() {
		return this.commonhttp.get<any>('/apis/home/is-mapr');
	}

	addClusterConfiguration(object): Observable<any> {
		return this.commonhttp.save<any>('/apis/cluster', object);
	}

	updateClusterConfiguration(clusterName, object): Observable<any> {
		return this.commonhttp.update<any>('/apis/cluster/' + clusterName, object);
	}

	deleteCluster(clusterName) {
		return this.commonhttp.delete<any>('/apis/cluster/' + clusterName);
	}

	handleError(error: Response) {
		let errorMessage = '';
		if (error instanceof ErrorEvent) {
			// client-side error
			errorMessage = `Error: ${error}`;
		} else {
			// server-side error
			errorMessage = `Error Code: ${error.status}\nMessage: ${error}`;
		}

		return throwError(errorMessage);

	}





















}
