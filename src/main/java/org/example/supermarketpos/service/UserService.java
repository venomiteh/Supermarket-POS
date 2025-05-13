package org.example.supermarketpos.service;

import org.example.supermarketpos.model.User;
import org.example.supermarketpos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
@Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
public List<User> findAll() {
    return userRepository.findAll();
}

public Optional<User> findById(long id) {
    return userRepository.findById(id);

}


public User save(User user) {
    return userRepository.save(user);
}
public void delete(long id) {
    userRepository.deleteById(id);
}

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(User user) throws Exception {
        // Check if the username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username already exists!");
        }

        // Optionally, you can hash the password here using BCrypt or another method
        userRepository.save(user);
    }
}



