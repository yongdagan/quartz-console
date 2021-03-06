package org.quartz.console.bean;

import java.io.Serializable;

public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9099759035917813619L;
	
	private Long id;
	
	private String name;
	
	private String owner;
	
	private String targetJob;
	
	private Integer triggerType;
	
	private String cornExpression;
	
	private Integer repeatSeconds;
	
	private String data;
	
	private Integer status;
	
	private Integer refresh;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public String getCornExpression() {
		return cornExpression;
	}

	public void setCornExpression(String cornExpression) {
		this.cornExpression = cornExpression;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Integer getRefresh() {
		return refresh;
	}

	public void setRefresh(Integer refresh) {
		this.refresh = refresh;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getTargetJob() {
		return targetJob;
	}

	public void setTargetJob(String targetJob) {
		this.targetJob = targetJob;
	}

	public Integer getRepeatSeconds() {
		return repeatSeconds;
	}

	public void setRepeatSeconds(Integer repeatSeconds) {
		this.repeatSeconds = repeatSeconds;
	}

}
