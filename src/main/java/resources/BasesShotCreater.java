package resources;

import java.io.PrintWriter;
import java.sql.*;

public class BasesShotCreater {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(BasesShotCreater.class);


    private static String user = "programiifromskolkovo";
    private static String password = "programiifromskolkovo";
    private static String url = "jdbc:postgresql://localhost:5432/Tickets";
    private static String commands = "CREATE TABLE IF NOT EXISTS locations\n" +
            "(\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    x double precision NOT NULL,\n" +
            "    y real NOT NULL,\n" +
            "    z bigint NOT NULL,\n" +
            "    name character varying COLLATE pg_catalog.\"default\"\n" +
            ");\n" +
            "CREATE TABLE IF NOT EXISTS address\n" +
            "(\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    zipcode character varying COLLATE pg_catalog.\"default\",\n" +
            "    town integer,\n" +
            "    FOREIGN KEY (town) REFERENCES locations (id)\n" +
            ");\n" +
            "CREATE TABLE IF NOT EXISTS venues\n" +
            "(\n" +
            "    id BIGSERIAL PRIMARY KEY,\n" +
            "    name character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    capacity integer,\n" +
            "    type character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    address integer,\n" +
            "    FOREIGN KEY (address) REFERENCES address (id)\n" +
            ");\n" +
            "CREATE TABLE IF NOT EXISTS coordinates\n" +
            "(\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    x bigint NOT NULL,\n" +
            "    y double precision NOT NULL\n" +
            ");\n" +
            "CREATE TABLE IF NOT EXISTS users\n" +
            "(\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    name character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    mail character varying COLLATE pg_catalog.\"default\",\n" +
            "    salt character varying COLLATE pg_catalog.\"default\",\n" +
            "    password bytea\n" +
            ");\n" +
            "CREATE TABLE IF NOT EXISTS tickets\n" +
            "(\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    name character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    coordinates integer NOT NULL,\n" +
            "    creationdate character varying(30) COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    price integer NOT NULL,\n" +
            "    comment character varying COLLATE pg_catalog.\"default\",\n" +
            "    refundable boolean NOT NULL,\n" +
            "    type character varying COLLATE pg_catalog.\"default\",\n" +
            "    venue integer NOT NULL,\n" +
            "    key character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
            "    userofticket integer NOT NULL,\n" +
            "    FOREIGN KEY (coordinates) REFERENCES coordinates (id),\n" +
            "    FOREIGN KEY (userofticket) REFERENCES users (id),\n" +
            "    FOREIGN KEY (venue) REFERENCES venues (id)\n" +
            ");\n"
            ;
    private static String sqlLocation = "INSERT INTO locations(id, x, y, z, name) VALUES (0, 0, 0, 0, null);\n";
    private static String sqlAddress = "INSERT INTO address(id, zipcode, town) VALUES (0, null, 0);";


    public static DataBaseManagerShots getDataBase(){
        Connection connection;
        DriverManager.setLogWriter(new PrintWriter(System.out));
        try {
            connection = DriverManager.getConnection(url, user, password);
            log.info("Get base connection");
            DataBaseManagerShots dbmt = new DataBaseManagerShots(connection);
            Statement st = connection.createStatement();
            st.execute(commands);
            st.close();

            Statement call = connection.createStatement();
            ResultSet rs = call.executeQuery("SELECT * FROM locations");
            int i = -1;
            while (rs.next()){
                i = rs.getInt("id");
                if(i == 0){
                    break;
                }
            }
            rs.close();
            call.close();
            if (i != 0){
                Statement stL = connection.createStatement();
                stL.execute(sqlLocation);
                stL.close();
            }


            call = connection.createStatement();
            rs = call.executeQuery("SELECT * FROM address");
            i = -1;
            while (rs.next()){
                i = rs.getInt("id");
                if(i == 0){
                    break;
                }
            }
            rs.close();
            call.close();
            if (i != 0){
                Statement stA = connection.createStatement();
                stA.execute(sqlAddress);
                stA.close();

            }

            dbmt.fillTable(new TicketsList(), new BaseOwners());
            log.info("Server tables filled: T - " + dbmt.getTickList().size() + " O - " + dbmt.getOwners().size());

            return dbmt;
        } catch (SQLException e) {
            log.info("Date base exception", e);
        }
        return null;
    }
}
