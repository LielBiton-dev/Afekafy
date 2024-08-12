package afekafy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Site {

    protected String HTML_HOME_PATH;
    protected final String SITE_TEMPLATE_PATH = "afekafy_site_template.html";
    protected final String SITE_PATH = "afekafy_site.html";
    protected final String SITE_REPLAYS_TEMPLATE_PATH = "replays_history_afekafy_site_template.html";
    protected final String SITE_REPLAYS_PATH = "replays_history_afekafy_site.html";

    public Site() {
        // Set HTML_HOME_PATH dynamically based on the current working directory
        HTML_HOME_PATH = Paths.get("").toAbsolutePath().toString() + "\\src\\site_props\\";
    }

    public void showSite(int user_id) throws SQLException, IOException {
        DBConnection db_conn = new DBConnection();
        db_conn.connect();
        
        String replayedQuery = 
                "SELECT COUNT(user_song.user_id) AS replay_count, " +
                "    user_song.song_id, " +
                "    song_table.song_name " +
                "FROM user_song " +
                "JOIN song_table ON user_song.song_id = song_table.song_id " +
                "GROUP BY user_song.song_id, song_table.song_name " +
                "ORDER BY replay_count DESC " +
                "LIMIT 1;";
            
        String newReleasesQuery = 
                "SELECT album_table.album_name, user_table.first_name, user_table.last_name " +
                "FROM album_table " +
                "JOIN user_table ON album_table.artist_id = user_table.user_id " +
                "ORDER BY album_release_year DESC " +
                "LIMIT 3;";
        
        String mostPopularGenreQuery = 
                "SELECT COUNT(song_genre), song_genre FROM song_table " +
                "GROUP BY song_genre " +
                "ORDER BY COUNT(song_genre) DESC " +
                "LIMIT 1;";
        
        String userReplaysQuery = 
                "SELECT user_table.email, song_table.song_name, play_date FROM user_song " +
                "JOIN user_table ON user_song.user_id = user_table.user_id " +
                "JOIN song_table ON user_song.song_id = song_table.song_id " +
                "WHERE user_song.user_id = '" + user_id + "'";
        
        // Execute queries and get results
        try {
            Map<String, Object> replayedResults = db_conn.executeMultipleQueries(replayedQuery);
            ResultSet newReleasesResults = db_conn.executeQuery(newReleasesQuery);
            ResultSet mostPopularGenreResults = db_conn.executeQuery(mostPopularGenreQuery);
            ResultSet userReplayResults = db_conn.executeQuery(userReplaysQuery);
            
            // Process results for "Most Played Songs"
            String songName = (String) replayedResults.get("song_name");
            long playCountLong = (Long) replayedResults.get("replay_count");
            String playCount = Long.toString(playCountLong); // Convert Long to String
            
            // Process results for "Most Popular Genre"
            String genreName = null;  
            if (mostPopularGenreResults.next()) {
                genreName = mostPopularGenreResults.getString("song_genre");
            }
            
            // Process results for "New Albums Released"
            StringBuilder newReleasesHtml = new StringBuilder();
            while (newReleasesResults.next()) {
                String albumName = newReleasesResults.getString("album_name");
                String artistFirstName = newReleasesResults.getString("first_name");
                String artistLastName = newReleasesResults.getString("last_name");

                if (albumName != null && artistFirstName != null && artistLastName != null) {
                    newReleasesHtml.append("<p>").append(albumName)
                                   .append(" by ").append(artistFirstName)
                                   .append(" ").append(artistLastName)
                                   .append("</p>");
                }
            }
            
            // Process user replay results into HTML table
            StringBuilder replayTableHtml = new StringBuilder();
            replayTableHtml.append("<table><tr><th>Song Name</th><th>Play Date</th></tr>");
            while (userReplayResults.next()) {
                String replaySongName = userReplayResults.getString("song_name");
                String playDate = userReplayResults.getString("play_date");

                replayTableHtml.append("<tr>")
            .append("<td style=\"padding:12px; text-align:left; border-bottom:1px solid #ddd;\">").append(replaySongName).append("</td>")
            .append("<td style=\"padding:12px; text-align:left; border-bottom:1px solid #ddd;\">").append(playDate).append("</td>")
            .append("</tr>");
            }
            replayTableHtml.append("</table>");

            
            // Load the HTML file content
            Path path = Paths.get(HTML_HOME_PATH + SITE_TEMPLATE_PATH);
            if (Files.notExists(path)) {
                throw new IOException("File does not exist: " + path.toString());
            }
            
            String htmlContent = Files.readString(path, StandardCharsets.UTF_8);

            // Replace placeholder with actual values
            htmlContent = htmlContent.replace("<!-- Song Name Placeholder -->", songName);
            htmlContent = htmlContent.replace("<!-- Play Count Placeholder -->", playCount);
            htmlContent = htmlContent.replace("<!-- Popular Genre Placeholder -->", genreName);
            htmlContent = htmlContent.replace("<!-- New Releases Placeholder -->", newReleasesHtml.toString());
            
            // Write the updated HTML content back to the file
            Path outputPath = Paths.get(HTML_HOME_PATH + SITE_PATH);
            Files.writeString(outputPath, htmlContent, StandardCharsets.UTF_8);
            
            // Load the HTML file content for the replays history site
            Path replaySitePath = Paths.get(HTML_HOME_PATH + SITE_REPLAYS_TEMPLATE_PATH);
            if (Files.notExists(replaySitePath)) {
                throw new IOException("File does not exist: " + replaySitePath.toString());
            }
            String replayHtmlContent = Files.readString(replaySitePath, StandardCharsets.UTF_8);

            // Insert the replay table HTML into the replays history site
            replayHtmlContent = replayHtmlContent.replace("<!-- Search Results Placeholder -->", replayTableHtml.toString());
            
            // Write the updated HTML content back to the file
            Path replayOutputPath = Paths.get(HTML_HOME_PATH + SITE_REPLAYS_PATH);
            Files.writeString(replayOutputPath, replayHtmlContent, StandardCharsets.UTF_8);

            // Open the updated HTML file in the default browser
            java.awt.Desktop.getDesktop().browse(outputPath.toUri());

        } finally {
            db_conn.closeDBresources();
        }
    }

}
