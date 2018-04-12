package au.com.acumen.faces.model;

import javax.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidates {
    @Id
    @Column(name="candidateId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long candidateId;

    @Column(nullable = true)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String dob;




    public Candidates() {

    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

}
