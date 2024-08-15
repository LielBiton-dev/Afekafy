package afekafy;

public class Artist extends User {
	
	protected int numOfSongs;
	protected int numOfAlbums;
	protected String bio;
	protected String genre;
	
	public Artist(String firstName, String lastName, String email, int yearOfBirth, String password, int numOfSongs, int numOfAlbums, String bio, String genre) {
		super(firstName, lastName, email, yearOfBirth, password);
		this.numOfAlbums = numOfAlbums;
		this.numOfSongs = numOfSongs;
		this.bio = bio;
		this.genre = genre;
	}
	
}
