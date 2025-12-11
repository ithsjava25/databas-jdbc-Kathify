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

    public JdbcMoonMissionRepository(DataSource ds) {
        this.ds = ds;
    }

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
