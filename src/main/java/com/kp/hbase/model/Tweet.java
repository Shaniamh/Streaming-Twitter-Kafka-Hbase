package com.kp.hbase.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Tweet {
	
	private String created_at;
	private long id;
	private String text;
	private String typePost;
	private DataUser dataUser;
	
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTypePost() {
		return typePost;
	}
	public void setTypePost(String typePost) {
		this.typePost = typePost;
	}
	public DataUser getDataUser() {
		return dataUser;
	}
	public void setDataUser(DataUser dataUser) {
		this.dataUser = dataUser;
	}
	

	
	
	
}