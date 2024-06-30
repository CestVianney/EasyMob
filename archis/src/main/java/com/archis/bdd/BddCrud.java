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

    private static void updateCountMonsterSync(int id, int quantite, int propose, int recherche) {
        String sql = "UPDATE archimonstres SET quantite = ?, propose = ?, recherche = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantite);
            pstmt.setInt(2, propose);
            pstmt.setInt(3, recherche);
            pstmt.setInt(4, id);
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

    static boolean isMetamobActive() {
        String sql = "SELECT valeur FROM settings WHERE nom = 'activerMetamob'";
        boolean activerMetamob = false;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            activerMetamob = rs.getString("valeur").equals("true");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return activerMetamob;
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
        String sql = "SELECT sum(CASE WHEN quantite <= ? THEN quantite ELSE ? END) as totalNombre FROM archimonstres";
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
        String sql = "SELECT sum(CASE WHEN quantite <= ? THEN quantite ELSE ? END) as totalNombre FROM archimonstres WHERE type = ?";
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
                        .nom(rs.getString("nom"))
                        .slug(rs.getString("slug"))
                        .type(rs.getString("type"))
                        .etape(rs.getInt("etape"))
                        .quantite(rs.getInt("nombre"))
                        .recherche(rs.getInt("recherche"))
                        .propose(rs.getInt("propose"))
                        .zone(zoneEnums)
                        .image(rs.getString("image_url"))
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

    private static List<ZoneEnum> getZoneEnums(ResultSet rs) throws SQLException {
        String zoneMonstre = rs.getString("souszone");
        if(zoneMonstre == null) {
            return List.of();
        }
        List<String> zones = List.of(zoneMonstre.split(","));
        List<ZoneEnum> zoneEnums = new ArrayList<>();
        for (String z : zones) {
            zoneEnums.add(ZoneEnum.getZoneEnum(z));
        }
        return zoneEnums;
    }

    public static List<Monstre> getMonstersStartingWith(String text) {
        String sql = "SELECT * FROM archimonstres WHERE nom LIKE ? LIMIT 9";
        List<Monstre> monsters = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + text + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                List<ZoneEnum> zoneEnums = getZoneEnums(rs);
                Monstre monstre = Monstre.builder()
                        .id(rs.getInt("id"))
                        .nom(rs.getString("nom"))
                        .slug(rs.getString("slug"))
                        .type(rs.getString("type"))
                        .etape(rs.getInt("etape"))
                        .quantite(rs.getInt("quantite"))
                        .recherche(rs.getInt("recherche"))
                        .propose(rs.getInt("propose"))
                        .zone(zoneEnums)
                        .image(rs.getString("image_url"))
                        .build();
                monsters.add(monstre);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monsters;
    }

    public static List<Monstre> getHistorique() {
        String sql = "SELECT * FROM historique ORDER BY date DESC";
        List<Monstre> monsters = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Monstre monstre = Monstre.builder()
                        .id(rs.getInt("id"))
                        .nom(rs.getString("nom"))
                        .slug(rs.getString("slug"))
                        .type(rs.getString("type"))
                        .etape(rs.getInt("etape"))
                        .image(rs.getString("image_url"))
                        .build();
                monsters.add(monstre);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monsters;
    }

    public static Monstre getMonstreByName(String nomMonstre) {
        String sql = "SELECT * FROM archimonstres WHERE lower(nom) = ?";
        Monstre monstre = null;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomMonstre.toLowerCase());
            ResultSet rs = pstmt.executeQuery();
            monstre = Monstre.builder()
                    .id(rs.getInt("id"))
                    .nom(rs.getString("nom"))
                    .slug(rs.getString("slug"))
                    .type(rs.getString("type"))
                    .etape(rs.getInt("etape"))
                    .quantite(rs.getInt("quantite"))
                    .recherche(rs.getInt("recherche"))
                    .propose(rs.getInt("propose"))
                    .image(rs.getString("image_url"))
                    .build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return monstre;
    }

    public static boolean checkMonstreExists(String monstre) {
        //check if monstre exists in the database and return true if it does
        String sql = "SELECT count(*) as total FROM archimonstres WHERE lower(nom) = ?";
        ResultSet rs;
        //if sql false, check sql2
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, monstre.toLowerCase());
            rs = pstmt.executeQuery();
            if (rs.getInt("total") > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void addMonster(Monstre monster) {
        String sql = "UPDATE archimonstres SET quantite = quantite + 1 WHERE id = ?";
        String sql2 = "INSERT INTO historique (id, nom, slug, type, etape, image_url, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sql3 = "DELETE FROM historique WHERE date NOT IN (SELECT date FROM historique ORDER BY date DESC LIMIT 8)";

        try (Connection conn = connect();
             PreparedStatement pstmt1 = conn.prepareStatement(sql2);
             PreparedStatement pstmt2 = conn.prepareStatement(sql3);
             PreparedStatement pstmt3 = conn.prepareStatement(sql)) {

            pstmt1.setInt(1, monster.getId());
            pstmt1.setString(2, monster.getNom());
            pstmt1.setString(3, monster.getSlug());
            pstmt1.setString(4, monster.getType());
            pstmt1.setInt(5, monster.getEtape());
            pstmt1.setString(6, monster.getImage());
            pstmt1.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            pstmt1.executeUpdate();

            pstmt2.executeUpdate();

            pstmt3.setInt(1, monster.getId());
            pstmt3.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void removeOneMonster(Monstre monster) {
        String sql = "UPDATE archimonstres SET quantite = quantite - 1 WHERE id = ?";
        String sql2 = "DELETE FROM historique WHERE rowid = (SELECT rowid FROM historique WHERE id = ? ORDER BY date DESC LIMIT 1)";

        try (Connection conn = connect();
             PreparedStatement pstmt1 = conn.prepareStatement(sql);
             PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {

            pstmt1.setInt(1, monster.getId());
            pstmt1.executeUpdate();

            pstmt2.setInt(1, monster.getId());
            pstmt2.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void syncWithMetamob() {
        MetamobCrud metamobCrud = new MetamobCrud();
        List<Monstre> monstres = metamobCrud.getMonstresFromMetamob();
        monstres.forEach(monstre -> {
                updateCountMonsterSync(monstre.getId(), monstre.getQuantite(), monstre.getPropose(), monstre.getRecherche());
        });
    }

    public static void main(String[] args) {
        syncWithMetamob();
    }

}
