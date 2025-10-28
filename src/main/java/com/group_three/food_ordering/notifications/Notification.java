package com.group_three.food_ordering.notifications;

import java.time.LocalDateTime;

import com.group_three.food_ordering.models.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

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

    @Column(length = 100)
    private String title;

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false, length = 500)
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
