package afekafy;

public class Song {
	
	protected String songTitle;
	protected int timeInSec;
	protected String genre;
	protected int releasedYear;
	// Who uploaded the song will be the user ID related to it
	// When uploaded songs within a new album, the album ID will be related to it.
	
	public Song(String songTitle, int timeInSec, String genre, int releasedYear) {
		this.songTitle = songTitle;
		this.timeInSec = timeInSec;
		this.releasedYear = releasedYear;
		this.genre = genre;
	}
	
}
