package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.Candidates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatesTableRepository extends JpaRepository<Candidates, Long> {

    List<Candidates> findByName(String candidateName);
//    Mono<Candidates> findByCandidateId(Long candidateId);
//    Mono<Candidates> findOne();


   }
