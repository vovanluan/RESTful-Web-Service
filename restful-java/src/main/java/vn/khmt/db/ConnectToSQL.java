package vn.khmt.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

/**
 *
 * @author TheNhan
 * @version 0.1
 */
public class ConnectToSQL {

    public static final String ERROR = "Error";
    public static final String NOTMATCH = "NotMatch";
    public static final String SQLSERVER = "sqlserver";
    public static final String SQLSERVERDRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String MYSQL = "mysql";
    public static final String MYSQLDRIVER = "com.mysql.jdbc.Driver";
    public static final String POSTGRESQL = "postgresql";
    public static final String POSTGRESQLDRIVER = "org.postgresql.Driver";

    Connection dbConnection = null;

    public ConnectToSQL(String type, String host, String dbname, String user, String pwd) {
        this.dbConnection = getDBConnection(type, host, dbname, user, pwd);
    }

    private Connection getDBConnection(String type, String host, String dbname, String user, String pwd) {
        if (type != null && !type.isEmpty()) {
            try {
                if (type.equalsIgnoreCase(SQLSERVER)) {
                    Class.forName(SQLSERVERDRIVER);
                    dbConnection = DriverManager.getConnection(host + ";database=" + dbname + ";sendStringParametersAsUnicode=true;useUnicode=true;characterEncoding=UTF-8;", user, pwd);
                } else if (type.equalsIgnoreCase(MYSQL)) {
                    Class.forName(MYSQLDRIVER);
                    dbConnection = DriverManager.getConnection(host + "/" + dbname, user, pwd);
                } else if (type.equalsIgnoreCase(POSTGRESQL)) {
                    Class.forName(POSTGRESQLDRIVER);
                    dbConnection = DriverManager.getConnection("jdbc:postgresql://" + host + "/" + dbname + "?ssl=true&sslmode=require&sslfactory=org.postgresql.ssl.NonValidatingFactory", user, pwd);
                }
                return dbConnection;
            } catch (ClassNotFoundException | SQLException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return dbConnection;
    }
    
    public String getUser(int id) {
        try {
            String SQL = "SELECT row_to_json(t) FROM public.user t WHERE t.id = " + id + ";";
            Statement stmt = this.dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            // Iterate through the data in the result set and display it.  
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return null;
    }

    public String addUser(String username, String password, String email, String name) {
        try {        
            String check = checkUser(username);
            if (!check.equals(NOTMATCH)) return "Username existed";
            this.dbConnection.setAutoCommit(false);
            Statement stmt = this.dbConnection.createStatement();
            String SQL = "INSERT INTO public.user(id, username, password, email, status, name) SELECT MAX(t.id) + 1, '" + username 
                        + "', '" + password + "', '" + email + "', 1, '" + name + "' FROM public.user t;";
            stmt.executeUpdate(SQL);
            stmt.close();
            this.dbConnection.commit();
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (this.dbConnection != null) {
                try {
                    this.dbConnection.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return "Success";
    }
      
    public String checkUser(String username) {
        try {
            if (username != null) {
                String SQL = "SELECT 1 FROM public.user u WHERE u.username = '" + username + "';";
                Statement stmt = this.dbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL);

                // Iterate through the data in the result set and display it.  
                if (rs.next()) {
                    return "Exist";
                } else {
                    return NOTMATCH;
                }
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return null;
    }

    private static Timestamp getTimeStampOfDate(Date date) {
        if (date != null) {
            return new Timestamp(date.getTime());
        }
        return null;
    }

    public boolean executeSQL(String sql) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = this.dbConnection.prepareStatement(sql);
            // execute insert SQL stetement
            if (preparedStatement != null) {
                int res = preparedStatement.executeUpdate();
                return res == 1;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqle) {
                    System.err.println(sqle.getMessage());
                }
            }
        }
        return false;
    }

}
