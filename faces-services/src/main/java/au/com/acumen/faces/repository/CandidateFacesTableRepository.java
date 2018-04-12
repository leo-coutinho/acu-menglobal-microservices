package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.CandidateFaces;
import au.com.acumen.faces.model.Candidates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateFacesTableRepository extends JpaRepository<CandidateFaces, Long> {

    CandidateFaces findByCandidate(Candidates candidate);

}
