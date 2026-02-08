package com.scraper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Service responsible for scraping the Oakhouse website
 */
@ApplicationScoped
@Transactional
public class WebScraperService {

    private static final Logger LOG = Logger.getLogger(WebScraperService.class);

    @ConfigProperty(name = "scraper.target.urls")
    String targetUrls;

    @Inject
    EmailNotificationService emailService;

    @Inject
    VacancyTrackingService trackingService;

    /**
     * Scrapes the target websites and checks for vacancy with "Apartment" room type
     */
    public void scrapeAndNotify() {
        String[] urls = targetUrls.split(",");
        LOG.info("Starting scraping job for " + urls.length + " URL(s)");

        // Track all vacancy IDs found in this scrape cycle
        Set<String> allCurrentVacancyIds = new HashSet<>();

        for (String url : urls) {
            url = url.trim();
            if (url.isEmpty()) {
                continue;
            }

            try {
                LOG.info("Scraping URL: " + url);
                Document document = fetchDocument(url);
                Set<VacancyInfo> vacancies = checkForVacancies(document, url);

                // Collect all vacancy IDs
                for (VacancyInfo vacancy : vacancies) {
                    allCurrentVacancyIds.add(vacancy.vacancyId());

                    // Only send email if this is a new vacancy
                    if (trackingService.shouldNotify(vacancy.vacancyId())) {
                        LOG.info("New vacancy found: " + vacancy);
                        emailService.sendVacancyNotification(vacancy);
                        trackingService.markAsNotified(vacancy);
                    }
                }

                if (vacancies.isEmpty()) {
                    LOG.debug("No vacancy found matching criteria for: " + url);
                } else {
                    LOG.info("Found " + vacancies.size() + " matching vacancy/vacancies for: " + url);
                }

            } catch (Exception e) {
                LOG.error("Error during scraping URL " + url + ": " + e.getMessage(), e);
            }
        }

        // Update tracking service with current vacancies
        trackingService.updateCurrentVacancies(allCurrentVacancyIds);
        LOG.info("Scrape cycle completed. Active vacancies: " + allCurrentVacancyIds.size());
    }

    /**
     * Fetches the HTML document from the target URL
     */
    private Document fetchDocument(String url) throws IOException {
        LOG.debug("Fetching document from: " + url);

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get();

        LOG.debug("Document fetched successfully");
        return doc;
    }

    /**
     * Checks for vacancy elements with data-status="vacancy" and validates room type
     * @return Set of all matching vacancies found on the page
     */
    private Set<VacancyInfo> checkForVacancies(Document document, String url) {
        Set<VacancyInfo> matchingVacancies = new HashSet<>();

        // Extract sharehouse name from h1.p-description__name
        String shareHouseName = extractShareHouseName(document);

        // Find all elements with data-status="vacancy"
        Elements vacancyElements = document.select("[data-status=vacancy]");

        LOG.debug("Found " + vacancyElements.size() + " elements with data-status='vacancy'");

        for (Element vacancyElement : vacancyElements) {
            // Extract the vacancy ID from the id attribute
            String vacancyId = vacancyElement.id();
            if (vacancyId == null || vacancyId.isEmpty()) {
                LOG.warn("Vacancy element found without ID attribute, skipping");
                continue;
            }

            LOG.debug("Checking vacancy element with ID: " + vacancyId);

            // Look for the specific child structure
            Elements roomTypeElements = vacancyElement.select("li.u-pc_only.has-label");

            for (Element roomTypeElement : roomTypeElements) {
                Element strongTag = roomTypeElement.selectFirst("strong");
                Element spanTag = roomTypeElement.selectFirst("span");

                if (strongTag != null && spanTag != null) {
                    String label = strongTag.text().trim();
                    String value = spanTag.text().trim();

                    LOG.debug("Found label: '" + label + "' with value: '" + value + "' for vacancy ID: " + vacancyId);

                    // Check if this is the Room type field with value "Apartment"
                    if ("Room type".equalsIgnoreCase(label) && "Apartment".equalsIgnoreCase(value)) {
                        LOG.info("Match found! Room type 'Apartment' with vacancy status at " + shareHouseName + " (ID: " + vacancyId + ")");

                        String timestamp = LocalDateTime.now()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                        VacancyInfo vacancy = new VacancyInfo(
                                vacancyId.strip(),
                                url.strip(),
                                shareHouseName.strip(),
                                value.strip(),
                                "vacancy",
                                timestamp.strip()
                        );

                        matchingVacancies.add(vacancy);
                        break; // Found the room type for this vacancy, move to next vacancy element
                    }
                }
            }
        }

        return matchingVacancies;
    }

    /**
     * Extracts the sharehouse name from the page
     */
    private String extractShareHouseName(Document document) {
        Element nameElement = document.selectFirst("h1.p-description__name");
        if (nameElement != null) {
            String name = nameElement.text().trim();
            LOG.debug("Extracted sharehouse name: " + name);
            return name;
        }
        LOG.debug("Sharehouse name not found");
        return "Unknown";
    }
}

