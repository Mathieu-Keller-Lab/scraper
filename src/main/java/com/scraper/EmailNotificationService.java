package com.scraper;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import static io.quarkus.arc.ComponentsProvider.LOG;

/**
 * Service responsible for sending email notifications
 */
@ApplicationScoped
@Transactional
public class EmailNotificationService {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "scraper.email.recipient")
    String recipientEmail;

    /**
     * Sends an email notification when a vacancy is found
     */
    public void sendVacancyNotification(VacancyInfo vacancyInfo) {
        try {
            String subject = "Vacancy Alert: " + vacancyInfo.shareHouseName() + " - Apartment Available!";
            String body = buildEmailBody(vacancyInfo);

            LOG.info("Sending email notification to: " + recipientEmail);

            mailer.send(
                    Mail.withText(recipientEmail, subject, body)
            );

            LOG.info("Email sent successfully");

        } catch (Exception e) {
            LOG.error("Failed to send email notification: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the email body content
     */
    private String buildEmailBody(VacancyInfo vacancyInfo) {
        StringBuilder body = new StringBuilder();
        body.append("Great news! A vacancy has been detected on the Oakhouse website.\n\n");
        body.append("Details:\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        body.append("🏠 Share House: ").append(vacancyInfo.shareHouseName()).append("\n");
        body.append("🆔 Vacancy ID: ").append(vacancyInfo.vacancyId()).append("\n");
        body.append("🔗 URL: ").append(vacancyInfo.url()).append("\n");
        body.append("🏘️  Room Type: ").append(vacancyInfo.roomType()).append("\n");
        body.append("✅ Status: ").append(vacancyInfo.status()).append("\n");
        body.append("🕐 Detected at: ").append(vacancyInfo.timestamp()).append("\n");
        body.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        body.append("Click the link above to view the listing and secure your spot!\n\n");
        body.append("This is an automated notification from the Oakhouse Scraper.\n");

        return body.toString();
    }
}

