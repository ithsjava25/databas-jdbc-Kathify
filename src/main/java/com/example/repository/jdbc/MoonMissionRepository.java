package com.example.repository.jdbc;

import com.example.MoonMission;

import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {
    List<MoonMission> findAll();
    Optional<MoonMission> findById(long id);
    int countByYear(int year);
}
