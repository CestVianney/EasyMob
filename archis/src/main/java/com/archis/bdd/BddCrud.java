package com.archis.bdd;

import com.archis.model.Monstre;
import com.archis.model.Settings;
import com.archis.utils.ZoneEnum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.archis.bdd.BddFirstInit.createTableHistorique;

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
                if (isExactZone) {
                    monstres.add(monstre);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monstres;
    }

    public static void updateHistorique() {

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

    public static List<Monstre> getMonstersStartingWith(String text) {
        String sql = "SELECT * FROM archimonstres WHERE (nom_archimonstre LIKE ?) OR (nom_monstre LIKE ?) LIMIT 10";
        List<Monstre> monsters = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, text + "%");
            pstmt.setString(2, text + "%");
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
                monsters.add(monstre);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monsters;
    }

    public static void addMonster(Monstre monster) {
        String sql = "UPDATE archimonstres SET nombre = nombre + 1 WHERE id = ?";
        String sql2 = "INSERT INTO historique (id, nom_monstre, nom_archimonstre, type, etape, nombre, zone, image, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setInt(1, monster.getId());
            pstmt.setString(2, monster.getNomMonstre());
            pstmt.setString(3, monster.getNomArchimonstre());
            pstmt.setString(4, monster.getType());
            pstmt.setInt(5, monster.getEtape());
            pstmt.setInt(6, monster.getNombre());
            pstmt.setString(7, monster.getZone().toString());
            pstmt.setString(8, monster.getImage());
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //la meme chose mais ne cherche pas par id, je veux juste que l'entite dépassant 8 soit supprimée
        String sql3 = "DELETE FROM historique WHERE date NOT IN (SELECT date FROM historique ORDER BY date DESC LIMIT 8)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql3)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, monster.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Monstre> getHistorique() {
        String sql = "SELECT * FROM historique ORDER BY date DESC";
        List<Monstre> monsters = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
                monsters.add(monstre);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monsters;
    }

    public static void changeColumnType() {
        String createNewTable = "CREATE TABLE IF NOT EXISTS historique_new (\n"
                + " id INTEGER,\n"
                + " nom_monstre TEXT NOT NULL,\n"
                + " nom_archimonstre TEXT, \n"
                + " type TEXT, \n"
                + " etape INTEGER NOT NULL,\n"
                + " nombre TEXT NOT NULL,\n"
                + " zone INTEGER NOT NULL,\n"
                + " image TEXT NOT NULL,\n"
                + " date DATE NOT NULL\n"
                + ");";

        String copyData = "INSERT INTO historique_new (id, nom_monstre, nom_archimonstre, type, etape, nombre, zone, image, date) "
                + "SELECT id, nom_monstre, nom_archimonstre, type, etape, nombre, zone, image, date FROM historique";

        String dropOldTable = "DROP TABLE historique";

        String renameTable = "ALTER TABLE historique_new RENAME TO historique";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createNewTable);
            stmt.execute(copyData);
            stmt.execute(dropOldTable);
            stmt.execute(renameTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
//        changeColumnType();
    }

}
