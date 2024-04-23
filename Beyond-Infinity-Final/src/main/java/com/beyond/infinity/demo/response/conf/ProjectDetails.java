package com.beyond.infinity.demo.response.conf;

public class ProjectDetails {
	private Long id;
    private String eim;
    
	private String appName;
    private String description;
    private String confluenceTitle;
    private String jiraProjectId;
    private String gitUrl;
    
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEim() {
		return eim;
	}
	public void setEim(String eim) {
		this.eim = eim;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getConfluenceTitle() {
		return confluenceTitle;
	}
	public void setConfluenceTitle(String confluenceTitle) {
		this.confluenceTitle = confluenceTitle;
	}
	public String getJiraProjectId() {
		return jiraProjectId;
	}
	public void setJiraProjectId(String jiraProjectId) {
		this.jiraProjectId = jiraProjectId;
	}
	public String getGitUrl() {
		return gitUrl;
	}
	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}

}
