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
     * Creates a JdbcMoonMissionRepository backed by the provided DataSource.
     */
    public JdbcMoonMissionRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Fetches all moon missions.
     *
     * @return a {@code List<MoonMission>} containing all missions; empty list if none are found or an error occurs
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
     * Retrieve a moon mission by its mission_id.
     *
     * @param id the mission's primary key (mission_id)
     * @return an Optional containing the MoonMission when found, `Optional.empty()` otherwise
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
     * Count the moon missions whose launch date falls in the specified calendar year.
     *
     * @param year the calendar year to count launches for
     * @return the number of missions launched in the given year; returns 0 if no missions are found or if a database error occurs
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
     * Creates a MoonMission object from the current row of the provided ResultSet.
     *
     * @param rs the ResultSet positioned at the row to map
     * @return a MoonMission populated with values from the current ResultSet row
     * @throws SQLException if an error occurs while reading from the ResultSet
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