package com.Repo;

import com.model.LoanRepaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

	@Repository
	public interface LoanRepaymentHistoryRepository extends JpaRepository<LoanRepaymentHistory, Long> {
	    List<LoanRepaymentHistory> findByLoanIdOrderByPaidAtDesc(Long loanId);
	}
