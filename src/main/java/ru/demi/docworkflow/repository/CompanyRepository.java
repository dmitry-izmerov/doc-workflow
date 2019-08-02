package ru.demi.docworkflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.demi.docworkflow.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
