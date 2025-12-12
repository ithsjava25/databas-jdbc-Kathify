package com.example.repository.jdbc;

import com.example.MoonMission;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-repository för MoonMission.
 * Ansvarar för att hämta, räkna o mappa månuppdrag från databasen
 */
public class JdbcMoonMissionRepository implements MoonMissionRepository {

    private final DataSource ds;

    /**
     * Skapar repository med given DataSource
     *
     * @param ds datakälla (DataSource) som används för databaskopplingar
     */
    public JdbcMoonMissionRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * hämtar alla månuppdrag från databasen
     *
     * @return lista med alla MoonMission-objekt, tom lista om inga uppdrag finns
     */
    @Override
    public List<MoonMission> findAll() {
        List<MoonMission> missions = new ArrayList<>();
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM moon_mission");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                missions.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return missions;
    }

    /**
     * hämtar ett månuppdrag med angivet ID
     *
     * @param id ID för uppdraget som ska hämtas
     * @return ett Optional med MoonMission om uppdraget finns, annars Optional.empty()
     */
    @Override
    public Optional<MoonMission> findById(long id) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM moon_mission WHERE mission_id = ?")) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * räknar antalet månuppdrag som skedde under ett visst år
     *
     * @param year året som uppdrag ska räknas för
     * @return antal uppdrag under det angivna året
     */
    @Override
    public int countByYear(int year) {
        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM moon_mission WHERE YEAR(launch_date) = ?")) {

            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * skapar ett MoonMission-objekt från en databasrad
     *
     * @param rs ResultSet som pekar på en rad i databasen
     * @return ett MoonMission-objekt som representerar raden
     * @throws SQLException om det uppstår fel vid åtkomst av databasen
     */
    private MoonMission mapRow(ResultSet rs) throws SQLException {
        return new MoonMission(
                rs.getLong("mission_id"),
                rs.getString("spacecraft"),
                rs.getDate("launch_date").toLocalDate(),
                rs.getString("carrier_rocket"),
                rs.getString("operator"),
                rs.getString("mission_type"),
                rs.getString("outcome")
        );
    }
}