/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amazonPrimeVideoMovies;

import connectionManager.MyConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Khushbu
 */
public class temp {

    public static void main(String[] args) {
        try {
            String selectQ = "SELECT prime_movie_id,url FROM amazon.prime_movies_links where asin is null or asin='null'";
            MyConnection.getConnection("amazon");
            ResultSet rs = MyConnection.getResultSet(selectQ);
            while (rs.next()) {

                String url = rs.getString("url");
                int id = rs.getInt("prime_movie_id");
                System.out.println("" + id);
                String asin = StringUtils.substringBetween(url, "/dp/", "/");
                if (asin == null) {
                    asin = StringUtils.substringBetween(url, "%2Fdp%2F", "%2F");
                }
                String uQ = "update amazon.prime_movies_links set asin='" + asin + "' where prime_movie_id=" + id;
                MyConnection.insertData(uQ);
            }
        } catch (SQLException ex) {
            Logger.getLogger(temp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
