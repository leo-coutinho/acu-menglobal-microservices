package au.com.acumen.faces.model;


import javax.persistence.*;

@Entity
@Table(name = "msCandidateFaces")
public class MsCandidateFaces {

    @Id
    @Column(name="mscandidateFaceId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long mscandidateFaceId;

    @Column(nullable = false)
    private String persistedFaceId;

    @OneToOne
    @JoinColumn(name="candidateFaceId")
    private CandidateFaces candidateFaces;

    public MsCandidateFaces() {
    }

    public Long getMscandidateFaceId() {
        return mscandidateFaceId;
    }

    public void setMscandidateFaceId(Long mscandidateFaceId) {
        this.mscandidateFaceId = mscandidateFaceId;
    }

    public String getPersistedFaceId() {
        return persistedFaceId;
    }

    public void setPersistedFaceId(String persistedFaceId) {
        this.persistedFaceId = persistedFaceId;
    }

    public CandidateFaces getCandidateFaces() {
        return candidateFaces;
    }

    public void setCandidateFaces(CandidateFaces candidateFaces) {
        this.candidateFaces = candidateFaces;
    }
}
