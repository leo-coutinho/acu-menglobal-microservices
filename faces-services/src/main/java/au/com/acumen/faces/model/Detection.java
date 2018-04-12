package au.com.acumen.faces.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "detection")
public class Detection {
    @Id
    @Column(name="detectionId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long detectionId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date detectionDateTime;

    @Lob
    @Column(name="detectionImg", nullable=false, columnDefinition="mediumblob")
    private byte[] detectionImg;

    @OneToOne
    @JoinColumn(name="cameraId")
    private Cameras cameras;

    public Detection() {
    }

    public Long getDetectionId() {
        return detectionId;
    }

    public void setDetectionId(Long detectionId) {
        this.detectionId = detectionId;
    }

    public Date getDetectionDateTime() {
        return detectionDateTime;
    }

    public void setDetectionDateTime(Date detectionDateTime) {
        this.detectionDateTime = detectionDateTime;
    }

    public byte[] getDetectionImg() {
        return detectionImg;
    }

    public void setDetectionImg(byte[] detectionImg) {
        this.detectionImg = detectionImg;
    }

    public Cameras getCameras() {
        return cameras;
    }

    public void setCameras(Cameras cameras) {
        this.cameras = cameras;
    }
}
