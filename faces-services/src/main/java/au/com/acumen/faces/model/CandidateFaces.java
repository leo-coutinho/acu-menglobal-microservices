package au.com.acumen.faces.model;

import javax.persistence.*;

@Entity
@Table(name = "candidateFaces")
public class CandidateFaces {
    @Id
    @Column(name="candidateFaceId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long candidateFaceId;

    @ManyToOne
    @JoinColumn(name="candidateId")
    private Candidates candidate;

    @Lob
    @Column(name="candidateImg", nullable=false, columnDefinition="mediumblob")
    private byte[] candidateImg;



    public CandidateFaces() {
    }

    public Long getCandidateFaceId() {
        return candidateFaceId;
    }

    public void setCandidateFaceId(Long candidateFaceId) {
        this.candidateFaceId = candidateFaceId;
    }

    public Candidates getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidates candidate) {
        this.candidate = candidate;
    }

    public byte[] getCandidateImg() {
        return candidateImg;
    }

    public void setCandidateImg(byte[] candidateImg) {
        this.candidateImg = candidateImg;
    }
}
