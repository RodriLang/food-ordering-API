package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.services.EmploymentService;
import com.group_three.food_ordering.services.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;
import static com.group_three.food_ordering.utils.EntityName.FOOD_VENUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final UserService userService;
    private final FoodVenueRepository foodVenueRepository;
    private final EmploymentMapper employmentMapper;

    @Override
    public EmploymentResponseDto create(EmploymentRequestDto dto) {
        User user = userService.getEntityByEmail(dto.getUserEmail());
        FoodVenue foodVenue = findFoodVenueById(dto.getFoodVenueId());

        Employment employment = Employment.builder()
                .user(user)
                .foodVenue(foodVenue)
                .role(dto.getRole())
                .build();

        Employment saved = employmentRepository.save(employment);
        log.info("Created new employment for user {} in venue {} with role {}", user.getEmail(), foodVenue.getName(), dto.getRole());
        return employmentMapper.toResponseDto(saved);
    }

    @Override
    public Employment getEmploymentEntityById(UUID publicId, Boolean active) {
        return employmentRepository.findByPublicIdAndActiveAndDeletedFalse(publicId, active)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYMENT, publicId.toString()));
    }

    @Override
    public EmploymentResponseDto getEmploymentDtoById(UUID publicId) {
        return employmentMapper.toResponseDto(getEmploymentEntityById(publicId, Boolean.TRUE));
    }

    @Override
    public List<Employment> getEmploymentsByUser(String userEmail, UUID foodVenueId, Boolean active) {
        return employmentRepository.findByUser_EmailAndFoodVenue_PublicIdAndActiveAndDeletedFalse(userEmail, foodVenueId, active);
    }

    @Override
    public EmploymentResponseDto update(UUID publicId, Employment newEmployment) {
        Employment employment = getEmploymentEntityById(publicId, null);
        employment.setRole(newEmployment.getRole());
        employment.setUser(newEmployment.getUser());
        employment.setFoodVenue(newEmployment.getFoodVenue());
        Employment updated = employmentRepository.save(employment);
        return employmentMapper.toResponseDto(updated);
    }

    @Override
    public void softDelete(UUID publicId) {
        Employment employment = getEmploymentEntityById(publicId, Boolean.TRUE);
        employment.setDeleted(true);
        employmentRepository.save(employment);
        log.info("Soft-deleted employment with id {}", publicId);
    }

    @Override
    public Page<EmploymentResponseDto> findByFilters(UUID foodVenueId, List<RoleType> roles, Boolean active, Pageable pageable) {

        Specification<Employment> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Employment, FoodVenue> foodVenueJoin = root.join("foodVenue");

            predicates.add(criteriaBuilder.equal(foodVenueJoin.get("publicId"), foodVenueId));

            if (roles != null && !roles.isEmpty()) {
                predicates.add(root.get("role").in(roles));
            }

            if (active != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), active));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Employment> employmentPage = employmentRepository.findAll(spec, pageable);

        return employmentPage.map(employmentMapper::toResponseDto);
    }

    private FoodVenue findFoodVenueById(UUID id) {
        return foodVenueRepository.findByPublicIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE, id.toString()));
    }
}