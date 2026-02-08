package com.scraper.repository;

import com.scraper.entity.NotifiedVacancy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;

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
     * Find all notified vacancies
     */
    public List<NotifiedVacancy> findAll() {
        return entityManager
                .createQuery("SELECT n FROM NotifiedVacancy n ORDER BY n.lastSeenAt DESC", NotifiedVacancy.class)
                .getResultList();
    }

    /**
     * Save a new notified vacancy
     */
    public void save(NotifiedVacancy vacancy) {
        entityManager.persist(vacancy);
        LOG.info("Saved notified vacancy: " + vacancy.getVacancyId());
    }

    /**
     * Delete a notified vacancy by vacancy ID
     */
    public boolean deleteByVacancyId(String vacancyId) {
        int deleted = entityManager
                .createQuery("DELETE FROM NotifiedVacancy n WHERE n.vacancyId = :vacancyId")
                .setParameter("vacancyId", vacancyId)
                .executeUpdate();

        if (deleted > 0) {
            LOG.info("Deleted notified vacancy: " + vacancyId);
            return true;
        }
        return false;
    }

    /**
     * Count all notified vacancies
     */
    public long count() {
        return entityManager
                .createQuery("SELECT COUNT(n) FROM NotifiedVacancy n", Long.class)
                .getSingleResult();
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

