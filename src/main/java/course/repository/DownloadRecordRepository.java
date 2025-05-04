package course.repository;

import course.model.DownloadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long> {

    @Query("SELECT dr FROM DownloadRecord dr WHERE dr.user.id = :userId")
    List<DownloadRecord> findByUserId(@Param("userId") Long userId);
}
