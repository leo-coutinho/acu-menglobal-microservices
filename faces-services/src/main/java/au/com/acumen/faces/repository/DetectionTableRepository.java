package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.Detection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DetectionTableRepository extends JpaRepository<Detection, Long> {

    List<Detection> findByDetectionDateTime(Date detectionDate);



   }
