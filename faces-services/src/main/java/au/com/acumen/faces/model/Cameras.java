package au.com.acumen.faces.model;

import javax.persistence.*;

@Entity
@Table(name = "cameras")
public class Cameras {

    @Id
    @Column(name="cameraId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cameraId;

    @Column(nullable = true)
    private String cameraAddress;

    @Column(nullable = true)
    private String cameraLocation;

    @Column(nullable = true)
    private String cameraName;


    public Cameras() {
    }

    public Long getCameraId() {
        return cameraId;
    }

    public void setCameraId(Long cameraId) {
        this.cameraId = cameraId;
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
}
