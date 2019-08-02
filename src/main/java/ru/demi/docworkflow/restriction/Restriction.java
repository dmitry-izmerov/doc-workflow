package ru.demi.docworkflow.restriction;

import ru.demi.docworkflow.exception.RestrictionViolationException;
import ru.demi.docworkflow.model.Doc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface Restriction {
    String check(Doc doc);

    static void check(Doc doc, List<? extends Restriction> restrictions) {
        List<String> messages = restrictions.stream()
                .map(item -> item.check(doc))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!messages.isEmpty()) {
            throw new RestrictionViolationException(messages);
        }
    }
}
