package com.archis.bdd;

import java.sql.*;
import java.util.List;

import static com.archis.bdd.BddCrud.connect;

public class BddFirstInit {

    static void firstInitArchimonstres() {
        String sql = "CREATE TABLE IF NOT EXISTS archimonstres (\n"
                + " id INTEGER NOT NULL,\n"
                + " nom TEXT NOT NULL,\n"
                + " slug TEXT NOT NULL, \n"
                + " type TEXT NOT NULL \n"
                + ");";
        try {
            Connection conn = connect();
            if(!isTableExists(conn, "archimonstres")) {
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static void firstInitSettings() {
        String sql = "CREATE TABLE IF NOT EXISTS settings (\n"
                + " nom TEXT PRIMARY KEY,\n"
                + " valeur TEXT NOT NULL\n"
                + ");";

        List<String> listeInsertions = List.of(
                "INSERT INTO settings(nom, valeur) VALUES ('opacite','100')",
                "INSERT INTO settings(nom, valeur) VALUES ('nombrepersonnages','1')",
                "INSERT INTO settings(nom, valeur) VALUES ('apiKey', '')",
                "INSERT INTO settings(nom, valeur) VALUES ('userKey', '')",
                "INSERT INTO settings(nom, valeur) VALUES ('nomPersonnage', '')",
                "INSERT INTO settings(nom, valeur) VALUES ('toucheCapture', 'F1')",
                "INSERT INTO settings(nom, valeur) VALUES ('rectangle', '')",
                "INSERT INTO settings(nom, valeur) VALUES ('useLastRectangle', 'false')");

        try {
            Connection conn = connect();
            if(!isTableExists(conn, "settings")) {
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                listeInsertions.forEach(script -> {
                    try {
                        stmt.execute(script);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    public static void main(String[] args) {
        firstInit();
//        eraseTable();
    }

    public static void firstInit() {
        firstInitSettings();
        firstInitArchimonstres();
    }

    private static boolean isTableExists(Connection conn, String nomTable) {
        boolean exists = false;
        String checkTableExists = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + nomTable + "'";

        try (PreparedStatement pstmt = conn.prepareStatement(checkTableExists)) {
            ResultSet rs = pstmt.executeQuery();
            exists = rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return exists;
    }

    static void eraseTable() {
        String sql = "DROP TABLE IF EXISTS archimonstres";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
