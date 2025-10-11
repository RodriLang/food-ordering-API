package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "participants")
@SQLDelete(sql = "UPDATE participants SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"tableSession", "user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Participant extends BaseEntity {

    @ManyToOne
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
