package ru.demi.docworkflow.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demi.docworkflow.exception.IllegalActionException;
import ru.demi.docworkflow.exception.PermissionDeniedException;
import ru.demi.docworkflow.model.Company;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.CompanyRepository;
import ru.demi.docworkflow.repository.DocRepository;
import ru.demi.docworkflow.restriction.CreateRestriction;
import ru.demi.docworkflow.restriction.Restriction;
import ru.demi.docworkflow.restriction.UpdateRestriction;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DocWorkflowServiceImpl implements DocWorkflowService {

    private final DocRepository docRepository;
    private final CompanyRepository companyRepository;

    @Autowired(required = false)
    private List<CreateRestriction> createRestrictions;
    @Autowired(required = false)
    private List<UpdateRestriction> updateRestrictions;

    @Override
    public long saveDoc(UUID docUuid, long firstCompany, long secondCompany) {
        Doc prevDoc = docRepository.findTopByUuidOrderByIdDesc(docUuid);

        boolean hasPrev = Objects.nonNull(prevDoc);
        if (hasPrev) {
            checkCompany(prevDoc, firstCompany);
            checkCompany(prevDoc, secondCompany);
            checkPossibilityToUpdate(prevDoc, firstCompany);
            Restriction.check(prevDoc, updateRestrictions);
        }

        Doc newDoc = new Doc();
        newDoc.setUuid(UUID.randomUUID());
        newDoc.setFirstSide(companyRepository.getOne(firstCompany));
        newDoc.setSecondSide(companyRepository.getOne(secondCompany));
        newDoc.setCreateDate(LocalDateTime.now());

        if (!hasPrev) {
            Restriction.check(newDoc, createRestrictions);
        }

        Doc saved = docRepository.save(newDoc);
        return saved.getId();
    }

    @Override
    public void removeDoc(long docId, long actorCompanyId) {
        Doc doc = findById(docId);
        if (actorCompanyId != doc.getFirstSide().getId()) {
            throw new PermissionDeniedException("Cannot remove document.");
        }

        if (doc.anySigned()) {
            throw new IllegalActionException("Cannot sign document in process of signing.");
        }
        docRepository.delete(doc);
    }

    @Override
    public void signDoc(UUID docUuid, long actorCompanyId) {
        Doc lastDoc = docRepository.findTopByUuidOrderByIdDesc(docUuid);

        checkCompany(lastDoc, actorCompanyId);
        checkPossibilityToUpdate(lastDoc, actorCompanyId);
        Restriction.check(lastDoc, updateRestrictions);

        if (actorCompanyId == lastDoc.getFirstSide().getId()) {
            lastDoc.setSignedByFirstSide(true);
        } else {
            lastDoc.setSignedBySecondSide(true);
        }
        docRepository.save(lastDoc);
    }

    private Doc findById(long docId) {
        return docRepository.findById(docId).orElseThrow(EntityNotFoundException::new);
    }

    private void checkPossibilityToUpdate(Doc doc, long actorCompanyId) {
        if (doc.alreadySigned()) {
            throw new IllegalActionException("Cannot update already signed document.");
        }

        Optional<Company> currentActor = doc.getCurrentActor();
        if (currentActor.isPresent() && actorCompanyId != currentActor.get().getId()) {
            throw new PermissionDeniedException("Cannot update document.");
        }
    }

    private void checkCompany(Doc doc, long actorCompanyId) {
        if (doc.getFirstSide().getId() != actorCompanyId && doc.getSecondSide().getId() != actorCompanyId) {
            throw new PermissionDeniedException("Current company doesn't participate in workflow of the document.");
        }
    }
}
