package ru.demi.docworkflow.restriction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.service.DateProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.restriction.timeLimit.enabled", havingValue = "true")
@Component
public class TimeLimitUpdateRestriction implements UpdateRestriction {
    private final DateProvider dateProvider;

    @Value("${app.restriction.timeLimit.fromDate}")
    private String fromDateStr;

    @Value("${app.restriction.timeLimit.toDate}")
    private String toDateStr;

    private static final String MESSAGE = "Запрещено подписывать или вносить изменения в документ с %s до %s";

    @Override
    public String check(Doc doc) {
        LocalDate localDate = LocalDate.now();
        LocalDateTime fromDate = LocalDateTime.of(localDate, LocalTime.parse(fromDateStr)).minusDays(1);
        LocalDateTime toDate = LocalDateTime.of(localDate, LocalTime.parse(toDateStr));
        LocalDateTime now = dateProvider.getNow();
        if (now.isAfter(fromDate) && now.isBefore(toDate)) {
            return String.format(MESSAGE, fromDateStr, toDateStr);
        }
        return null;
    }
}
