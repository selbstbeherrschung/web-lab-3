package resources;

import beansLab.Shot;

import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

public class DataBaseManagerShots {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataBaseManagerShots.class);

    public TicketsList getTickList() {
        return tickList;
    }

    public BaseOwners getOwners() {
        return owners;
    }

    private TicketsList tickList;
    private BaseOwners owners = null;
    ReentrantLock rLock = new ReentrantLock();

    private String user = "programiifromskolkovo";
    private String password = "programiifromskolkovo";
    private String url = "jdbc:postgresql://localhost:5432/Tickets";
    public Connection connection;

    public DataBaseManagerShots(Connection c) {
        connection = c;
    }

//    public DataBaseManagerTickets(String u, String p, String ur) {
//        user = u;
//        password = p;
//        url = ur;
//        try {
//            connection = DriverManager.getConnection(url, user, password);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

    public void close() throws SQLException {
        connection.close();
    }

    public boolean insertShot(Shot shot, String id){
        return false;
    }

    public Shot[] getShots(String id){
        return null;
    }

    private int sendTown(Location town, boolean insert) throws SQLException {
        if (town == null) {
            return 0;
        }
        String sql;
        if (insert) {
            sql = "INSERT INTO locations (id, x, y, z, name) VALUES (DEFAULT, (?), (?), (?), (?)) RETURNING id;";
        } else {
            sql = "UPDATE locations SET " +
                    "x = (?), y = (?), z = (?), name = (?) WHERE id = (?) RETURNING id;";
        }
        PreparedStatement st = connection.prepareStatement(sql);

        st.setDouble(1, town.getX());
        st.setFloat(2, town.getY());
        st.setLong(3, town.getZ());
        st.setString(4, town.getName());

        if (!insert) {
            st.setInt(5, town.getId());
        }

        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            int i = resultSet.getInt("id");
            town.setId(i);
            resultSet.close();
            st.close();
            return i;
        }
        resultSet.close();
        st.close();
        throw new SQLException();
    }

    private int sendAddress(Address addr, boolean insert) throws SQLException {
        if (addr == null) {
            return 0;
        }
        String sql;
        if (insert) {
            sql = "INSERT INTO address (id, zipcode, town) VALUES (DEFAULT, (?), (?)) RETURNING id;";
        } else {
            sql = "UPDATE address SET " +
                    "zipcode = (?), town = (?) WHERE id = (?) RETURNING id;";
        }
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, addr.getZipCode());
        int i = sendTown(addr.getTown(), insert);
        st.setInt(2, i);
        if (!insert) {
            st.setInt(3, addr.getId());
        }
        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            i = resultSet.getInt("id");
            addr.setId(i);
            resultSet.close();
            st.close();
            return i;
        }
        resultSet.close();
        st.close();
        throw new SQLException();
    }

    private int sendVenue(Venue ven, boolean insert) throws SQLException {
        String sql;
        if (insert) {
            sql = "INSERT INTO venues (id, name, capacity, type, address) VALUES (DEFAULT, (?), (?), (?), (?)) RETURNING id;";
        } else {
            sql = "UPDATE venues SET " +
                    "name = (?), capacity = (?), type = (?), address = (?) WHERE id = (?) RETURNING id;";
        }
        PreparedStatement st = connection.prepareStatement(sql);
        st.setString(1, ven.getName());
        st.setInt(2, ven.getCapacity());
        st.setString(3, ven.getType().toString());

        int i = sendAddress(ven.getAddress(), insert);

        st.setInt(4, i);

        if (!insert) {
            st.setInt(5, (int) ven.getId());
        }

        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            i = resultSet.getInt("id");
            ven.setId(i);
            resultSet.close();
            st.close();
            return i;
        }
        resultSet.close();
        st.close();
        throw new SQLException();
    }

    private int sendCoordinates(Coordinates coord, boolean insert) throws SQLException {
        String sql;
        if (insert) {
            sql = "INSERT INTO coordinates (id, x, y) VALUES (DEFAULT, (?), (?)) RETURNING id;";
        } else {
            sql = "UPDATE coordinates SET " +
                    "x = (?), y = (?) WHERE id = (?) RETURNING id;";
        }
        PreparedStatement st = connection.prepareStatement(sql);


        st.setLong(1, coord.getX());
        st.setDouble(2, coord.getY());

        if (!insert) {
            st.setInt(3, coord.getId());
        }

        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            int i = resultSet.getInt("id");
            coord.setId(i);
            resultSet.close();
            st.close();
            return i;
        }
        resultSet.close();
        st.close();
        throw new SQLException();
    }

    public int sendTicket(Ticket tick, String key) {
        String sql = "INSERT INTO tickets (id, " +
                "name, " +
                "coordinates, " +
                "creationdate, " +
                "price, " +
                "comment, " +
                "refundable, " +
                "type, " +
                "venue, " +
                "userofticket, " +
                "key) VALUES (DEFAULT, " +
                "(?), (?), (?), (?), (?), (?), (?), (?), (?), (?)) RETURNING id;";

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            Savepoint save = connection.setSavepoint();
            st.setString(1, tick.getName());
            int i = sendCoordinates(tick.getCoordinates(), true);
            st.setInt(2, i);
            st.setString(3, tick.getCreationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss ZZ")));
            st.setLong(4, tick.getPrice());
            st.setString(5, tick.getComment());
            st.setBoolean(6, tick.isRefundable());
            st.setString(7, tick.getType().toString());
            i = sendVenue(tick.getVenue(), true);
            st.setInt(8, i);
            st.setInt(9, tick.getTowner().getId());
            st.setString(10, key);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                i = resultSet.getInt("id");
                tick.setId(i);
                connection.commit();
                connection.setAutoCommit(true);
                resultSet.close();
                return i;
            }
            connection.rollback();
            connection.setAutoCommit(true);
            resultSet.close();
        } catch (SQLException e) {
            log.error("Send ticket", e);
        }
        return -1;
    }

    public int updateTicket(Ticket tick, Ticket prev, String key) {
        String sql = "UPDATE tickets SET " +
                "name = (?), " +
                "coordinates = (?), " +
                "creationdate = (?), " +
                "price = (?), " +
                "comment = (?), " +
                "refundable = (?), " +
                "type = (?), " +
                "venue = (?), " +
                "userofticket = (?), " +
                "key = (?) WHERE id = (?) RETURNING id;";
        Preparer preparer = new Preparer(prev, tick);
        preparer.prepareForUpdating();
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            Savepoint save = connection.setSavepoint();
            st.setString(1, tick.getName());
            int i = sendCoordinates(tick.getCoordinates(), false);
            st.setInt(2, i);
            st.setString(3, tick.getCreationDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss ZZ")));
            st.setLong(4, tick.getPrice());
            st.setString(5, tick.getComment());
            st.setBoolean(6, tick.isRefundable());
            st.setString(7, tick.getType().toString());
            i = sendVenue(tick.getVenue(), false);
            st.setInt(8, i);
            st.setInt(9, tick.getTowner().getId());
            st.setString(10, key);
            st.setInt(11, (int) tick.getId());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                i = resultSet.getInt("id");
                tick.setId(i);
                preparer.deleteStuff();
                connection.commit();
                connection.setAutoCommit(true);
                resultSet.close();
                return i;
            }
            connection.rollback();
            connection.setAutoCommit(true);
            resultSet.close();
        } catch (SQLException e) {
            log.error("Send ticket", e);
        }
        return -1;
    }

    public int sendTOwner(TicketOwner towner, boolean insert) {
        String sql;
        if (insert) {
            sql = "INSERT INTO users (id, name, password, mail, salt) VALUES (DEFAULT, (?), (?), (?), (?)) RETURNING id;";
        } else {
            sql = "UPDATE users SET mail = (?) WHERE id = (?) RETURNING id;";
        }
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            if(insert){
                st.setString(1, towner.getName());
                st.setBytes(2, towner.getPassword());
                st.setString(3, towner.getMail());
                st.setString(4, towner.getSalt());
            }

            if (!insert) {
                st.setString(1, towner.getMail());
                st.setInt(2, towner.getId());
            }
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                int i = resultSet.getInt("id");
                towner.setId(i);
                resultSet.close();
                return i;
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Send ticket owner ", e);
        }
        return -1;
    }

    private class Preparer {

        private Ticket prev;
        private Ticket newOne;
        private Address addr = null;
        private Location town = null;

        private Preparer(Ticket prev, Ticket newOne) {
            this.prev = prev;
            this.newOne = newOne;
        }

        private void prepareForUpdating() {
            newOne.setKey(prev.getKey());
            newOne.setTowner(prev.getTowner());
            newOne.getCoordinates().setId(prev.getCoordinates().getId());
            newOne.getVenue().setId(prev.getVenue().getId());
            if (newOne.getVenue().getAddress() == null) {
                addr = prev.getVenue().getAddress();
            } else {
                if (prev.getVenue().getAddress() != null) {
                    newOne.getVenue().getAddress().setId(prev.getVenue().getAddress().getId());
                    if (newOne.getVenue().getAddress().getTown() != null) {
                        if (prev.getVenue().getAddress().getTown() != null) {
                            newOne.getVenue().getAddress().getTown().setId(prev.getVenue().getAddress().getTown().getId());
                        } else {
                            try {
                                sendTown(newOne.getVenue().getAddress().getTown(), true);
                            } catch (SQLException e) {
                                log.error("Inserting new address while updating", e);
                            }
                        }
                    } else {
                        town = prev.getVenue().getAddress().getTown();
                    }
                } else {
                    try {
                        sendAddress(newOne.getVenue().getAddress(), true);
                    } catch (SQLException e) {
                        log.error("Inserting new address while updating", e);
                    }
                }
            }
        }

        private void deleteStuff() {
            if (town != null) {
                try {
                    deleteTown(town);
                } catch (SQLException e) {
                    log.error("Deleting town after update", e);
                }
            }
            if (addr != null) {
                try {
                    deleteAddress(addr);
                } catch (SQLException e) {
                    log.error("Deleting address after update", e);
                }
            }
        }
    }

    private Location receiveTown(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM locations WHERE id = (?);")) {
            st.setInt(1, id);

            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                Double x = resultSet.getDouble("x");
                Float y = resultSet.getFloat("y");
                Long z = resultSet.getLong("z");
                String name = resultSet.getString("name");
                resultSet.close();
                return new Location(id, x, y, z, name);
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Receive town ", e);
        }
        return null;
    }

    private Address receiveAddress(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM address WHERE id = (?);")) {
            st.setInt(1, id);

            ResultSet resultSet = st.executeQuery();

            if (resultSet.next()) {
                String zipcode = resultSet.getString("zipcode");
                int loc = resultSet.getInt("town");
                Location town;
                if (loc == 0) {
                    town = null;
                } else {
                    town = receiveTown(loc);
                }
                resultSet.close();
                return new Address(id, zipcode, town);
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Receive address ", e);
        }
        return null;
    }

    private Venue receiveVenue(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM venues WHERE id = (?);")) {
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Integer capacity = resultSet.getInt("capacity");
                VenueType type = VenueType.valueOf(resultSet.getString("type"));
                int addr = resultSet.getInt("address");
                Address address;
                if (addr == 0) {
                    address = null;
                } else {
                    address = receiveAddress(addr);
                }
                resultSet.close();
                return new Venue(id, name, capacity, type, address);
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Receive venue ", e);
        }
        return null;
    }

    private Coordinates receiveCoordinates(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM coordinates WHERE id = (?);")) {
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                long x = resultSet.getLong("x");
                double y = resultSet.getDouble("y");
                resultSet.close();
                return new Coordinates(id, x, y);
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Ticket receiveTicket(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM tickets WHERE id = (?);")) {
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Coordinates coordinates = receiveCoordinates(resultSet.getInt("coordinates"));
                ZonedDateTime creationDate = ZonedDateTime.parse(resultSet.getString("creationdate"), DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss ZZ"));
                long price = resultSet.getLong("price");
                String comment = resultSet.getString("comment");
                boolean refundable = resultSet.getBoolean("refundable");
                TicketType type = TicketType.valueOf(resultSet.getString("type"));

                Venue venue = receiveVenue(resultSet.getInt("venue"));

                TicketOwner ticketOwner = receiveTOwner(resultSet.getInt("userofticket"));
                String key = resultSet.getString("key");

                if (!owners.containsKey(ticketOwner.getId())) {
                    owners.put(ticketOwner.getId(), ticketOwner);
                }
                Ticket t = new Ticket(id, name, coordinates, creationDate, price, comment, refundable, type, venue, owners.get(ticketOwner.getId()));
                t.setKey(key);
                resultSet.close();
                return t;
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Receive ticket ", e);
        }
        return null;
    }

    public TicketOwner receiveTOwner(int id) {
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM users WHERE id = (?);")) {
            st.setInt(1, id);
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                byte[] password = resultSet.getBytes("password");
                String mail = resultSet.getString("mail");
                String salt = resultSet.getString("salt");
                resultSet.close();
                return new TicketOwner(id, name, password, mail, salt);
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Receive ticket owner ", e);
        }
        System.out.println("Doesn't get TOwner");
        return null;
    }

    private boolean deleteTown(Location town) throws SQLException {
        if (town == null) {
            return true;
        }
        PreparedStatement st = connection.prepareStatement("DELETE FROM locations WHERE id = (?) RETURNING id;");
        st.setInt(1, (int) town.getId());
        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            boolean b = (town.getId() == resultSet.getInt("id"));
            resultSet.close();
            return b;
        }
        resultSet.close();
        throw new SQLException();
    }

    private boolean deleteAddress(Address addr) throws SQLException {
        if (addr == null) {
            return true;
        }
        boolean work = false;
        PreparedStatement st = connection.prepareStatement("DELETE FROM address WHERE id = (?) RETURNING id;");
        st.setInt(1, (int) addr.getId());
        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            work = (addr.getId() == resultSet.getInt("id"));
            work = work & deleteTown(addr.getTown());
            resultSet.close();
            return work;
        }
        resultSet.close();
        throw new SQLException();
    }

    private boolean deleteVenue(Venue ven) throws SQLException {
        if (ven == null) {
            return false;
        }
        boolean work = false;
        PreparedStatement st = connection.prepareStatement("DELETE FROM venues WHERE id = (?) RETURNING id;");
        st.setInt(1, (int) ven.getId());
        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            work = (ven.getId() == resultSet.getInt("id"));
            work = work & deleteAddress(ven.getAddress());
            resultSet.close();
            return work;
        }
        resultSet.close();
        throw new SQLException();
    }

    private boolean deleteCoordinates(Coordinates coord) throws SQLException {
        if (coord == null) {
            return false;
        }
        PreparedStatement st = connection.prepareStatement("DELETE FROM coordinates WHERE id = (?) RETURNING id;");
        st.setInt(1, (int) coord.getId());
        ResultSet resultSet = st.executeQuery();
        if (resultSet.next()) {
            boolean b = (coord.getId() == resultSet.getInt("id"));
            resultSet.close();
            return b;
        }
        resultSet.close();
        throw new SQLException();
    }

    public boolean deleteTicket(Ticket tick) {
        if (tick == null) {
            System.out.println("Can't delete null");
            return false;
        }
        boolean work = false;
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM tickets WHERE id = (?) RETURNING id;")) {
            connection.setAutoCommit(false);
            Savepoint save = connection.setSavepoint();
            st.setInt(1, (int) tick.getId());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                work = (tick.getId() == resultSet.getInt("id"));
                work = work & deleteCoordinates(tick.getCoordinates()) && deleteVenue(tick.getVenue());
                if (work) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
                resultSet.close();
                return work;
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Delete ticket ", e);
        }
        return false;
    }

    public boolean deleteTOwner(TicketOwner towner) {
        try (PreparedStatement st = connection.prepareStatement("DELETE FROM users WHERE id = (?) RETURNING id;")) {
            st.setInt(1, towner.getId());
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                boolean b = (towner.getId() == resultSet.getInt("id"));
                resultSet.close();
                return b;
            }
            resultSet.close();
        } catch (SQLException e) {
            log.error("Delete ticket owner ", e);
        }
        return false;
    }

    boolean fillTable(TicketsList ticketsList, BaseOwners baseOwners) {
        tickList = ticketsList;
        if (fillOwnersTable(baseOwners)) {
            try (PreparedStatement st = connection.prepareStatement("SELECT * FROM tickets;")) {
                ResultSet resultSet = st.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    Coordinates coordinates = receiveCoordinates(resultSet.getInt("coordinates"));
                    ZonedDateTime creationDate = ZonedDateTime.parse(resultSet.getString("creationdate"), DateTimeFormatter.ofPattern("MM/dd/yyyy - HH:mm:ss ZZ"));
                    long price = resultSet.getLong("price");
                    String comment = resultSet.getString("comment");
                    boolean refundable = resultSet.getBoolean("refundable");
                    TicketType type = TicketType.valueOf(resultSet.getString("type"));
                    Venue venue = receiveVenue(resultSet.getInt("venue"));
                    String key = resultSet.getString("key");
                    TicketOwner ticketOwner = receiveTOwner(resultSet.getInt("userofticket"));
                    Ticket t = new Ticket(id, name, coordinates, creationDate, price, comment, refundable, type, venue, owners.get(ticketOwner.getId()));
                    t.setKey(key);
                    ticketsList.put(key, t);
                }
                resultSet.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean fillOwnersTable(BaseOwners baseOwners) {
        owners = baseOwners;
        try (PreparedStatement st = connection.prepareStatement("SELECT * FROM users;")) {
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                if (!baseOwners.containsKey(id)) {
                    String name = resultSet.getString("name");
                    byte[] password = resultSet.getBytes("password");
                    String mail = resultSet.getString("mail");
                    String salt = resultSet.getString("salt");
                    baseOwners.put(id, new TicketOwner(id, name, password, mail, salt));
                }
            }
            resultSet.close();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
