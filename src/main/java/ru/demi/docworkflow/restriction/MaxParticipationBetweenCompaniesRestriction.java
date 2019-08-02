package ru.demi.docworkflow.restriction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.DocRepository;

@ConditionalOnProperty(name = "app.restriction.maxParticipationBetweenCompanies.enabled", havingValue = "true")
@RequiredArgsConstructor
@Component
public class MaxParticipationBetweenCompaniesRestriction implements CreateRestriction {

    private final DocRepository docRepository;

    @Value("${app.restriction.maxParticipationBetweenCompanies.value}")
    private int maxParticipationBetweenCompanies;

    private static final String MESSAGE = "Запрещено ведение более %d документооборотов между 2мя компаниями";

    @Override
    public String check(Doc doc) {
        long count = docRepository.countParticipationBetweenCompanies(doc.getFirstSide().getId(), doc.getSecondSide().getId());

        if (count >= maxParticipationBetweenCompanies) {
            return String.format(MESSAGE, maxParticipationBetweenCompanies);
        }

        return null;
    }
}
