package au.com.acumen.faces.repository;




import au.com.acumen.faces.model.Candidates;
import au.com.acumen.faces.model.MsCandidateRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsCandidateRelationTableRepository extends JpaRepository<MsCandidateRelation, Long> {

    MsCandidateRelation findByPersonId(String personId);
    MsCandidateRelation findByCandidate(Candidates candidate);


   }
