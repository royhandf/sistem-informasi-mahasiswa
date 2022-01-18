/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Koneksi;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author asus
 */
public class koneksi {
    private static Connection koneksi;
    
    public static Connection getKoneksi() throws SQLException {
        if (koneksi == null) {
            try {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                koneksi = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/infomahasiswa","root","");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Koneksi Gagal");
            }
        }
        return koneksi;
    }
    
}
