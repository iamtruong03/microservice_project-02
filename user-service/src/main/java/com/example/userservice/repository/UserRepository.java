package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  // Unique constraints
  boolean existsByEmail(String email);
  boolean existsByEmailAndIdNot(String email, Long id);
  boolean existsByNationalId(String nationalId);
  boolean existsByNationalIdAndIdNot(String nationalId, Long id);
  
  // Complex search query
  @Query("SELECT u FROM User u WHERE " +
         "(:keyword IS NULL OR :keyword = '' OR " +
         "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
         "LOWER(u.nationalId) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<User> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
  
  // Name searches (firstName + lastName)
  @Query("SELECT u FROM User u WHERE " +
         "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
  Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
  
  Page<User> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
  Page<User> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);
  
  // Email search
  Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);
  
  // Phone search
  Page<User> findByPhoneNumberContaining(String phoneNumber, Pageable pageable);
  
  // Location searches
  Page<User> findByCityContainingIgnoreCase(String city, Pageable pageable);
  Page<User> findByStateContainingIgnoreCase(String state, Pageable pageable);
  Page<User> findByCountryContainingIgnoreCase(String country, Pageable pageable);
  
  // Status searches
  Page<User> findByIsActive(Boolean isActive, Pageable pageable);
  Page<User> findByIsVerified(Boolean isVerified, Pageable pageable);
  Page<User> findByKycStatus(String kycStatus, Pageable pageable);
  Page<User> findByRiskLevel(String riskLevel, Pageable pageable);
  
  // Gender search
  Page<User> findByGender(String gender, Pageable pageable);
  
  // Age range search
  @Query("SELECT u FROM User u WHERE u.dateOfBirth BETWEEN :startDate AND :endDate")
  Page<User> findByDateOfBirthBetween(@Param("startDate") java.time.LocalDate startDate, 
                                     @Param("endDate") java.time.LocalDate endDate, 
                                     Pageable pageable);
  
  // Employment search
  Page<User> findByOccupationContainingIgnoreCase(String occupation, Pageable pageable);
  Page<User> findByEmployerNameContainingIgnoreCase(String employerName, Pageable pageable);
  
  // Advanced filters
  @Query("SELECT u FROM User u WHERE " +
         "(:isActive IS NULL OR u.isActive = :isActive) AND " +
         "(:isVerified IS NULL OR u.isVerified = :isVerified) AND " +
         "(:kycStatus IS NULL OR u.kycStatus = :kycStatus) AND " +
         "(:riskLevel IS NULL OR u.riskLevel = :riskLevel) AND " +
         "(:gender IS NULL OR u.gender = :gender) AND " +
         "(:city IS NULL OR LOWER(u.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
         "(:country IS NULL OR LOWER(u.country) LIKE LOWER(CONCAT('%', :country, '%')))")
  Page<User> findByFilters(@Param("isActive") Boolean isActive,
                          @Param("isVerified") Boolean isVerified, 
                          @Param("kycStatus") String kycStatus,
                          @Param("riskLevel") String riskLevel,
                          @Param("gender") String gender,
                          @Param("city") String city,
                          @Param("country") String country,
                          Pageable pageable);
}
