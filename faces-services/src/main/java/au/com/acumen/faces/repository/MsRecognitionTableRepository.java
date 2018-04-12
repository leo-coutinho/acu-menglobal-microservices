package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.MsRecognition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MsRecognitionTableRepository extends JpaRepository<MsRecognition, Long> {


   List<MsRecognition> findByPersonId(String personId);

   }
