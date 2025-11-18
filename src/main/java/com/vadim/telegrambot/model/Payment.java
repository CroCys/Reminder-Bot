package com.vadim.telegrambot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Month;

@Entity
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Month month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private boolean isPayed;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment other)) {
            return false;
        }

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
