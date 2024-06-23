package com.archis.bdd;

import com.archis.model.Monstre;
import com.archis.model.Settings;
import com.archis.utils.ZoneEnum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public static int selectAllArchimonstres(int nombrePersonnages) {
        String sql = "SELECT count(*) as total FROM archimonstres";
        int result = 0;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            result = rs.getInt("total");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result * nombrePersonnages;
    }
    public static int selectAllArchimonstresWithNombre(int nombrePersonnages) {
        String sql = "SELECT sum(CASE WHEN nombre <= ? THEN nombre ELSE ? END) as totalNombre FROM archimonstres";
        int result = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nombrePersonnages);
            pstmt.setInt(2, nombrePersonnages);
            ResultSet rs = pstmt.executeQuery();
            result = rs.getInt("totalNombre");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static int selectAllArchimonstresByType(String type, int nombrePersonnages) {
        String sql = "SELECT count(*) as total FROM archimonstres WHERE type = ?";
        int result = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            result = rs.getInt("total");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result * nombrePersonnages;
    }

    public static int selectAllArchimonstresByTypeWithNombre(String type, int nombrePersonnages) {
        String sql = "SELECT sum(CASE WHEN nombre <= ? THEN nombre ELSE ? END) as totalNombre FROM archimonstres WHERE type = ?";
        int result = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nombrePersonnages);
            pstmt.setInt(2, nombrePersonnages);
            pstmt.setString(3, type);
            ResultSet rs = pstmt.executeQuery();
            result = rs.getInt("totalNombre");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static List<Monstre> selectAllMonstresFromZone(String zone) {
        String sql = "SELECT * FROM archimonstres WHERE zone LIKE ?";
        List<Monstre> monstres = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + zone + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                List<ZoneEnum> zoneEnums = getZoneEnums(rs);
                Monstre monstre = Monstre.builder()
                        .id(rs.getInt("id"))
                        .nomMonstre(rs.getString("nom_monstre"))
                        .nomArchimonstre(rs.getString("nom_archimonstre"))
                        .type(rs.getString("type"))
                        .etape(rs.getInt("etape"))
                        .nombre(rs.getInt("nombre"))
                        .zone(zoneEnums)
                        .image(rs.getString("image"))
                        .build();
                boolean isExactZone = monstre.getZone().stream().anyMatch(z -> z.getNom().equals(zone));
                if(isExactZone) {
                    monstres.add(monstre);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monstres;
    }

    private static List<ZoneEnum> getZoneEnums(ResultSet rs) throws SQLException {
        String zoneMonstre = rs.getString("zone");
        List<String> zones = List.of(zoneMonstre.split(","));
        List<ZoneEnum> zoneEnums = new ArrayList<>();
        for (String z : zones) {
            zoneEnums.add(ZoneEnum.getZoneEnum(z));
        }
        return zoneEnums;
    }

    public static void main(String[] args) {
        getSettings();
    }

}
