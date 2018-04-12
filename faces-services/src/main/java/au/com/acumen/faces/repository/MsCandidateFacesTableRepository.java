package au.com.acumen.faces.repository;


import au.com.acumen.faces.model.MsCandidateFaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MsCandidateFacesTableRepository extends JpaRepository<MsCandidateFaces, Long> {

    List<MsCandidateFaces> findByPersistedFaceId(String persistedFaceId);


   }
