/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_jdbc;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger; 
/**
 *
 * @author inf117256
 */
class Lab_JDBC {
    /**
     * @param args the command line arguments
     */
    private Connection conn = null;
    private Properties connectionProps = null;
    public void finalize(){
        closeConnection();
    }
    public void establishConnection()
    {
        connectionProps = new Properties();
        connectionProps.put("user", "inf117256");
        connectionProps.put("password", "inf117256");
        try {
            conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//admlab2-main.cs.put.poznan.pl:1521/dblab01.cs.put.poznan.pl",
                    connectionProps);
            System.out.println("Połączono z bazą danych");
        } catch (SQLException ex) {
        Logger.getLogger(Lab_JDBC.class.getName()).log(Level.SEVERE,
        "nie udało się połączyć z bazą danych", ex);
        System.exit(-1);        
        }
    }
    public void closeConnection()
    {
        try {
            conn.close();
            System.out.println("Odlaczono od bazy danych");
        } catch (SQLException ex) {
            Logger.getLogger(Lab_JDBC.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Nie udalo sie odlaczyc od bazy danych ???");
        }
    }
    public void findPerson(String name, String surname)
    {
        Statement stmt = null;
        ResultSet rs = null;
        if("".equals(surname)) return;
        try {    
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id_prac, RPAD(nazwisko,10), placa_pod FROM pracownicy WHERE nazwisko='"+surname+"'");
            while (rs.next()) {
                int x = rs.getInt(1);
                String s = rs.getString(2); 
                int y = rs.getInt(3);
                System.out.println(x + " " + s + " " + y);
            }
            
        } catch (SQLException sQLException) {
            System.out.println("Cos sie nie udalo");
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException sQLException) {
                    System.out.println("RS NOT CLOSED");
                }
            }
            if(stmt!=null) {
                try {
                    stmt.close();
                } catch (SQLException sQLException) {
                    System.out.println("STMT NOT CLOSED");
                }
            }
        }
    }

    
}
