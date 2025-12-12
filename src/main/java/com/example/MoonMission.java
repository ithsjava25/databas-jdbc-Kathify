package com.example;

import java.time.LocalDate;

/**
 * Representerar ett månuppdrag med grundläggande info
 *
 * Innehåller detaljer såsom identifierare, rymdfarkost,
 * uppskjutningsdatum, bärraket, operatör, typ av uppdrag och resultat
 *
 * @param missionId Unik identifierare för uppdraget
 * @param spacecraft Namn på rymdfarkosten som skickades
 * @param launchDate Datum då uppdraget sköts upp
 * @param carrierRocket Namn på bärraketen som användes
 * @param operator Organisation som ansvarar för uppdraget
 * @param missionType Typ av uppdrag (t.ex. bemannat, obemannat)
 * @param outcome Resultatet av uppdraget (t.ex. framgång, misslyckande)
 */
public record MoonMission(
        long missionId,
        String spacecraft,
        LocalDate launchDate,
        String carrierRocket,
        String operator,
        String missionType,
        String outcome
) {}