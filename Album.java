package afekafy;

public class Album {
	
	protected String albumTitle;
	protected int releasedYear;
	String genre;
	// Who created the album will be the owner user ID of it.
	
	public Album(String title, int year, String genre) {
		this.albumTitle = title;
		this.releasedYear = year;
		this.genre = genre;
	}
	
}
