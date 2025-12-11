package com.example.repository.jdbc;

import com.example.MoonMission;

import java.util.List;
import java.util.Optional;

public interface MoonMissionRepository {
    /**
 * Retrieve all moon mission records.
 *
 * @return a list of all {@link com.example.MoonMission} objects; an empty list if no records exist
 */
List<MoonMission> findAll();
    /**
 * Retrieve the MoonMission with the specified identifier, if it exists.
 *
 * @param id the numeric identifier of the MoonMission to retrieve
 * @return an Optional containing the MoonMission with the given id if found, or an empty Optional otherwise
 */
Optional<MoonMission> findById(long id);
    /**
 * Count MoonMission records that occurred in the specified year.
 *
 * @param year the calendar year to count missions for
 * @return the number of MoonMission records for the specified year
 */
int countByYear(int year);
}