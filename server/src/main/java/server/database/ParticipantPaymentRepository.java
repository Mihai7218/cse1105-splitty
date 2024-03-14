package server.database;

import commons.ParticipantPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantPaymentRepository extends JpaRepository<ParticipantPayment, Long> {
}
