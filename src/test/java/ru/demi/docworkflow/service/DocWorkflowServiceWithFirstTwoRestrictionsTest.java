package ru.demi.docworkflow.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demi.docworkflow.DocWorkflowApplication;
import ru.demi.docworkflow.exception.RestrictionViolationException;
import ru.demi.docworkflow.model.Company;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.CompanyRepository;
import ru.demi.docworkflow.repository.DocRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = DocWorkflowApplication.class,
    properties = {
        "app.restriction.maxParticipation.value=2",
        "app.restriction.maxDocCreations.enabled=false",
        "app.restriction.maxParticipationBetweenCompanies.enabled=false"
    }
)
public class DocWorkflowServiceWithFirstTwoRestrictionsTest {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private DocWorkflowService docWorkflowService;
    @SpyBean
    private DateProvider dateProvider;

    @Test(expected = RestrictionViolationException.class)
    public void shouldThrowRestrictionExceptionForTimeLimit() {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);

        UUID uuid = UUID.randomUUID();
        Doc doc = new Doc();
        doc.setUuid(uuid);
        doc.setFirstSide(company1);
        doc.setSecondSide(company2);
        doc.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc);
        LocalDateTime time = LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0));

        Mockito.when(dateProvider.getNow()).thenReturn(time);

        docWorkflowService.saveDoc(uuid, company1.getId(), company2.getId());
    }

    @Test(expected = RestrictionViolationException.class)
    public void shouldThrowRestrictionExceptionForMaxParticipation() {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);

        Doc doc1 = new Doc();
        doc1.setUuid(UUID.randomUUID());
        doc1.setFirstSide(company1);
        doc1.setSecondSide(company2);
        doc1.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc1);
        Doc doc2 = new Doc();
        doc2.setUuid(UUID.randomUUID());
        doc2.setFirstSide(company1);
        doc2.setSecondSide(company2);
        doc2.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc2);

        docWorkflowService.saveDoc(UUID.randomUUID(), company1.getId(), company2.getId());
    }
}