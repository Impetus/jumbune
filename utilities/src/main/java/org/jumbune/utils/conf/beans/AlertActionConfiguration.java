package org.jumbune.utils.conf.beans;

import java.util.List;

public class AlertActionConfiguration {

    private List<AlertAction> alertActions;

    private List<ExclusionCondition> exclusionConditions;

    private List<String> inefficiencyEmailTo;
    
    private Double recurringInterval;

    public List<AlertAction> getAlertActions() {
        return alertActions;
    }

    public void setAlertActions(List<AlertAction> alertActions) {
        this.alertActions = alertActions;
    }


    public List<ExclusionCondition> getExclusionConditions() {
        return exclusionConditions;
    }

    public void setExclusionConditions(List<ExclusionCondition> exclusionConditions) {
        this.exclusionConditions = exclusionConditions;
    }

    public List<String> getInefficiencyEmailTo() {
        return inefficiencyEmailTo;
    }

    public void setInefficiencyEmailTo(List<String> inefficiencyEmailTo) {
        this.inefficiencyEmailTo = inefficiencyEmailTo;
    }

    public Double getRecurringInterval() {
        return recurringInterval;
    }

    public void setRecurringInterval(Double recurringInterval) {
        this.recurringInterval = recurringInterval;
    }

    @Override
    public String toString() {
        return "AlertActionConfiguration [alertActions=" + alertActions + ", exclusionConditions=" + exclusionConditions
                + ", inefficiencyEmailTo=" + inefficiencyEmailTo + ", recurringInterval=" + recurringInterval + "]";
    }

    
    
}
