package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.DuplicatedEmploymentException;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.EmploymentMapper;
import com.group_three.food_ordering.models.Employment;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.EmploymentRepository;
import com.group_three.food_ordering.services.EmploymentInvitationService;
import com.group_three.food_ordering.services.EmploymentService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.EMPLOYMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmploymentMapper employmentMapper;
    private final EmploymentInvitationService employmentInvitationService;

    @Override
    public EmploymentResponseDto create(FoodVenue foodVenue, User user, RoleType role) {

        resolveEmployment(user.getEmail(), foodVenue.getPublicId(), role);

        Instant tokenExpiration = Instant.now().plusSeconds(259200); // Corresponde a 72 horas

        Employment employment = Employment.builder()
                .user(user)
                .foodVenue(foodVenue)
                .role(role)
                .active(Boolean.FALSE)
                .status(EmploymentStatus.PENDING)
                .invitationToken(UUID.randomUUID().toString())
                .tokenExpiration(tokenExpiration)
                .build();

        log.debug("[EmploymentRepository] Calling save to create new {} employment for user {} in venue {}",
                role, user.getPublicId(), foodVenue.getPublicId());
        Employment saved = employmentRepository.save(employment);

        log.info("Created new employment for user {} in venue {} with role {}",
                user.getEmail(), foodVenue.getName(), role);

        employmentInvitationService.createInvitation(employment);

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
    public Page<Employment> findByFilters(UUID foodVenueId, List<RoleType> roles, Boolean active, Pageable pageable) {

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

        return employmentRepository.findAll(spec, pageable);
    }

    private void resolveEmployment(String userEmail, UUID foodVenueId, RoleType role){
        Optional<Employment> optionalEmployment = employmentRepository.findByUser_EmailAndFoodVenue_PublicIdAndRoleAndDeletedFalse(
                userEmail, foodVenueId, role);
        if (optionalEmployment.isPresent()) {
            Employment employment = optionalEmployment.get();
            if (employment.getActive().equals(Boolean.TRUE)) {
                throw new DuplicatedEmploymentException(userEmail, foodVenueId.toString(), role.name());
            }
        }
    }

}