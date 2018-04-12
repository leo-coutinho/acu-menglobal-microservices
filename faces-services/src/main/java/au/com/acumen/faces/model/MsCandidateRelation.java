package au.com.acumen.faces.model;


import javax.persistence.*;

@Entity
@Table(name = "msCandidateRelation")
public class MsCandidateRelation {

    @Id
    @Column(name="candidateRelationId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long candidateRelationId;

    @Column(nullable = false)
    private String personId;

    @OneToOne
    @JoinColumn(name="candidateId")
    private Candidates candidate;

    public MsCandidateRelation() {
    }

    public Long getCandidateRelationId() {
        return candidateRelationId;
    }

    public void setCandidateRelationId(Long candidateRelationId) {
        this.candidateRelationId = candidateRelationId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Candidates getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidates candidate) {
        this.candidate = candidate;
    }
}
