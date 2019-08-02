package ru.demi.docworkflow.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Doc extends AbstractPersistable<Long> {

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "first_company_id")
    private Company firstSide;

    @OneToOne
    @JoinColumn(name = "second_company_id")
    private Company secondSide;

    private boolean signedByFirstSide = false;

    private boolean signedBySecondSide = false;

    @Column(updatable = false)
    private LocalDateTime createDate;

    @Transient
    public boolean alreadySigned() {
        return signedByFirstSide && signedBySecondSide;
    }

    @Transient
    public boolean anySigned() {
        return signedByFirstSide || signedBySecondSide;
    }

    @Transient
    public Optional<Company> getCurrentActor() {
        if (alreadySigned()) {
            return Optional.empty();
        }

        if (!signedByFirstSide) {
            return Optional.of(firstSide);
        }
        return Optional.of(secondSide);
    }

    public boolean hasPrevVersion() {
        return uuid != null;
    }
}
