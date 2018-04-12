package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.Cameras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraTableRepository extends JpaRepository<Cameras, Long> {

    Cameras findByCameraAddress(String cameraAddress);
    List<Cameras> findByCameraLocation(String location);
    List<Cameras> findByCameraName(String cameraName);
}
