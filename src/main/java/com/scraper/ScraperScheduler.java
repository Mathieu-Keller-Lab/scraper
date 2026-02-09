package com.scraper;

import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import static io.quarkus.arc.ComponentsProvider.LOG;

/**
 * Scheduler that runs the scraping job every 15 minutes
 */
@ApplicationScoped
@Transactional
public class ScraperScheduler {

    @Inject
    WebScraperService scraperService;

    /**
     * Scheduled job that runs every 15 minutes
     * Cron expression: every 15 minutes
     */
    @Scheduled(every = "30m")
    public void scheduledScrape() {
        LOG.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        LOG.info("Scheduled scraping job triggered");
        LOG.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        scraperService.scrapeAndNotify();

        LOG.info("Scheduled scraping job completed");
    }
}

