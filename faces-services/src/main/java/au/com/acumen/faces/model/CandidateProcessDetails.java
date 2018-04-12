package au.com.acumen.faces.model;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class CandidateProcessDetails {

    private Long candidateId;

    private String name;

    private String gender;

    private String dob;

    private String candidateImage;

    private String cameraAddress;

    private String cameraLocation;

    private String cameraName;

    @Temporal(TemporalType.TIMESTAMP)
    private String detectionDateTime;

    private String detectionImg;

    private double confidence;

    public CandidateProcessDetails() {
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
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

    public String getCandidateImage() {
        return candidateImage;
    }

    public void setCandidateImage(String candidateImage) {
        this.candidateImage = candidateImage;
    }

    public String getCameraAddress() {
        return cameraAddress;
    }

    public void setCameraAddress(String cameraAddress) {
        this.cameraAddress = cameraAddress;
    }

    public String getCameraLocation() {
        return cameraLocation;
    }

    public void setCameraLocation(String cameraLocation) {
        this.cameraLocation = cameraLocation;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getDetectionDateTime() {
        return detectionDateTime;
    }

    public void setDetectionDateTime(String detectionDateTime) {
        this.detectionDateTime = detectionDateTime;
    }

    public String getDetectionImg() {
        return detectionImg;
    }

    public void setDetectionImg(String detectionImg) {
        this.detectionImg = detectionImg;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
