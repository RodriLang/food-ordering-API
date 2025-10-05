package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "participants")
@SQLDelete(sql = "UPDATE participants SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"tableSession", "user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Participant extends BaseEntity {

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "public_id", length = 36, unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(length = 100, nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "table_session_id", referencedColumnName = "id")
    private TableSession tableSession;

    @Enumerated(EnumType.STRING)
    private RoleType role;

}
