import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;
import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding 
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }


    // TODO: Implement this function
    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();
        
        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            // Your implementation goes here....
           
            ResultSet result = stmt.executeQuery("SELECT * FROM " + userTableName);
           

            while(result.next()) {
                
                JSONObject user = new JSONObject();
                int curUserId = result.getInt("USER_ID");
                //Get User specific information
                user.put("user_id", result.getInt("USER_ID"));
                user.put("first_name", result.getString("FIRST_NAME"));
                user.put("last_name", result.getString("LAST_NAME"));
                user.put("gender", result.getString("gender"));
                user.put("YOB", result.getInt("year_of_birth"));
                user.put("MOB", result.getInt("month_of_birth"));
                user.put("DOB", result.getInt("day_of_birth"));

                //Get user friend information
                JSONArray friends = new JSONArray();
                Statement friendStmt = oracleConnection.createStatement();
                ResultSet friendResult = friendStmt.executeQuery("SELECT user2_id FROM " + friendsTableName + " WHERE user1_id = " + curUserId);
                while(friendResult.next()) {
                    friends.put(friendResult.getInt("user2_id"));
                }
                user.put("friends", friends);
                friendResult.close();
                friendStmt.close();

                //Get current city info
                JSONObject currentCityObj = new JSONObject();
                Statement cityStmt = oracleConnection.createStatement();
                ResultSet currentCityResult = cityStmt.executeQuery("SELECT c.city_name, c.state_name, c.country_name " +
                                                                "FROM " + currentCityTableName + " ucc " + 
                                                                "JOIN " + cityTableName + " c ON ucc.current_city_id = c.city_id " +
                                                                "WHERE ucc.user_id = " + curUserId);
                

                if(currentCityResult.next()) {
                    currentCityObj.put("city", currentCityResult.getString("city_name"));
                    currentCityObj.put("state", currentCityResult.getString("state_name"));
                    currentCityObj.put("country", currentCityResult.getString("country_name"));
                }
                user.put("current_city", currentCityObj);
                currentCityResult.close();
                cityStmt.close();
                
                //Get hometown city info
                JSONObject hometownCityObj = new JSONObject();
                Statement hometownCityStmt = oracleConnection.createStatement();
                ResultSet hometownCityResult = hometownCityStmt.executeQuery("SELECT c.city_name, c.state_name, c.country_name " +
                                                                "FROM " + hometownCityTableName + " uhc " + 
                                                                "JOIN " + cityTableName + " c ON uhc.hometown_city_id = c.city_id " +
                                                                "WHERE uhc.user_id = " + curUserId);
                
                if(hometownCityResult.next()) {
                   
                    hometownCityObj.put("city", hometownCityResult.getString("city_name"));
                    hometownCityObj.put("state", hometownCityResult.getString("state_name"));
                    hometownCityObj.put("country", hometownCityResult.getString("country_name"));
                }
                user.put("hometown", hometownCityObj);
                hometownCityResult.close();
                hometownCityStmt.close();
                //System.out.println(userToString(user));

                users_info.put(user);
            }

            System.out.println("before close");
            stmt.close();
            System.out.println("after close");
        } catch (SQLException e) {
            System.out.println("uhuh");
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
