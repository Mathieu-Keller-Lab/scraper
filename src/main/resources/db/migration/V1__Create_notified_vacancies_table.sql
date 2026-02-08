-- Flyway Migration V1: Create notified_vacancies table
-- This script creates the initial database schema for tracking notified vacancies

-- Create the notified_vacancies table
CREATE TABLE notified_vacancies (
    id BIGSERIAL PRIMARY KEY,
    vacancy_id VARCHAR(255) NOT NULL UNIQUE,
    share_house_name VARCHAR(255),
    url VARCHAR(1000),
    room_type VARCHAR(255),
    first_notified_at TIMESTAMP NOT NULL,
    last_seen_at TIMESTAMP NOT NULL,
    notification_count INTEGER NOT NULL DEFAULT 1
);

-- Create indexes for better query performance
CREATE INDEX idx_notified_vacancies_vacancy_id
    ON notified_vacancies(vacancy_id);

CREATE INDEX idx_notified_vacancies_last_seen_at
    ON notified_vacancies(last_seen_at DESC);

CREATE INDEX idx_notified_vacancies_share_house_name
    ON notified_vacancies(share_house_name);

-- Add comments for documentation
COMMENT ON TABLE notified_vacancies IS 'Tracks vacancies that have been notified via email';
COMMENT ON COLUMN notified_vacancies.vacancy_id IS 'Unique vacancy ID from the Oakhouse website';
COMMENT ON COLUMN notified_vacancies.share_house_name IS 'Name of the share house';
COMMENT ON COLUMN notified_vacancies.url IS 'URL of the property page';
COMMENT ON COLUMN notified_vacancies.room_type IS 'Type of room (e.g., Apartment)';
COMMENT ON COLUMN notified_vacancies.first_notified_at IS 'Timestamp when first notification was sent';
COMMENT ON COLUMN notified_vacancies.last_seen_at IS 'Timestamp when vacancy was last seen on website';
COMMENT ON COLUMN notified_vacancies.notification_count IS 'Number of times this vacancy has been notified';

