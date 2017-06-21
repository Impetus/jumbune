package org.jumbune.web.beans;

import java.util.Comparator;

import org.apache.hadoop.yarn.api.records.ApplicationReport;

public class AppFinishTimeComparator implements Comparator<ApplicationReport> {
	
private static volatile AppFinishTimeComparator INSTANCE = null;
	
	public static AppFinishTimeComparator getInstance() {
		if (INSTANCE == null) {
			synchronized (AppFinishTimeComparator.class) {
				if (INSTANCE == null) {
					INSTANCE = new AppFinishTimeComparator();
				}
			}
		}
		return INSTANCE;
	}
	
	private AppFinishTimeComparator() {}

	@Override
	public int compare(ApplicationReport app1, ApplicationReport app2) {
		return (app1.getFinishTime() - app2.getFinishTime() ) < 0 ? 1 : -1;
	}

}
