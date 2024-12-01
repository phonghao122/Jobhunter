package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.FetchUserDTO;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.dto.UpdateUserDTO;
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

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        List<FetchUserDTO> users = new ArrayList<FetchUserDTO>();
        for (User user : pageUser.getContent()) {
            FetchUserDTO userDTO = new FetchUserDTO();
            userDTO.setId(user.getId());
            userDTO.setAddress(user.getAddress());
            userDTO.setAge(user.getAge());
            userDTO.setEmail(user.getEmail());
            userDTO.setGender(user.getGender());
            userDTO.setName(user.getName());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setUpdatedAt(user.getUpdatedAt());
            users.add(userDTO);
        }
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageUser.getTotalElements());
        meta.setPages(pageUser.getTotalPages());
        result.setMeta(meta);
        result.setResult(users);
        return result;
    }

    public UpdateUserDTO handleUpdateUser(User user) {
        Optional<User> userOptional = this.fetchUserById(user.getId());
        if (userOptional.isPresent()) {
            userOptional.get().setEmail(user.getEmail());
            userOptional.get().setName(user.getName());
            userOptional.get().setPassword(user.getPassword());
            userOptional.get().setAddress(user.getAddress());
            userOptional.get().setAge(user.getAge());
            userOptional.get().setGender(user.getGender());
            User crrUser = this.handleSaveUser(userOptional.get());
            UpdateUserDTO userDTO = new UpdateUserDTO();
            userDTO.setId(crrUser.getId());
            userDTO.setAddress(crrUser.getAddress());
            userDTO.setAge(crrUser.getAge());
            userDTO.setGender(crrUser.getGender());
            userDTO.setName(crrUser.getName());
            userDTO.setUpdatedAt(crrUser.getUpdatedAt());
            return userDTO;
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean CheckExistEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public boolean CheckExistId(long id) {
        return this.userRepository.existsById(id);
    }

    public void updateUserToken(String email, String token) {
        User user = this.handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            this.userRepository.save(user);
        }
    }

    public User fetchUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
