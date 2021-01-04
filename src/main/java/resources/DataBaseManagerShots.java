package resources;

import beansLab.Shot;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class DataBaseManagerShots {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataBaseManagerShots.class);

    ReentrantLock rLock = new ReentrantLock();

    public Connection connection;

    public DataBaseManagerShots(Connection c) {
        connection = c;
    }

    public void close() throws SQLException {
        connection.close();
    }

    private boolean insertSession(String id){
        String sql = "INSERT INTO SESSIONS3 (session_id, session_str_id) " +
                "VALUES (DEFAULT, (?))";

        try (PreparedStatement st = connection.prepareStatement(sql)){

            st.setNString(1, id );

            ResultSet resultSet = st.executeQuery();

            resultSet.close();
        } catch (SQLException e) {
            log.warn(e.getMessage());
            return false;
        }


        return true;
    }

    private int getSessionId(String id){
        String sql = "SELECT * FROM SESSIONS3 WHERE session_str_id=(?)";

        int result = 0;

        try (PreparedStatement st = connection.prepareStatement(sql)){

            st.setString(1, id);

            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                result = resultSet.getInt("session_id");
            }

            resultSet.close();
        } catch (SQLException e) {
            log.warn(e.getMessage());
            return result;
        }


        return result;
    }


    public boolean insertShot(Shot shot, String id){

        String sql = "SELECT COUNT(*) as c FROM SESSIONS3 WHERE session_str_id = (?)";
        int sessionId = 0;
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, id);

            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();
            st.close();

            if(count == 0){
                insertSession(id);
            }else if(count > 1){
                throw new SQLException("There are more sessions in database, then ordered: " + count);
            }
            sessionId = getSessionId(id);

        }catch (SQLException e){
            log.warn(e.getMessage());
            return false;
        }

        sql = "INSERT INTO SHOTS3 (shot_id, x, y, r, rg, start_time, script_time, session_id) " +
                "VALUES (DEFAULT, (?), (?), (?), (?), (?), (?), (?))";


        try (PreparedStatement st = connection.prepareStatement(sql)){

            st.setDouble(1, shot.getX());
            st.setDouble(2, shot.getY());
            st.setDouble(3, shot.getR());

            st.setInt(4, convertBooleanToInt(shot.isGR()));
            st.setString(5, shot.getStart().format(DateTimeFormatter.ofPattern("dd-MM-yyyy;hh:mm:ss")));
            st.setLong(6, shot.getScriptTime());

            st.setInt(7, sessionId);

            ResultSet resultSet = st.executeQuery();

            resultSet.close();
        } catch (SQLException e) {
            log.warn(e.getMessage());
            return false;
        }


        return true;
    }

    private int convertBooleanToInt(boolean b){
        if(b){
            return 1;
        }else {
            return 0;
        }
    }

    public Shot[] getShots(String id){

        int resultId = getSessionId(id);

        String sql = "SELECT * FROM SHOTS3 WHERE session_id=(?)";

        try (PreparedStatement st = connection.prepareStatement(sql)){
            st.setInt(1, resultId);
            ResultSet resultSet = st.executeQuery();
            LinkedList<Shot> shotLinkedList = new LinkedList<>();
            while (resultSet.next()){
                Double x = resultSet.getDouble("x");
                Double y = resultSet.getDouble("y");
                Double r = resultSet.getDouble("r");
                boolean gr = convertIntToBoolean(resultSet.getInt("rg"));
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy;hh:mm:ss");
                String str = resultSet.getString("start_time");
                LocalDateTime start = parseDate(str);
                long scriptTime = resultSet.getLong("script_time");
                shotLinkedList.addFirst(new Shot(x, y, r, gr, start, scriptTime));
            }
            resultSet.close();

            int j = shotLinkedList.size();
            Shot[] shots = new Shot[j];
            for (int i = 0; i < j; i++) {
                shots[i] = shotLinkedList.get(i);
            }

            return shots;
        } catch (SQLException e) {
            log.warn(e.getMessage());
        }
        return null;
    }

    private boolean convertIntToBoolean(int i){
        return i != 0;
    }

    private LocalDateTime parseDate(String dateTime){
        String[] dateAndTime = dateTime.split(";");
        String[] date = dateAndTime[0].split("-");
        String[] time = dateAndTime[1].split(":");
        return LocalDateTime.of(pInt(date[2]),pInt(date[1]),pInt(date[0]),pInt(time[0]),pInt(time[1]),pInt(time[2]));
    }
    private int pInt(String intS){
        return Integer.valueOf(intS);
    }
}
