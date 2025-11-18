package com.vadim.telegrambot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    private String username;
    private String firstName;
    private String lastName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscription_id")
    )
    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }

        // Сравниваем по ID, если оба уже сохранены
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        // Для новых (несохранённых) — сравниваем по telegramId
        return telegramId != null && telegramId.equals(other.telegramId);
    }

    @Override
    public int hashCode() {
        // Если есть id, используем его
        if (id != null) {
            return id.hashCode();
        }
        // Для новых — по telegramId
        return telegramId != null ? telegramId.hashCode() : 0;
    }
}
