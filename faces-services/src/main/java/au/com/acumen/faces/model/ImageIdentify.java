package au.com.acumen.faces.model;

public class ImageIdentify {

    private String personId;
    private double confidence;

    public ImageIdentify() {
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
