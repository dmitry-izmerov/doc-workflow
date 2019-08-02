package ru.demi.docworkflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.demi.docworkflow.model.Doc;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DocRepository extends JpaRepository<Doc, Long> {

    Doc findTopByUuidOrderByIdDesc(UUID uuid);

    @Query("select count(d) from Doc d where (d.firstSide.id = :companyId or d.secondSide.id = :companyId) and (d.signedByFirstSide = false or d.signedBySecondSide = false)")
    long countByCompanyId(long companyId);

    @Query("select count(distinct d.uuid) from Doc d where d.firstSide.id = :companyId and d.createDate >= :fromDate and d.createDate <= :toDate")
    long countDocCreationsForPeriod(long companyId, LocalDateTime fromDate, LocalDateTime toDate);

    @Query("select count(distinct d.uuid) from Doc d " +
            "where (d.firstSide.id = :firstCompany and d.secondSide.id = :secondCompany " +
            "or d.secondSide.id = :firstCompany and d.firstSide.id = :secondCompany) " +
            "and (d.signedByFirstSide = false or d.signedBySecondSide = false)")
    long countParticipationBetweenCompanies(long firstCompany, long secondCompany);
}
