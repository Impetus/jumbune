package org.jumbune.web.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean for Creating json for client side (UI)
 */
public class QueueResponse {
	private String queueName;
	/**
	 * Contains 24 hour data
	 */
	private List<Value> queueData = new ArrayList<Value>(24);

	public QueueResponse(String queueName) {
		this.queueName = queueName;
	}

	public void addValue(String percent, String hour, String queueName) {
		queueData.add(new Value(percent, hour, queueName));
	}

	class Value {
	    private String queueName;
		private String percent;
		private String hour;

		public Value(String percent, String hour, String queueName) {
			this.percent = percent;
			this.hour = hour;
			this.queueName = queueName;
		}

		public String getPercent() {
			return percent;
		}

		public void setPercent(String percent) {
			this.percent = percent;
		}

		public String getHour() {
			return hour;
		}

		public void setHour(String hour) {
			this.hour = hour;
		}

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public List<Value> getQueueData() {
		return queueData;
	}

	public void setQueueData(List<Value> queueData) {
		this.queueData = queueData;
	}

}
