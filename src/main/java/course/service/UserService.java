package course.service;

import course.model.UserEntity;
import course.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}