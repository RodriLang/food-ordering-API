package com.group_three.food_ordering.notifications.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.notifications.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "notifications")
@SQLDelete(sql = "UPDATE notifications SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "public_id", length = 36, unique = true, nullable = false, updatable = false)
    private UUID publicId;

    @Column(length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private boolean unread = true;

    @Column
    private LocalDateTime readDateTime;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User targetUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column
    private String linkUrl;

    @Column
    private Boolean deleted;

}
