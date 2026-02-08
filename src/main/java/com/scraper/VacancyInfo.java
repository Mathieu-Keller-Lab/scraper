package com.scraper;

/**
 * Data Transfer Object for vacancy information
 */
public record VacancyInfo(
        String vacancyId,
        String url,
        String shareHouseName,
        String roomType,
        String status,
        String timestamp
) {
}

