package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.unigap.api.common.ApiException;
import vn.unigap.api.common.ErrorCode;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.UpdateEmployerDtoOut;
import vn.unigap.api.entity.Employer;
import vn.unigap.api.repository.EmployerRepository;

@Service
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;

    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public EmployerDtoOut create(EmployerDtoIn employerDtoIn) {
        // Check if the email is existing yet
        employerRepository.findByEmail(employerDtoIn.getEmail()).ifPresent(user -> {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "email already existed");
        });

        Employer employer = employerRepository.save(Employer.builder()
                .email(employerDtoIn.getEmail())
                .name(employerDtoIn.getName())
                .provinceId(employerDtoIn.getProvinceId())
                .description(employerDtoIn.getDescription())
                .build());

        // Sử dụng phương thức from để chuyển đổi entity sang DTO
        return EmployerDtoOut.from(employer);
    }


    @Override
    public UpdateEmployerDtoOut update(Long id, EmployerDtoIn employerDtoIn) {
        // Check if the id is existing yet
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));

        employer.setEmail(employerDtoIn.getEmail());
        employer.setName(employerDtoIn.getName());
        employer.setProvinceId(employerDtoIn.getProvinceId());
        employer.setDescription(employerDtoIn.getDescription());

        employer = employerRepository.save(employer);

        // Sử dụng phương thức from để chuyển đổi entity sang DTO
        return UpdateEmployerDtoOut.from(employer);
    }

    @Override
    public EmployerDtoOut get(Long id) {
        // Check if the id is existing yet
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));

        return EmployerDtoOut.from(employer);
    }

    /*Copy from sample projects*/
    @Override
    public PageDtoOut<EmployerDtoOut> list(PageDtoIn pageDtoIn) {
        Page<Employer> employers = this.employerRepository
                .findAll(PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize(), Sort.by("name").ascending()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), employers.getTotalElements(),
                employers.stream().map(EmployerDtoOut::from).toList());
    }

    @Override
    public void delete(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));
        employerRepository.delete(employer);
    }
}
