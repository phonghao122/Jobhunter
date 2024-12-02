package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResFetchUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;

    public UserService(UserRepository userRepository, CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
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
        List<ResFetchUserDTO> users = new ArrayList<ResFetchUserDTO>();
        for (User user : pageUser.getContent()) {
            ResFetchUserDTO userDTO = new ResFetchUserDTO();
            userDTO.setId(user.getId());
            userDTO.setAddress(user.getAddress());
            userDTO.setAge(user.getAge());
            userDTO.setEmail(user.getEmail());
            userDTO.setGender(user.getGender());
            userDTO.setName(user.getName());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setUpdatedAt(user.getUpdatedAt());
            if (user.getCompany() != null) {
                ResCompanyDTO companyDTO = new ResCompanyDTO(
                        user.getCompany().getId(),
                        user.getCompany().getName());
                userDTO.setCompany(companyDTO);
            } else {
                userDTO.setCompany(null);
            }
            users.add(userDTO);
        }
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pageUser.getTotalElements());
        meta.setPages(pageUser.getTotalPages());
        result.setMeta(meta);
        result.setResult(users);
        return result;
    }

    public ResUpdateUserDTO handleUpdateUser(User user) {
        Optional<User> userOptional = this.fetchUserById(user.getId());
        if (userOptional.isPresent()) {
            userOptional.get().setEmail(user.getEmail());
            userOptional.get().setName(user.getName());
            userOptional.get().setPassword(user.getPassword());
            userOptional.get().setAddress(user.getAddress());
            userOptional.get().setAge(user.getAge());
            userOptional.get().setGender(user.getGender());
            Optional<Company> cOptional = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(cOptional.isPresent() ? cOptional.get() : null);
            User crrUser = this.handleSaveUser(userOptional.get());
            ResUpdateUserDTO userDTO = new ResUpdateUserDTO();
            userDTO.setId(crrUser.getId());
            userDTO.setAddress(crrUser.getAddress());
            userDTO.setAge(crrUser.getAge());
            userDTO.setGender(crrUser.getGender());
            userDTO.setName(crrUser.getName());
            userDTO.setUpdatedAt(crrUser.getUpdatedAt());
            if (crrUser.getCompany() != null) {
                ResCompanyDTO companyDTO = new ResCompanyDTO(
                        crrUser.getCompany().getId(),
                        crrUser.getCompany().getName());
                userDTO.setCompany(companyDTO);
            } else {
                userDTO.setCompany(null);
            }
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

    public List<User> fetchUserByCompany(Company company) {
        Optional<List<User>> usersOptional = Optional.ofNullable(this.userRepository.findByCompany(company));
        return usersOptional.isPresent() ? usersOptional.get() : null;
    }
}
