package com.innov8ors.insurance.repository;

import com.innov8ors.insurance.entity.Claim;
import com.innov8ors.insurance.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ClaimRepository extends BaseRepository<Claim, Long> {
    @Query("SELECT c FROM Claim c JOIN c.userPolicy up WHERE up.userId = :userId")
    Page<Claim> findByUserId(@Param("userId") Long userId, Pageable pageable);

    List<Claim> findByUserPolicyId(Long userPolicyId);

    List<Claim> findByStatus(ClaimStatus status);

    @Query("SELECT c FROM Claim c JOIN c.userPolicy up WHERE up.userId = :userId AND c.status = :status")
    List<Claim> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ClaimStatus status);

    Optional<Claim> findByIdAndUserPolicyUserId(Long id, Long userId);
}
