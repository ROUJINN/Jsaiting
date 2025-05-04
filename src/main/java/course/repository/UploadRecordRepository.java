package course.repository;

import course.model.UploadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadRecordRepository extends JpaRepository<UploadRecord, Long> {

    @Query("SELECT ur FROM UploadRecord ur WHERE ur.user.id = :userId")
    List<UploadRecord> findByUserId(@Param("userId") Long userId);
}
