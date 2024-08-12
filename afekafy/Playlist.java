package afekafy;

public class Playlist {
	
	public enum permission {
		PUBLIC, PRIVATE
	};
	
	protected String title;
	protected int creationYear;
	permission permissionType;
	// Who created the playlist will be the owner user ID of it.
	
	public Playlist(String title, int year, String permission) {
		this.creationYear = year;
		this.title = title;
		setPermission(permission);
	}
	
	public void setPermission(String per) {
		permissionType = permission.valueOf(per.toUpperCase());
	}
}
