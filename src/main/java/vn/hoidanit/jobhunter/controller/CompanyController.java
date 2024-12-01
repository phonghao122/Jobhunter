package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company) {
        Company crrCompany = this.companyService.handleSaveCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(crrCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) {
        Optional<Company> c = this.companyService.fetchCompanyById(id);
        if (c.isPresent()) {
            return ResponseEntity.ok().body(c.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> putCompany(@RequestBody Company company) {
        Company c = this.companyService.handleUpdateCompany(company);
        if (c != null) {
            return ResponseEntity.ok().body(c);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompanyById(@PathVariable("id") long id) {
        Optional<Company> c = this.companyService.fetchCompanyById(id);
        if (c.isPresent()) {
            this.companyService.handleDeleteCompany(id);
            return ResponseEntity.ok().body("success");
        }
        return ResponseEntity.badRequest().body("Not found Company");
    }
}
