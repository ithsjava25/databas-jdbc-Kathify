package com.example.repository.jdbc;

import com.example.MoonMission;
import java.util.List;
import java.util.Optional;

/**
 * rep-interface för att hantera MoonMission-objekt i ett datalager
 * <p>
 * definierar metoder för att hämta, söka och räkna månuppdrag
 */
public interface MoonMissionRepository {

    /**
     * hämtar alla månuppdrag
     *
     * @return en lista med alla MoonMission-objekt, tom lista om inga uppdrag finns
     */
    List<MoonMission> findAll();

    /**
     * hämtar ett månuppdrag baserat på dess ID
     *
     * @param id ID för det uppdrag som ska hämtas
     * @return ett Optional med MoonMission om uppdraget finns, annars Optional.empty()
     */
    Optional<MoonMission> findById(long id);

    /**
     * räknar hur många månuppdrag som inträffade under ett visst år
     *
     * @param year året som uppdrag ska räknas för
     * @return antal uppdrag under det angivna året
     */
    int countByYear(int year);
}