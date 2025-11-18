package com.vadim.telegrambot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uniqueCode;
    private String name;
    private int price;
    private int dayOfMonth;

    @ManyToMany(mappedBy = "subscriptions", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Payment> payments = new HashSet<>();

    public void setDayOfMonth(int dayOfMonth) {
        int year = Year.now().getValue();
        Month month = LocalDate.now().getMonth();
        int daysInMonth = YearMonth.of(year, month).lengthOfMonth();

        if (dayOfMonth < 1 || dayOfMonth > daysInMonth) {
            throw new RuntimeException("""
                    Invalid dayOfMonth: %d for subscription: %s. It should be between 1 and %d.
                    """.formatted(dayOfMonth, name, daysInMonth));
        } else {
            this.dayOfMonth = dayOfMonth;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subscription other)) {
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
