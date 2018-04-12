package au.com.acumen.faces.model;

import com.google.gson.annotations.SerializedName;

public class CandidatesApi {


    @SerializedName("candidateTitle")
    private String candidateTitle = null;

    @SerializedName("cameraName")
    private String cameraName = null;

    @SerializedName("photoThumbnail")
    private byte[] photoThumbnail = null;

    @SerializedName("eventDateTime")
    private String eventDateTime = null;

    public CandidatesApi() {
    }

    public String getCandidateTitle() {
        return candidateTitle;
    }

    public void setCandidateTitle(String candidateTitle) {
        this.candidateTitle = candidateTitle;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public byte[] getPhotoThumbnail() {
        return photoThumbnail;
    }

    public void setPhotoThumbnail(byte[] photoThumbnail) {
        this.photoThumbnail = photoThumbnail;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
