package com.example.repository.jdbc;

import com.example.MoonMission;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMoonMissionRepository implements MoonMissionRepository {
    private final DataSource ds;

    /**
     * Create a JdbcMoonMissionRepository backed by the provided DataSource.
     */
    public JdbcMoonMissionRepository(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Retrieves all MoonMission records from the data source.
     *
     * @return a List containing all MoonMission instances; an empty list if no records are found or a database error occurs.
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
     * Finds a moon mission by its mission identifier.
     *
     * @param id the mission identifier to look up
     * @return an Optional containing the matching MoonMission if found, `Optional.empty()` otherwise
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
     * Count moon missions launched in the specified year.
     *
     * @param year the calendar year to count missions for
     * @return the number of missions launched in that year; 0 if none are found or a database error occurs
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
     * Create a MoonMission from the current row of the given ResultSet.
     *
     * @param rs ResultSet positioned at the row to map; must contain the columns
     *           "mission_id", "spacecraft", "launch_date", "carrier_rocket",
     *           "operator", "mission_type", and "outcome".
     * @return a MoonMission populated from the ResultSet row
     * @throws SQLException if a database access error occurs or a required column is missing
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