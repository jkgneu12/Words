package com.example.words.adapter;

public class UserRowData extends Data {

	public String user;
	public String userName;
	public String userId;

	public UserRowData(String userId, String user, String userName) {
		this.userId = userId;
		this.user = user;
		this.userName = userName;
	}

	@Override
	public String getFilterableName() {
		return userName.toLowerCase();
	}
}
