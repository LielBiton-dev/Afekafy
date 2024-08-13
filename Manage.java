package afekafy;
import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Manage {
	
    // Database connection parameters
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/Afekafy";
    static final String USER = "postgres";
    static final String PASS = "Liel4321";
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
    // Method to connect to the database
    public void connect(){
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void closeDBresources() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void printUserDetails(int userId) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM user_table WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
           
            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String name = firstName + " " + lastName;
                String email = rs.getString("email");
                int yearOfBirth = rs.getInt("birth_year");
                int age = calculateAge(yearOfBirth);
                String password = rs.getString("user_password");
                
                System.out.printf("%-10d %-20s %-30s %-5d %-15s", userId, name, email, age, password);
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void printUsers(int userID) {
    	// Assuming admin is a userID 1
        if (userID != 1) {
            System.out.println("Access denied. Only admin users can print all users.");
            return;
        }
        
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user_id FROM user_table");
            
            System.out.println("\nUsers:");
            System.out.println("=".repeat(80)); 
            System.out.printf("%-10s %-20s %-30s %-5s %-15s%n", "User ID", "Name", "Email", "Age", "Password");
            System.out.println("=".repeat(80));            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                // Use the helper function to print user details
                printUserDetails(userId);
                System.out.println();
            }

        } 	catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        
    }
    
    public void printArtists(int userID) {
        // Assuming admin is a userID 1
        if (userID != 1) {
            System.out.println("Access denied. Only admin users can print all artists.");
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM artist_table";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nArtists:");
            System.out.println("=".repeat(150));
            System.out.printf("%-10s %-20s %-30s %-5s %-20s %-12s %-12s %-12s %-10s%n", 
                              "User ID", "Name", "Email", "Age", "Password", 
                              "Genre", "Num Songs", "Num Albums", "Bio");
            System.out.println("=".repeat(150));

            // Print artist details
            while (rs.next()) {
                String bio = rs.getString("bio");
                String genre = rs.getString("artist_genre");
                int numOfSongs = rs.getInt("num_of_songs");
                int numOfAlbums = rs.getInt("num_of_albums");
                int artistId = rs.getInt("artist_id");
                printUserDetails(artistId);   
                System.out.printf("%-5s %-12s %-12d %-12d %-10s", "",genre, numOfSongs, numOfAlbums, bio);
                System.out.println();
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public int calculateAge(int yearOfBirth) {
        return LocalDate.now().getYear() - yearOfBirth;
    }
    
    public void userRegistration (Scanner s) {
    	
    	String firstName, lastName, email, password;
    	int yearOfBirth;
        // Get user details
        System.out.print("Enter first name: ");
        firstName = s.nextLine();

        System.out.print("Enter last name: ");
        lastName = s.nextLine();

        System.out.print("Enter email: ");
        email = s.nextLine();

        System.out.print("Enter year of birth: ");
        yearOfBirth = s.nextInt();
        s.nextLine();

        System.out.print("Enter password: ");
        password = s.nextLine();

        // Create a new User instance
        User newUser = new User(firstName, lastName, email, yearOfBirth, password);

        // Insert the new user into the database
        insertUserIntoDatabase(newUser);
    }
    
    public void insertUserIntoDatabase(User user) {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create SQL insert statement
            String sql = "INSERT INTO user_table (first_name, last_name, email, birth_year, user_password, registration_date) " +
                         "VALUES (?, ?, ?, ?, ?, NOW())";

            // Set the parameters
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.firstName);
            pstmt.setString(2, user.lastName);
            pstmt.setString(3, user.email);
            pstmt.setInt(4, user.yearOfBirth);
            pstmt.setString(5, user.password);

            // Execute the insert
            pstmt.executeUpdate();

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

    }
    
    public int userLogin(Scanner s) {
    	String email, password;
    	
        System.out.print("Enter your email: ");
        email = s.nextLine();

        System.out.print("Enter your password: ");
        password = s.nextLine();
        
        int userID = authenticateUser(email, password);

        if (userID != -1) {
            System.out.println("User authenticated successfully.");
        } else {
            System.out.println("Invalid email or password. Try again");
        }
        return userID;
    }
    
    public int authenticateUser(String email, String password) {
    	int userID = -1;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create SQL select statement and set the parameters
            pstmt = conn.prepareStatement("SELECT user_id, user_password FROM user_table WHERE email = ?");
            pstmt.setString(1, email);

            // Execute the query
            rs = pstmt.executeQuery();

            // Check if a user was found and if the password matches
            if (rs.next()) {
                String storedPassword = rs.getString("user_password");
                if (storedPassword.equals(password)) {
                    userID = rs.getInt("user_id");
                }
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        return userID; // Return false if user not found or any exception occurs
    }
    
    public void deleteUserAccount(int userID) {
    	try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Set the parameters
            pstmt = conn.prepareStatement("DELETE FROM user_table WHERE user_id = ?");
            pstmt.setInt(1, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user. User ID may not exist.");
            }
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

    }
    
    public void becomeAnArtist(int userId, Scanner s) {

        // Ask for the additional traits required for an artist
        System.out.print("Enter the number of songs you have: ");
        int numOfSongs = s.nextInt();
        s.nextLine();

        System.out.print("Enter the number of albums you have: ");
        int numOfAlbums = s.nextInt();
        s.nextLine();

        System.out.print("Enter a short bio: ");
        String bio = s.nextLine();

        // Set the genre
        String genre;
        while (true) {
            System.out.print("Enter your genre (POP, ROCK, COUNTRY, ELECTRONIC, JAZZ): ");
            genre = s.nextLine();
            try {
                new Artist("test","test", "test@example.test", 1999, "test", numOfSongs, numOfAlbums, bio, genre);
                break; // If no exception, genre is valid
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage()); // Invalid genre, prompt again
            }
        }

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "INSERT INTO artist_table (artist_id, artist_genre, num_of_songs, num_of_albums, bio) "
            		+ "VALUES (?, ?::genre, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase());
            pstmt.setInt(3, numOfSongs);
            pstmt.setInt(4, numOfAlbums);
            pstmt.setString(5, bio);

            pstmt.executeUpdate();
            System.out.println("You have successfully become an artist!");

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public Song getSongDetails(Scanner s) {
        
    	System.out.print("Enter song title: ");
        String songTitle = s.nextLine();

        System.out.print("Enter song duration in seconds: ");
        int timeInSec = s.nextInt();
        s.nextLine();

        System.out.print("Enter song genre (POP, ROCK, COUNTRY, ELECTRONIC, JAZZ): ");
        String genre = s.nextLine(); //should be a check for valid genre in here!!

        System.out.print("Enter release year: ");
        int releasedYear = s.nextInt();
        s.nextLine();

        return new Song(songTitle, timeInSec, genre, releasedYear);
    }
    
    public boolean isValidAlbumChoice(int albumID, int artistID) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "SELECT COUNT(*) FROM album_table WHERE album_id = ? AND artist_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, albumID);
            pstmt.setInt(2, artistID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        return false;
    }
    
    public int selectAlbum(int userId, Scanner s) {
        printArtistAlbums(userId);
        
        System.out.println("Select the album to add songs to by entering its id number:");
        int choice = s.nextInt();
        
        while (!isValidAlbumChoice(choice, userId)) {
            System.out.println("Invalid album ID. Please enter a valid album ID:");
            choice = s.nextInt();
        }
        s.nextLine();
        return choice; // Return the chosen album ID
    }
    
    public void addSongsToAlbum(int artistID, Scanner s) {
        if (!isArtist(artistID)) {
            System.out.println("Only artists can add songs.");
            return;
        }
        
        int albumID = selectAlbum(artistID, s);
        System.out.print("How many songs do you want to add to this album? ");
        int numberOfSongs = s.nextInt();
        s.nextLine();

        for (int i = 0; i < numberOfSongs; i++) {
            System.out.println("Adding song " + (i + 1) + " of " + numberOfSongs + ":");
            Song newSong = getSongDetails(s);
            addSong(newSong, artistID, albumID);
        }

        System.out.println(numberOfSongs + " songs have been added to album with ID: " + albumID);
    }
    
    public void addSong(Song song, int artistID, int albumId) {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Create SQL insert statement
            String sql = "INSERT INTO song_table (song_name, duration, song_genre, replays, song_release_year, artist_id, album_id) "
            		+ "VALUES (?, ?, ?::genre, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            // Set the parameters
            pstmt.setString(1, song.songTitle);
            pstmt.setInt(2, song.timeInSec);
            pstmt.setString(3, song.genre.toString().substring(0, 1).toUpperCase() + song.genre.toString().substring(1).toLowerCase());
            pstmt.setInt(4, song.replays);
            pstmt.setInt(5, song.releasedYear);
            pstmt.setInt(6, artistID);
            
            if (albumId != 0) {
                pstmt.setInt(7, albumId);
            } else {
                pstmt.setNull(7, java.sql.Types.INTEGER);
            }
            
            // Execute the insert
            pstmt.executeUpdate();
            System.out.println("Song added successfully!");

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public boolean isArtist(int userId) {
        
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM artist_table WHERE artist_id = ?");
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        return false;
    }
    
    public void addNewAlbum(int userId, Scanner s) {
        if (!isArtist(userId)) {
            System.out.println("Only artists can add albums.");
            return;
        }

        System.out.print("Enter album title: ");
        String albumTitle = s.nextLine();

        System.out.print("Enter album genre (POP, ROCK, COUNTRY, ELECTRONIC, JAZZ): ");
        String albumGenre = s.nextLine();

        System.out.print("Enter album release year: ");
        int albumReleaseYear = s.nextInt();
        s.nextLine();

        // Create a new Album object
        Album newAlbum = new Album(albumTitle, albumReleaseYear, albumGenre);

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "INSERT INTO album_table (album_name, album_genre, album_release_year, artist_id) "
            		+ "VALUES (?, ?::genre, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newAlbum.albumTitle);
            pstmt.setString(2, newAlbum.genre.toString().substring(0, 1).toUpperCase() + newAlbum.genre.toString().substring(1).toLowerCase());
            pstmt.setInt(3, newAlbum.releasedYear);
            pstmt.setInt(4, userId);

            pstmt.executeUpdate();
            System.out.println("Album added successfully!");

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }

    public void deleteAlbum(int userId, Scanner s) {
        if (!isArtist(userId)) {
            System.out.println("Only artists can delete albums.");
            return;
        }

        System.out.print("Enter album ID to delete: ");
        int albumId = s.nextInt();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
        	String sql = "DELETE FROM album_table WHERE album_id = ? AND artist_id = ?";
        	pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, albumId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Album deleted successfully!");
            } else {
                System.out.println("No album found with this ID for the given artist.");
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void printArtistAlbums(int userId) {
        if (!isArtist(userId)) {
            System.out.println("Only artists can view their albums.");
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "SELECT * FROM album_table WHERE artist_id = ?";
            pstmt = conn.prepareStatement(sql); 
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Albums:");
            System.out.println("=".repeat(55)); 
            System.out.printf("%-10s %-15s %-10s %-10s%n", "Album ID", "Album Name", "Genre", "Release Year");
            System.out.println("=".repeat(55)); 

            while (rs.next()) {
                Album album = new Album(rs.getString("album_name"), rs.getInt("album_release_year"), rs.getString("album_genre"));
                int albumId = rs.getInt("album_id");              
                System.out.printf("%-10d %-15s %-10s %-10d%n", albumId, album.albumTitle, album.genre.toString(), album.releasedYear);
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void updateReplays(int songId) {

        try {
        	
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "UPDATE song_table SET replays = replays + 1 WHERE song_id = ?";
            pstmt = conn.prepareStatement(sql); 
            pstmt.setInt(1, songId);
            pstmt.executeUpdate();
            System.out.println("Replays updated successfully!");

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void playSong(int songId, int userId) {
        // Increase replays
        updateReplays(songId);

        // Add to user_song table
        try {
        	
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String sql = "INSERT INTO user_song (user_id, song_id, play_date) VALUES (?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, songId);

            pstmt.executeUpdate();
            System.out.println("Song played and added to history!");

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void printAllSongs() {
    	
        try {
        	Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            pstmt = conn.prepareStatement("SELECT * FROM song_table");
            
            System.out.println("Songs:");
            System.out.println("=".repeat(110));
            System.out.printf("%-10s %-30s %-10s %-15s %-10s %-10s %-10s %-10s%n", 
                              "ID", "Title", "Duration", "Genre", "Replays", "Year", "Artist ID", "Album ID");
            System.out.println("=".repeat(110));

            while (rs.next()) {         	
                System.out.printf("%-10d %-30s %-10d %-15s %-10d %-10d %-10d %-10d%n", 
                        rs.getInt("song_id"), 
                        rs.getString("song_name"), 
                        rs.getInt("duration"), 
                        rs.getString("song_genre"), 
                        rs.getInt("replays"), 
                        rs.getInt("song_release_year"), 
                        rs.getInt("artist_id"), 
                        rs.getInt("album_id"));
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void deleteSong(int songId, int userId) {

        try {
        	Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "DELETE FROM songs WHERE song_id = ? AND artist_id = ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, songId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Song deleted successfully!");
            } else {
                System.out.println("No song found with this ID for the given artist.");
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public boolean searchSongs(String query) {
        boolean found = false;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM song_table WHERE song_name ILIKE ?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Songs:");
            System.out.println("=".repeat(110));
            System.out.printf("%-10s %-30s %-10s %-15s %-10s %-10s %-10s %-10s%n", 
                              "ID", "Title", "Duration", "Genre", "Replays", "Year", "Artist ID", "Album ID");
            System.out.println("=".repeat(110));

            while (rs.next()) {
            	found = true;
                System.out.printf("%-10d %-30s %-10d %-15s %-10d %-10d %-10d %-10d%n", 
                        rs.getInt("song_id"), 
                        rs.getString("song_name"), 
                        rs.getInt("duration"), 
                        rs.getString("song_genre"), 
                        rs.getInt("replays"), 
                        rs.getInt("song_release_year"), 
                        rs.getInt("artist_id"), 
                        rs.getInt("album_id"));
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return found;
    }
    
    public void searchAndActOnSong(int userId, Scanner s) {
        System.out.print("Enter song name to search: ");
        String query = s.nextLine();
        if (!searchSongs(query)) {
        	System.out.println("No songs found matching the query: " + query);
        	return;
        }

        System.out.print("\nChoose a song by ID number: ");
        int choice = s.nextInt();

        System.out.println("1: Play song");
        System.out.println("2: Delete song (if you are the artist)");
        System.out.println("3: Add song to a playlist");
        System.out.print("Choose an action: ");
        int action = s.nextInt();
        s.nextLine();

        switch (action) {
            case 1:
                playSong(choice, userId);
                break;
            case 2:
                deleteSong(choice, userId);
                break;
            case 3:
            	choosePlaylistAndAddSong(choice, userId, s);
                break;
            default:
                System.out.println("Invalid action.");
        }
    }
    
    public void addNewPlaylist(int userId, Scanner s) {

        System.out.print("Enter playlist title: ");
        String title = s.nextLine();
        // Generate current year
        int creationYear = Year.now().getValue();

        System.out.print("Enter playlist permission (PUBLIC/PRIVATE): ");
        String permission = s.nextLine(); //add valid check

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "INSERT INTO playlist_table (playlist_name, creation_year, permissions, user_id) "
            		+ "VALUES (?, ?, ?::permission, ?)";
            pstmt = conn.prepareStatement(sql);

            // Set the parameters
            pstmt.setString(1, title);
            pstmt.setInt(2, creationYear);
            pstmt.setString(3, permission.substring(0, 1).toUpperCase() + permission.substring(1).toLowerCase());
            pstmt.setInt(4, userId);

            // Execute the insert
            pstmt.executeUpdate();
            System.out.println("Playlist added successfully!");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updatePlaylist(int playlistId, int songId, boolean add) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            if (add) {
                // Add song to playlist
                String sql = "INSERT INTO song_playlist (song_id, playlist_id) VALUES (?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, songId);
                pstmt.setInt(2, playlistId);
                pstmt.executeUpdate();
                System.out.println("Song added to playlist successfully!");
            } else {
                // Delete song from playlist
                String sql = "DELETE FROM song_playlist WHERE song_id = ? AND playlist_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, songId);
                pstmt.setInt(2, playlistId);
                pstmt.executeUpdate();
                System.out.println("Song removed from playlist successfully!");
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void printUserPlaylists(int userId) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM playlist_table WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Your playlists:");
            System.out.println("=".repeat(60));
            System.out.printf("%-10s %-20s %-10s %-15s%n", 
                              "ID", "Title", "Year", "Permission");
            System.out.println("=".repeat(60));

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10d %-15s%n", 
                        rs.getInt("playlist_id"), 
                        rs.getString("playlist_name"), 
                        rs.getInt("creation_year"), 
                        rs.getString("permissions"));
          }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void choosePlaylistAndAddSong(int songId, int userId, Scanner s) {
        printUserPlaylists(userId);

        System.out.print("Enter the ID of the playlist you want to add the song to: ");
        int playlistId = s.nextInt();
        s.nextLine();

        // Check if the playlist exists and belongs to the user
        if (playlistExistsAndBelongsToUser(playlistId, userId)) {
            // Add song to playlist
            updatePlaylist(playlistId, songId, true);
        } else {
            System.out.println("Invalid playlist ID or you do not own this playlist.");
        }
    }
    
    public boolean playlistExistsAndBelongsToUser(int playlistId, int userId) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT 1 FROM playlist_table WHERE playlist_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
            
        } catch (SQLException | ClassNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
        }
    }
    
    public void deletePlaylist(int userId, Scanner s) {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            printUserPlaylists(userId);
            System.out.print("Enter the ID of the playlist you want to delete: ");
            int playlistIdToDelete = s.nextInt();
            s.nextLine();

            // Delete the playlist
            String deletePlaylistSQL = "DELETE FROM playlist_table WHERE playlist_id = ? AND user_id = ?";
            pstmt = conn.prepareStatement(deletePlaylistSQL);
            pstmt.setInt(1, playlistIdToDelete);
            pstmt.setInt(2, userId);
            /*int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Playlist deleted successfully!");
                // Also delete from song_playlist table
                String deleteSongsSQL = "DELETE FROM song_playlist WHERE playlist_id = ?";
                pstmt = conn.prepareStatement(deleteSongsSQL);
                pstmt.setInt(1, playlistIdToDelete);
                pstmt.executeUpdate();
            } else {
                System.out.println("Failed to delete playlist. Make sure you own the playlist and it exists.");
            }*/

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void printSongsInPlaylist(int playlistId) {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT s.song_id, s.song_name, s.duration, s.song_genre, s.replays, s.song_release_year " +
                         "FROM song_table s " +
                         "JOIN song_playlist sp ON s.song_id = sp.song_id " +
                         "WHERE sp.playlist_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Songs in the playlist:");
            System.out.println("=".repeat(150));
            System.out.printf("%-10s %-30s %-10s %-15s %-10s %-5s%n", 
                              "ID", "Title", "Duration", "Genre", "Replays", "Year");
            System.out.println("=".repeat(150));

            while (rs.next()) {         	
                System.out.printf("%-10d %-30s %-10d %-15s %-10d %-5d%n", 
                        rs.getInt("song_id"), 
                        rs.getString("song_name"), 
                        rs.getInt("duration"), 
                        rs.getString("song_genre"), 
                        rs.getInt("replays"), 
                        rs.getInt("song_release_year"));
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
       
    public void removeSongFromPlaylist(int userId, Scanner s) {
        printUserPlaylists(userId);

        System.out.print("Enter the ID of the playlist to manage songs: ");
        int playlistId = s.nextInt();
        s.nextLine();

        // Check if the playlist exists and belongs to the user
        if (!playlistExistsAndBelongsToUser(playlistId, userId)) {
            System.out.println("Invalid playlist ID or you do not own this playlist.");
            return;
        }

        // Print all songs in the selected playlist
        printSongsInPlaylist(playlistId);

        System.out.print("Enter the ID of the song to remove from this playlist: ");
        int songId = s.nextInt();
        s.nextLine();
        
        updatePlaylist(playlistId, songId, false);
    }
    
    public void handleAddAppReview(int userId, Scanner s) {
        System.out.print("Enter your review text: ");
        String reviewText = s.nextLine();

        System.out.print("Enter your rating (1-5): ");
        int rating = s.nextInt();
        s.nextLine();

        addAppReview(userId, reviewText, rating);
    }
    
    public void addAppReview(int userId, String reviewText, int rating) {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Check if the user has already written a review
            String checkReviewSQL = "SELECT COUNT(*) FROM app_review_table WHERE user_id = ?";
            pstmt = conn.prepareStatement(checkReviewSQL);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            int reviewCount = rs.getInt(1);

            if (reviewCount > 0) {
                System.out.println("User has already written a review.");
                return;
            }

            // Insert the new review
            String sql = "INSERT INTO app_review_table (app_rating, review_text, review_date, user_id) VALUES (?, ?, NOW(), ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, rating);
            pstmt.setString(2, reviewText);
            pstmt.setInt(3, userId);
            pstmt.executeUpdate();
            System.out.println("Review added successfully!");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void printAllReviews(int userId) {
        if (userId != 1) {
            System.out.println("Access denied. Only admin users can print all app reviews.");
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            String sql = "SELECT * FROM app_review_table ORDER BY app_rating ASC";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Reviews:");
            System.out.println("=".repeat(100));
            System.out.printf("%-10s %-7s %-50s %-15s %-10s%n", 
                              "Review ID", "Rating", "Review", "Date", "User ID");
            System.out.println("=".repeat(100));
            
            while (rs.next()) {
                int reviewId = rs.getInt("review_id");
                int rating = rs.getInt("app_rating");
                String reviewText = rs.getString("review_text");
                LocalDate reviewDate = rs.getDate("review_date").toLocalDate();
                String formattedDate = reviewDate.format(formatter);
                int reviewUserId = rs.getInt("user_id");

                System.out.printf("%-10d %-7d %-50s %-15s %-10d%n", reviewId, rating, reviewText, formattedDate, reviewUserId);
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void searchMusicDatabase(Scanner s) {
    	
        System.out.print("Search: ");
        String searchString = s.nextLine();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
            String query = "SELECT * FROM ("
                    + "SELECT song_name AS name, song_id AS id, 'song' AS type FROM song_table "
                    + "UNION "
                    + "SELECT CONCAT(first_name, ' ', last_name) AS name, user_id AS id, 'artist' AS type "
                    + "FROM artist_table "
                    + "JOIN user_table ON artist_table.artist_id = user_table.user_id "
                    + "UNION "
                    + "SELECT playlist_name AS name, playlist_id AS id, 'playlist' AS type FROM playlist_table "
                    + "UNION "
                    + "SELECT album_name AS name, album_id AS id, 'album' AS type FROM album_table) "
                    + "AS search_table WHERE name ILIKE '%' || ? || '%'";
            
            pstmt = conn.prepareStatement(query);             
            pstmt.setString(1, searchString);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Search Results:");
            System.out.println("==================================================");
            System.out.printf("%-5s %-30s %-15s%n", "ID", "Name", "Type");
            System.out.println("==================================================");
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                System.out.printf("%-5d %-30s %-15s%n", id, name, type);
            }

            System.out.println("==================================================");

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
      
}
