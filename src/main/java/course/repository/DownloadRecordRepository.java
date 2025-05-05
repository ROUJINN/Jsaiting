package course.repository;

import course.model.DownloadRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadRecordRepository extends JpaRepository<DownloadRecord, Long> {
    // 可按需添加自定义查询方法
    List<DownloadRecord> findByUsername(String username);
}
