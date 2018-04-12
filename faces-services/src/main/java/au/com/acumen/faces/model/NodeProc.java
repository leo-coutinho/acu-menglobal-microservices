package au.com.acumen.faces.model;

import java.util.List;

public class NodeProc {

    private String cameraID;
    private String timeUTC;
    private String jobID;
    private Double timeCode;
    private Integer framesVisible;
    private String position;
    private List<Images> images;
//         Quality<Decimal>
//         ImageBase64<Base64>

    public NodeProc() {
    }

    public String getCameraID() {
        return cameraID;
    }

    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }

    public String getTimeUTC() {
        return timeUTC;
    }

    public void setTimeUTC(String timeUTC) {
        this.timeUTC = timeUTC;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public Double getTimeCode() {
        return timeCode;
    }

    public void setTimeCode(Double timeCode) {
        this.timeCode = timeCode;
    }

    public Integer getFramesVisible() {
        return framesVisible;
    }

    public void setFramesVisible(Integer framesVisible) {
        this.framesVisible = framesVisible;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }
}
