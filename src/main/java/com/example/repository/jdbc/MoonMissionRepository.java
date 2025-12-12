package com.example.repository.jdbc;

import com.example.MoonMission;

import java.util.List;
import java.util.Optional;

/**
 * repository interface för att hantera MoonMission-objekt i ett datalager
 */
public interface MoonMissionRepository {

    /**
 * Retrieve all MoonMission records.
 *
 * @return a list of MoonMission objects; an empty list if no records exist
 */
    List<MoonMission> findAll();

    /**
 * Finds a moon mission by its unique identifier.
 *
 * @param id the unique identifier of the moon mission to retrieve
 * @return an Optional containing the MoonMission if found, or an empty Optional otherwise
 */
    Optional<MoonMission> findById(long id);

    /**
 * Counts MoonMission records that occurred in the specified year.
 *
 * @param year the calendar year to count missions for (e.g., 1969)
 * @return the number of MoonMission records that occurred in the specified year
 */
    int countByYear(int year);
}