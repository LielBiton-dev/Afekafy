package afekafy;

import afekafy.Artist.Genre;

public class Album {
	
	protected String albumTitle;
	protected int releasedYear;
	Genre genre;
	// Who created the album will be the owner user ID of it.
	
	public Album(String title, int year, String genre) {
		this.albumTitle = title;
		this.releasedYear = year;
		setGenre(genre);
	}
	
	public void setGenre(String genre_) { //move to a more general class.
		genre = Genre.valueOf(genre_.toUpperCase());
	}
}
