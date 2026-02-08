package com.scraper.repository;

import com.scraper.entity.NotifiedVacancy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing NotifiedVacancy entities using Hibernate
 */
@ApplicationScoped
public class NotifiedVacancyRepository {

    private static final Logger LOG = Logger.getLogger(NotifiedVacancyRepository.class);

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Find a notified vacancy by its vacancy ID
     */
    public Optional<NotifiedVacancy> findByVacancyId(String vacancyId) {
        try {
            NotifiedVacancy vacancy = entityManager
                    .createQuery("SELECT n FROM NotifiedVacancy n WHERE n.vacancyId = :vacancyId", NotifiedVacancy.class)
                    .setParameter("vacancyId", vacancyId)
                    .getSingleResult();
            return Optional.of(vacancy);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

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
    @Transactional
    public NotifiedVacancy save(NotifiedVacancy vacancy) {
        entityManager.persist(vacancy);
        LOG.info("Saved notified vacancy: " + vacancy.getVacancyId());
        return vacancy;
    }

    /**
     * Update an existing notified vacancy
     */
    @Transactional
    public NotifiedVacancy update(NotifiedVacancy vacancy) {
        NotifiedVacancy updated = entityManager.merge(vacancy);
        LOG.debug("Updated notified vacancy: " + vacancy.getVacancyId());
        return updated;
    }

    /**
     * Delete a notified vacancy by vacancy ID
     */
    @Transactional
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
    public boolean existsByVacancyId(String vacancyId) {
        Long count = entityManager
                .createQuery("SELECT COUNT(n) FROM NotifiedVacancy n WHERE n.vacancyId = :vacancyId", Long.class)
                .setParameter("vacancyId", vacancyId)
                .getSingleResult();
        return count > 0;
    }
}

