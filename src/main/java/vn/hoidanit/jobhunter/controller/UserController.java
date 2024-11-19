package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User crrUser = this.userService.handleSaveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(crrUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("id khong lon hon 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        Optional<User> user = this.userService.fetchUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return null;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.fetchAllUsers());
    }

    @PutMapping("/users")
    public ResponseEntity<User> putUser(@RequestBody User user) {

        return ResponseEntity.ok(this.userService.handleUpdateUser(user));
    }

}
