package course.service;

import course.model.FileEntity;
import course.model.UserEntity;
import course.model.DownloadRecord;
import course.repository.FileRepository;
import course.repository.UserRepository;
import course.repository.DownloadRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
    private DownloadRecordRepository downloadRecordRepository;

    public List<DownloadRecord> getDownloadRecords(UserEntity user) {
        return downloadRecordRepository.findByUsername(user.getUsername());
    }


}