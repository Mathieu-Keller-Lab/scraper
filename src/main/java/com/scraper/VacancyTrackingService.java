package com.scraper;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to track vacancy IDs that have been seen and notified
 */
@ApplicationScoped
public class VacancyTrackingService {

    private static final Logger LOG = Logger.getLogger(VacancyTrackingService.class);

    // Stores vacancy IDs that are currently active (seen in the last scrape)
    private final Set<String> currentVacancies = ConcurrentHashMap.newKeySet();

    // Stores vacancy IDs that have been notified
    private final Set<String> notifiedVacancies = ConcurrentHashMap.newKeySet();

    /**
     * Checks if this vacancy is new and should trigger a notification
     * @param vacancyId The ID of the vacancy
     * @return true if email should be sent, false otherwise
     */
    public boolean shouldNotify(String vacancyId) {
        // If we haven't notified about this vacancy before, we should notify
        if (!notifiedVacancies.contains(vacancyId)) {
            LOG.info("New vacancy detected: " + vacancyId);
            return true;
        }

        LOG.debug("Vacancy " + vacancyId + " already notified, skipping");
        return false;
    }

    /**
     * Marks a vacancy as notified
     * @param vacancyId The ID of the vacancy
     */
    public void markAsNotified(String vacancyId) {
        notifiedVacancies.add(vacancyId);
        LOG.debug("Marked vacancy " + vacancyId + " as notified");
    }

    /**
     * Updates the set of currently active vacancies
     * Should be called at the start of each scrape cycle
     * @param vacancyIds Set of vacancy IDs found in current scrape
     */
    public void updateCurrentVacancies(Set<String> vacancyIds) {
        // Find vacancies that were removed (in old set but not in new set)
        Set<String> removedVacancies = new HashSet<>(currentVacancies);
        removedVacancies.removeAll(vacancyIds);

        // Remove deleted vacancies from notified set so they can trigger again if they reappear
        for (String removedId : removedVacancies) {
            if (notifiedVacancies.remove(removedId)) {
                LOG.info("Vacancy " + removedId + " was removed, will notify again if it reappears");
            }
        }

        // Update current vacancies
        currentVacancies.clear();
        currentVacancies.addAll(vacancyIds);

        LOG.debug("Updated current vacancies. Active: " + currentVacancies.size() +
                  ", Total notified: " + notifiedVacancies.size());
    }

    /**
     * Gets the count of currently tracked notified vacancies
     */
    public int getNotifiedCount() {
        return notifiedVacancies.size();
    }

    /**
     * Gets the count of currently active vacancies
     */
    public int getCurrentCount() {
        return currentVacancies.size();
    }
}


