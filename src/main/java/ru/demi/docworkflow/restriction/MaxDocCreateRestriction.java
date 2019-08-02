package ru.demi.docworkflow.restriction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.DocRepository;

import java.time.Duration;
import java.time.LocalDateTime;

@ConditionalOnProperty(name = "app.restriction.maxDocCreations.enabled", havingValue = "true")
@RequiredArgsConstructor
@Component
public class MaxDocCreateRestriction implements CreateRestriction {
    private final DocRepository docRepository;

    @Value("${app.restriction.maxDocCreations.value}")
    private int maxDocCreations;

    @Value("${app.restriction.maxDocCreations.limitInMinutes}")
    private int docCreationLimitInMinutes;

    private static final String MESSAGE = "Компании запрещено создавать более %d документов в течении %d минут";

    @Override
    public String check(Doc doc) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minus(Duration.ofMinutes(docCreationLimitInMinutes));
        long count = docRepository.countDocCreationsForPeriod(doc.getFirstSide().getId(), from, to);
        if (count >= maxDocCreations) {
            return String.format(MESSAGE, maxDocCreations, docCreationLimitInMinutes);
        }
        return null;
    }
}
