package com.scraper.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a notified vacancy in the database
 */
@Entity
@Table(name = "notified_vacancies")
public class NotifiedVacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vacancy_id", nullable = false, unique = true)
    private String vacancyId;

    @Column(name = "share_house_name")
    private String shareHouseName;

    @Column(name = "url")
    private String url;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "first_notified_at", nullable = false)
    private LocalDateTime firstNotifiedAt;

    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    @Column(name = "notification_count", nullable = false)
    private Integer notificationCount = 1;

    // Default constructor required by JPA
    public NotifiedVacancy() {
    }

    public NotifiedVacancy(String vacancyId, String shareHouseName, String url, String roomType) {
        this.vacancyId = vacancyId;
        this.shareHouseName = shareHouseName;
        this.url = url;
        this.roomType = roomType;
        this.firstNotifiedAt = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
        this.notificationCount = 1;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(String vacancyId) {
        this.vacancyId = vacancyId;
    }

    public String getShareHouseName() {
        return shareHouseName;
    }

    public void setShareHouseName(String shareHouseName) {
        this.shareHouseName = shareHouseName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public LocalDateTime getFirstNotifiedAt() {
        return firstNotifiedAt;
    }

    public void setFirstNotifiedAt(LocalDateTime firstNotifiedAt) {
        this.firstNotifiedAt = firstNotifiedAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(LocalDateTime lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public void updateLastSeen() {
        this.lastSeenAt = LocalDateTime.now();
    }

    public void incrementNotificationCount() {
        this.notificationCount++;
    }
}

