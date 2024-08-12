package afekafy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manage manage = new Manage();
        Site site = new Site();
        manage.connect();

        try (Scanner s = new Scanner(System.in)) {
            int currentUserID = -1;  // No user is logged in at the start
            boolean exit = false;

            while (!exit) {
                if (currentUserID == -2) {
                    exit = true;
                } else if (currentUserID == -1) {
                    currentUserID = handleUserLoginOrRegistration(s, manage);
                } else {
                    exit = handleUserMenu(s, manage, site, currentUserID);
                    if (exit) {
                        currentUserID = -1;  // Reset to the initial state
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            manage.closeDBresources();
        }
        System.out.println("Thank you for using Afekafy!");
    }

    public static int handleUserLoginOrRegistration(Scanner s, Manage manage) {
        System.out.println("Welcome to Afekafy!");
        System.out.println("1. Sign up");
        System.out.println("2. Sign in");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getUserChoice(s);
        switch (choice) {
            case 1:
                manage.userRegistration(s);
                return -1;  // Stay in the registration/login state
            case 2:
                return manage.userLogin(s);
            case 3:
                return -2;  // Signal to exit the application
            default:
                System.out.println("Invalid option. Please try again.");
                return -1;  // Stay in the registration/login state
        }
    }

    public static boolean handleUserMenu(Scanner s, Manage manage, Site site, int currentUserID) throws SQLException, IOException {
        printUserMenu(manage, currentUserID);
        int choice = getUserChoice(s);
        return executeUserChoice(choice, s, manage, site, currentUserID);
    }

    public static void printUserMenu(Manage manage, int currentUserID) {
        System.out.println("\nUser Menu:");
        System.out.println("1. Search for Artists, Songs, Playlists, etc.");
        System.out.println("2. Become an artist");
        System.out.println("3. Search And Act On Songs");
        System.out.println("4. Create New Playlist");
        System.out.println("5. Remove Songs From Playlist");
        System.out.println("6. Delete Playlist");
        System.out.println("7. Add An App Review");
        System.out.println("8. Show Site");
        
        int nextOption = 9;
        
        if (manage.isArtist(currentUserID)) {
            System.out.println("\nArtist Options:");
            System.out.println(nextOption++ + ". Add a New Single");
            System.out.println(nextOption++ + ". Add a New Album");
            System.out.println(nextOption++ + ". Add Songs To An Album");
        }
        if (currentUserID != 1) {
            System.out.println(nextOption++ + ". Delete My Account");
        }
        
        if (currentUserID == 1) {
            System.out.println("\nAdmin Options:");
            System.out.println(nextOption++ + ". Print All Users");
        }

        System.out.println(nextOption + ". Exit");
        System.out.print("Choose an option: ");
    }

    public static boolean executeUserChoice(int choice, Scanner s, Manage manage, Site site, int currentUserID) throws SQLException, IOException {

        switch (choice) {
            case 1:
                manage.searchMusicDatabase(s);
                break;
            case 2:
                manage.becomeAnArtist(currentUserID, s);
                break;
            case 3:
                manage.searchAndActOnSong(currentUserID, s);
                break;
            case 4:
                manage.addNewPlaylist(currentUserID, s);
                manage.printUserPlaylists(currentUserID);
                break;
            case 5:
            	manage.removeSongFromPlaylist(currentUserID, s);
                break;
            case 6:
            	manage.deletePlaylist(currentUserID, s);
                break;
            case 7:
            	manage.handleAddAppReview(currentUserID, s);
                break;
            case 8:
            	site.showSite(currentUserID);
                break;
            case 9:
                if (manage.isArtist(currentUserID)) {
                    manage.addSong(manage.getSongDetails(s), currentUserID, 0);
                }
                else if (currentUserID != 1) {
                    manage.deleteUserAccount(currentUserID);
                }
                else if (currentUserID == 1) {
                    manage.printUsers(currentUserID);
                }	
                break;
            case 10:
                if (manage.isArtist(currentUserID)) {
                	manage.addNewAlbum(currentUserID, s);
                	manage.printArtistAlbums(currentUserID);
                }
                else
                	return true;
                break;
            case 11:
                if (manage.isArtist(currentUserID)) {
                	manage.addSongsToAlbum(currentUserID, s);
                }
                break;
            case 12:
                if (currentUserID != 1) {
                    manage.deleteUserAccount(currentUserID);
                }
                break;
            case 13:
                	return true;
            default:
                System.out.println("Invalid option. Please try again.");
                break;
        }
        return false;
    }

    public static int getUserChoice(Scanner s) {
        int choice;
        try {
            choice = s.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            s.nextLine();
            return -1;
        }
        s.nextLine();
        return choice;
    }
}
