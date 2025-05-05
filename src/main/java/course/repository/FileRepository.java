package course.repository;

import course.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    
    @Query("SELECT f FROM FileEntity f WHERE " +
           "LOWER(f.fileName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.courseShortName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.instructor) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(f.semester) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FileEntity> searchByKeyword(@Param("keyword") String keyword);

    List<FileEntity> findByUploaderUsername(String uploaderUsername);

    List<FileEntity> findByIdIn(List<Long> ids);
}