package st.cri.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import st.cri.app.domain.CallHistory;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {
}

