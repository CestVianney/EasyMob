package com.archis.bdd;

import java.sql.*;

import static com.archis.bdd.BddFirstInit.*;

public class BddCrud {

    static Connection connect() {
        Connection conn = null;
        String url = "jdbc:sqlite:archimonstres.db";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void updateCountMonster(String name, int nombre, String type) {
        String sql = "UPDATE archimonstres SET nombre = ? WHERE nom_monstre = ? and type = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nombre);
            pstmt.setString(2, name);
            pstmt.setString(3, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour sélectionner tous les utilisateurs
    private static void selectAllArchimonstres() {
        String sql = "SELECT count(*) as total FROM archimonstres";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Boucle sur les résultats
            while (rs.next()) {
                System.out.println(rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        createNewTable();
        selectAllArchimonstres();
    }
}
