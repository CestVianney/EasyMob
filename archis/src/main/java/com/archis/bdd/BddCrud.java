package com.archis.bdd;

import com.archis.model.Monstre;
import com.archis.model.MonstreMetamobRecense;
import com.archis.model.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BddCrud {

    static Connection connect() {
        Connection conn = null;
        String url = "jdbc:sqlite:easymetamob.db";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static List<Settings> getSettings() {
        String sql = "SELECT nom, valeur FROM settings";
        List<Settings> settings = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Settings setting = Settings.builder()
                        .nom(rs.getString("nom"))
                        .valeur(rs.getString("valeur"))
                        .build();
                settings.add(setting);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return settings;
    }

    public static void updateSettings(String nom, String valeur) {
        String sql = "UPDATE settings SET valeur = ? WHERE nom = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, valeur);
            pstmt.setString(2, nom);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static String getApiKey() {
        String sql = "SELECT valeur FROM settings WHERE nom = 'apiKey'";
        String apiKey = "";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            apiKey = rs.getString("valeur");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return apiKey;
    }

    static String getUserKey() {
        String sql = "SELECT valeur FROM settings WHERE nom = 'userKey'";
        String userKey = "";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            userKey = rs.getString("valeur");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userKey;
    }

    static String getNomPersonnage() {
        String sql = "SELECT valeur FROM settings WHERE nom = 'nomPersonnage'";
        String nomPersonnage = "";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            nomPersonnage = rs.getString("valeur");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return nomPersonnage;
    }

    public static String getDimensionRectangle() {
        String sql = "SELECT valeur FROM settings WHERE nom = 'rectangle'";
        String rectangle = "";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rectangle = rs.getString("valeur");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rectangle;
    }

    public static boolean isDatabaseSetWithData() {
        String sql = "SELECT count(*) as total FROM archimonstres";
        ResultSet rs;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            rs = stmt.executeQuery(sql);
            if (rs.getInt("total") > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static Monstre getMonstreByName(String nomMonstre) {
        String sql = "SELECT * FROM archimonstres WHERE nom = ?";
        Monstre monstre = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomMonstre);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                monstre = Monstre.builder()
                        .id(rs.getInt("id"))
                        .nom(rs.getString("nom"))
                        .slug(rs.getString("slug"))
                        .type(rs.getString("type"))
                        .build();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monstre;
    }

    public static boolean checkMonstreExists(String nomMonstre) {
        String sql = "SELECT count(*) as total FROM archimonstres WHERE LOWER(TRIM(nom)) = LOWER(TRIM(?))";        ResultSet rs;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomMonstre);
            rs = pstmt.executeQuery();
            if (rs.getInt("total") > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void addMonstre(MonstreMetamobRecense monstre) {
        String sql = "INSERT INTO archimonstres(id, nom, slug, type) VALUES(?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(monstre.getId()));
            pstmt.setString(2, monstre.getNom());
            pstmt.setString(3, monstre.getSlug());
            pstmt.setString(4, monstre.getType());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteAllMonstres() {
        String sql = "DELETE FROM archimonstres";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int countMonstres() {
        String sql = "SELECT count(*) as total FROM archimonstres";
        ResultSet rs;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            rs = stmt.executeQuery(sql);
            return rs.getInt("total");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static Monstre getMonstreById(String id) {
        id = "Fantôme Égérie";
        String sql = "SELECT * FROM archimonstres WHERE nom = ?";
        Monstre monstre = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            monstre = Monstre.builder()
                    .id(rs.getInt("id"))
                    .nom(rs.getString("nom"))
                    .slug(rs.getString("slug"))
                    .type(rs.getString("type"))
                    .build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monstre;
    }

    public static void main(String[] args) {
        System.out.println(checkMonstreExists("Gardienne des Égouts"));
//        System.out.println(getMonstreNameById("205"));
    }
}
