
	package com.Repo;

	import com.model.Loan;
	import com.model.LoanStatus;
	import com.model.User;
	import org.springframework.data.jpa.repository.JpaRepository;
	import java.util.List;

	public interface LoanRepository extends JpaRepository<Loan, Long> {

	    List<Loan> findByUser(User user);
	    
	    List<Loan> findByStatus(LoanStatus status);
	    
	    List<Loan> findByUserAndStatus(User user, LoanStatus status);
	}
	


