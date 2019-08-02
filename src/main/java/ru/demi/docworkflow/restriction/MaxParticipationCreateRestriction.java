package ru.demi.docworkflow.restriction;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.DocRepository;

@ConditionalOnProperty(name = "app.restriction.maxParticipation.enabled", havingValue = "true")
@RequiredArgsConstructor
@Component
public class MaxParticipationCreateRestriction implements CreateRestriction {
    private final DocRepository docRepository;

    @Value("${app.restriction.maxParticipation.value}")
    private int maxParticipation;

    private static final String MESSAGE = "Компаниям запрещено участвовать более чем в %d документооборотах";

    @Override
    public String check(Doc doc) {
        long countByFirstCompany = docRepository.countByCompanyId(doc.getFirstSide().getId());
        if (countByFirstCompany >= maxParticipation) {
            return String.format(MESSAGE, countByFirstCompany);
        }
        long countBySecondCompany = docRepository.countByCompanyId(doc.getSecondSide().getId());
        if (countBySecondCompany >= maxParticipation) {
            return String.format(MESSAGE, countBySecondCompany);
        }
        return null;
    }
}
