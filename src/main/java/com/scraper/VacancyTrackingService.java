package com.scraper;

import com.scraper.entity.NotifiedVacancy;
import com.scraper.repository.NotifiedVacancyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service to track vacancy IDs that have been seen and notified
 */
@ApplicationScoped
@Transactional
public class VacancyTrackingService {

    private static final Logger LOG = Logger.getLogger(VacancyTrackingService.class);

    @Inject
    NotifiedVacancyRepository repository;

    /**
     * Checks if this vacancy is new and should trigger a notification
     *
     * @param vacancyId The ID of the vacancy
     * @return true if email should be sent, false otherwise
     */
    public boolean shouldNotify(String vacancyId) {
        // Check database to see if we've notified about this vacancy before
        boolean exists = repository.existsByVacancyId(vacancyId);

        if (!exists) {
            LOG.info("New vacancy detected: " + vacancyId);
            return true;
        }

        LOG.debug("Vacancy " + vacancyId + " already notified, skipping");
        return false;
    }

    /**
     * Marks a vacancy as notified by saving it to the database
     *
     * @param vacancyInfo The vacancy information
     */
    public void markAsNotified(VacancyInfo vacancyInfo) {
        NotifiedVacancy vacancy = new NotifiedVacancy(
                vacancyInfo.vacancyId(),
                vacancyInfo.shareHouseName(),
                vacancyInfo.url(),
                vacancyInfo.roomType()
        );
        repository.save(vacancy);
        LOG.info("Marked vacancy " + vacancyInfo.vacancyId() + " as notified in database");
    }

    /**
     * Updates the set of currently active vacancies
     * Should be called at the start of each scrape cycle
     *
     * @param vacancyIds Set of vacancy IDs found in current scrape
     */
    public void updateCurrentVacancies(Set<String> vacancyIds) {
        // Find vacancies that were removed (in old set but not in new set)
        Set<String> removedVacancies = repository.findAll()
                .stream()
                .map(NotifiedVacancy::getVacancyId)
                .collect(Collectors.toSet());
        removedVacancies.removeAll(vacancyIds);

        // Remove deleted vacancies from database so they can trigger again if they reappear
        for (String removedId : removedVacancies) {
            if (repository.deleteByVacancyId(removedId)) {
                LOG.info("Vacancy " + removedId + " was removed from database, will notify again if it reappears");
            }
        }

        long totalNotified = repository.count();
        LOG.debug("Updated current vacancies. Active: " + (vacancyIds.size() - removedVacancies.size()) +
                ", Total notified in DB: " + totalNotified);
    }
}


