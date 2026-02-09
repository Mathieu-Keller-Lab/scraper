package com.scraper;

import com.scraper.entity.NotifiedVacancy;
import com.scraper.repository.NotifiedVacancyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

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
    public boolean shouldNotify(String vacancyId, String vacancyName) {
        // Check database to see if we've notified about this vacancy before
        boolean exists = repository.existsByVacancyId(vacancyId, vacancyName);

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

}


