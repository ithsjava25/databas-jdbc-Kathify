package com.example.repository.jdbc;

import com.example.MoonMission;

import java.util.List;
import java.util.Optional;

/**
 * repository interface för att hantera MoonMission-objekt i ett datalager
 */
public interface MoonMissionRepository {

    /**
     * hämtar alla månuppdrag
     */
    List<MoonMission> findAll();

    /**
     *hämtar ett månuppdrag baserat på dess ID
     */
    Optional<MoonMission> findById(long id);

    /**
     * räknar hur många månuppdrag som inträffade under ett visst år
     */
    int countByYear(int year);
}
