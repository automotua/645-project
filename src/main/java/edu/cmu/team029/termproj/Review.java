package edu.cmu.team029.termproj;

/**
 * Created by auta on 24/4/16.
 */
public class Review {
	public Review() {
	}

	private int stars;
	private String date;
	private String content;
	private String userName;

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public int getStars() {
		return stars;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public String toString() {
		String retStr = "User name: " + this.userName + "\nDate: " + this.date + "\nStars: " + this.stars +
				"\nContent: " + this.content;
		retStr += "\n";
		return retStr;
	}
}
