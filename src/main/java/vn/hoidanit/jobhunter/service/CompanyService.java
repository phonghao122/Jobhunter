package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Optional<Company> fetchCompanyById(long id) {
        return this.companyRepository.findById(id);
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> c = this.fetchCompanyById(company.getId());
        if (c.isPresent()) {
            c.get().setName(company.getName());
            c.get().setAddress(company.getAddress());
            c.get().setDescription(company.getDescription());
            c.get().setLogo(company.getLogo());
            this.handleSaveCompany(c.get());
            return c.get();
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllCompanies(Pageable pageable) {
        Page<Company> pageCompanies = this.companyRepository.findAll(pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();
        meta.setPage(pageCompanies.getNumber() + 1);
        meta.setPageSize(pageCompanies.getSize());
        meta.setTotal(pageCompanies.getTotalElements());
        meta.setPages(pageCompanies.getTotalPages());
        result.setMeta(meta);
        result.setResult(pageCompanies.getContent());
        return result;
    }
}
