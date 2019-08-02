package ru.demi.docworkflow.service;

import java.util.UUID;

public interface DocWorkflowService {
    long saveDoc(UUID docUuid, long firstCompany, long secondCompany);

    void removeDoc(long id, long actorCompany);

    void signDoc(UUID docUuid, long actorCompanyId);
}
