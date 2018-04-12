package au.com.acumen.faces.model;

import javax.persistence.*;

@Entity
@Table(name = "msRecognition")
public class MsRecognition {
    @Id
    @Column(name="recognitionId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long recognitionId;


    @OneToOne
    @JoinColumn(name="detectionId")
    private Detection detection;


    @Column(nullable = false)
    private String personId;


    @Column(nullable = false)
    private double confidence;


    public MsRecognition() {
    }

    public Long getRecognitionId() {
        return recognitionId;
    }

    public void setRecognitionId(Long recognitionId) {
        this.recognitionId = recognitionId;
    }

    public Detection getDetection() {
        return detection;
    }

    public void setDetection(Detection detection) {
        this.detection = detection;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
