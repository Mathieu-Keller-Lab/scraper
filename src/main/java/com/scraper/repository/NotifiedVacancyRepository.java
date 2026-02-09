package com.scraper.repository;

import com.scraper.entity.NotifiedVacancy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Repository for managing NotifiedVacancy entities using Hibernate
 */
@ApplicationScoped
@Transactional
public class NotifiedVacancyRepository {

    private static final Logger LOG = Logger.getLogger(NotifiedVacancyRepository.class);

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Save a new notified vacancy
     */
    public void save(NotifiedVacancy vacancy) {
        entityManager.persist(vacancy);
        LOG.info("Saved notified vacancy: " + vacancy.getVacancyId());
    }


    /**
     * Check if a vacancy ID exists
     */
    public boolean existsByVacancyId(String vacancyId, String vacancyName) {
        Long count = entityManager
                .createQuery("SELECT COUNT(n) FROM NotifiedVacancy n WHERE n.vacancyId = :vacancyId and n.shareHouseName = :vacancyName", Long.class)
                .setParameter("vacancyName", vacancyName)
                .setParameter("vacancyId", vacancyId)
                .getSingleResult();
        return count > 0;
    }
}

