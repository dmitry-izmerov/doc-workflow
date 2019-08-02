package ru.demi.docworkflow.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demi.docworkflow.DocWorkflowApplication;
import ru.demi.docworkflow.exception.RestrictionViolationException;
import ru.demi.docworkflow.model.Company;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.CompanyRepository;
import ru.demi.docworkflow.repository.DocRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = DocWorkflowApplication.class,
    properties = {
        "app.restriction.timeLimit.enabled=false",
        "app.restriction.maxParticipation.enabled=false",
        "app.restriction.maxDocCreations.enabled=false",
        "app.restriction.maxParticipationBetweenCompanies.value=1"
    }
)
public class DocWorkflowServiceWithMaxParticipationBetweenCompaniesRestrictionTest {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private DocWorkflowService docWorkflowService;

    @Test(expected = RestrictionViolationException.class)
    public void shouldThrowRestrictionException() throws InterruptedException {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);

        Doc doc = new Doc();
        doc.setUuid(UUID.randomUUID());
        doc.setFirstSide(company1);
        doc.setSecondSide(company2);
        doc.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc);

        TimeUnit.SECONDS.sleep(1);
        docWorkflowService.saveDoc(UUID.randomUUID(), company1.getId(), company2.getId());
    }

    @Test
    public void shouldSaveSuccessfully() throws InterruptedException {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);

        Doc doc = new Doc();
        doc.setUuid(UUID.randomUUID());
        doc.setFirstSide(company1);
        doc.setSecondSide(company2);
        doc.setSignedByFirstSide(true);
        doc.setSignedBySecondSide(true);
        docRepository.saveAndFlush(doc);

        TimeUnit.SECONDS.sleep(1);
        docWorkflowService.saveDoc(UUID.randomUUID(), company1.getId(), company2.getId());
    }
}