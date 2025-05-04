package course.service;

import course.model.UserEntity;
import course.model.DownloadRecord;
import course.model.UploadRecord;
import course.repository.UserRepository;
import course.repository.DownloadRecordRepository;
import course.repository.UploadRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity registerUser(String username, String password) {
        UserEntity found_user = userRepository.findByUsername(username);
        if (found_user != null) {
            return null;
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public UserEntity authenticateUser(String username, String password) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Autowired
    private UploadRecordRepository uploadRecordRepository;

    @Autowired
    private DownloadRecordRepository downloadRecordRepository;

    public List<UploadRecord> getUploadRecords(UserEntity user) {
        return uploadRecordRepository.findByUserId(user.getId());
    }

    public List<DownloadRecord> getDownloadRecords(UserEntity user) {
        return downloadRecordRepository.findByUserId(user.getId());
    }
}