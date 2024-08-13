package afekafy;

public class Artist extends User {
	
	public enum Genre {
		POP, ROCK, COUNTRY, ELECTRONIC, JAZZ
	};
	protected int numOfSongs;
	protected int numOfAlbums;
	protected String bio;
	Genre genre;
	
	public Artist(String firstName, String lastName, String email, int yearOfBirth, String password, int numOfSongs, int numOfAlbums, String bio, String genre) {
		super(firstName, lastName, email, yearOfBirth, password);
		this.numOfAlbums = numOfAlbums;
		this.numOfSongs = numOfSongs;
		this.bio = bio;
		setGenre(genre);
	}
	
	public void setGenre(String genre_) {
        try {
        	genre = Genre.valueOf(genre_.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid genre. Please enter a valid genre.");
        }
	}

}
