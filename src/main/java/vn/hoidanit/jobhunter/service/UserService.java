package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public Optional<User> fetchUserById(long id) {
        return this.userRepository.findById(id);
    }

    public List<User> fetchAllUsers() {
        return this.userRepository.findAll();
    }

    public User handleUpdateUser(User user) {
        Optional<User> crrUser = this.fetchUserById(user.getId());
        if (crrUser.isPresent()) {
            crrUser.get().setEmail(user.getEmail());
            crrUser.get().setName(user.getName());
            crrUser.get().setPassword(user.getPassword());
            this.handleSaveUser(crrUser.get());
            return crrUser.get();
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
