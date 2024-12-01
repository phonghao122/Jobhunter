package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.CreateUserDTO;
import vn.hoidanit.jobhunter.domain.dto.FetchUserDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UpdateUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a User")
    public ResponseEntity<CreateUserDTO> createNewUser(@RequestBody User user) throws IdInvalidException {
        if (this.userService.CheckExistEmail(user.getEmail())) {
            throw new IdInvalidException("Email đã tồn tại");
        }
        String hashPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User crrUser = this.userService.handleSaveUser(user);
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setId(crrUser.getId());
        userDTO.setAddress(crrUser.getAddress());
        userDTO.setAge(crrUser.getAge());
        userDTO.setEmail(crrUser.getEmail());
        userDTO.setGender(crrUser.getGender());
        userDTO.setName(crrUser.getName());
        userDTO.setCreatedAt(crrUser.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a User")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        if (!this.userService.CheckExistId(id)) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }
        if (id >= 1500) {
            throw new IdInvalidException("id khong lon hon 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch a User")
    public ResponseEntity<FetchUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        if (!this.userService.CheckExistId(id)) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }
        Optional<User> user = this.userService.fetchUserById(id);
        if (user.isPresent()) {
            FetchUserDTO userDTO = new FetchUserDTO();
            userDTO.setId(user.get().getId());
            userDTO.setAddress(user.get().getAddress());
            userDTO.setAge(user.get().getAge());
            userDTO.setEmail(user.get().getEmail());
            userDTO.setGender(user.get().getGender());
            userDTO.setName(user.get().getName());
            userDTO.setCreatedAt(user.get().getCreatedAt());
            userDTO.setUpdatedAt(user.get().getUpdatedAt());
            return ResponseEntity.ok(userDTO);
        }
        return null;
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a User")
    public ResponseEntity<UpdateUserDTO> putUser(@RequestBody User user) throws IdInvalidException {
        if (!this.userService.CheckExistId(user.getId())) {
            throw new IdInvalidException("User có id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.handleUpdateUser(user));
    }

}
