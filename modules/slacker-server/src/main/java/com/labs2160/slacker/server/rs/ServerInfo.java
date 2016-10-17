package com.labs2160.slacker.server.rs;

import java.util.Date;

import com.labs2160.slacker.core.ApplicationStatus;

public class ServerInfo {

	private ApplicationStatus status;

	private Date startDate;

	public ServerInfo() {}

	public ServerInfo(ApplicationStatus s, Date startDate) {
		this.status = s;
		this.startDate = startDate;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus ApplicationStatus) {
		this.status = ApplicationStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getUptime() {
		if (startDate == null) {
			return "n/a";
		}
		long sec = (System.currentTimeMillis() - startDate.getTime())/1000L;
		StringBuilder str = new StringBuilder();
		if (sec > (3600)) { // more than 1hr
			str.append(" " + (sec/3600) + "h");
			sec = sec % 3600;
		}
		if (sec > 60) {
			str.append(" " + (sec/60) + "m");
			sec = sec %60;
		}
		if (sec > 0 || str.length() == 0) {
			str.append(" " + sec + "s");
		}
		return str.substring(1);
	}
}
