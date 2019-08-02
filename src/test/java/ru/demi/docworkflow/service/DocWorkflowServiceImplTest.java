package ru.demi.docworkflow.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.demi.docworkflow.DocWorkflowApplication;
import ru.demi.docworkflow.model.Company;
import ru.demi.docworkflow.model.Doc;
import ru.demi.docworkflow.repository.CompanyRepository;
import ru.demi.docworkflow.repository.DocRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = DocWorkflowApplication.class
)
public class DocWorkflowServiceImplTest {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private DocWorkflowService docWorkflowService;

    @Test
    public void shouldSaveDoc() {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);

        UUID uuid = UUID.randomUUID();
        Long id = docWorkflowService.saveDoc(uuid, company1.getId(), company2.getId());

        assertNotNull(id);
    }

    @Test
    public void shouldRemoveDoc() {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);
        Doc doc = new Doc();
        UUID uuid = UUID.randomUUID();
        doc.setUuid(uuid);
        doc.setFirstSide(company1);
        doc.setSecondSide(company2);
        doc.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc);

        docWorkflowService.removeDoc(doc.getId(), company1.getId());

        assertNull(docRepository.findTopByUuidOrderByIdDesc(uuid));
    }

    @Test
    public void shouldSignDoc() {
        Company company1 = new Company();
        Company company2 = new Company();
        companyRepository.saveAndFlush(company1);
        companyRepository.saveAndFlush(company2);
        Doc doc = new Doc();
        UUID uuid = UUID.randomUUID();
        doc.setUuid(uuid);
        doc.setFirstSide(company1);
        doc.setSecondSide(company2);
        doc.setCreateDate(LocalDateTime.now());
        docRepository.saveAndFlush(doc);

        docWorkflowService.signDoc(uuid, company1.getId());
        docRepository.flush();

        assertTrue(docRepository.findTopByUuidOrderByIdDesc(uuid).isSignedByFirstSide());
    }
}