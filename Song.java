package afekafy;

public class Song {
	
	protected String songTitle;
	protected int timeInSec;
	protected String genre;
	protected int replays;
	protected int releasedYear;
	// Who uploaded the song will be the user ID related to it
	// When uploaded songs within a new album, the album ID will be related to it.
	
	public Song(String songTitle, int time, String genre, int year) {
		this.songTitle = songTitle;
		this.timeInSec = time;
		this.replays = 0;
		this.releasedYear = year;
		this.genre = genre;
	}
	
}
