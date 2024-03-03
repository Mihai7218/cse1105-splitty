package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantPaymentRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@Service
public class ParticipantPaymentService {

    private final ParticipantRepository participantRepository;
    private final ParticipantPaymentRepository participantPaymentRepository;
    private final EventRepository eventRepository;

    /**
     * Constructor for the participantpayment service
     * @param participantRepository repository for participants
     * @param participantPaymentRepository repository for the participantPayments
     * @param eventRepository repository for events
     */
    @Autowired
    public ParticipantPaymentService(ParticipantRepository participantRepository,
                                     ParticipantPaymentRepository participantPaymentRepository,
                                     EventRepository eventRepository) {
        this.participantRepository = participantRepository;
        this.participantPaymentRepository = participantPaymentRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Method to find the list of ParticipantPayments associated with given eventId and ExpenseId
     * @param eventId id of the event to search for the payments in
     * @param expenseId id of the expense to find payments in
     * @return list of participant payments for given event
     */
    public ResponseEntity<List<ParticipantPayment>> getAllParticipantPayment(
            long eventId, long expenseId) {
        if(eventId < 0 || expenseId < 0){
            return ResponseEntity.badRequest().build();
        }
        try{
            Event event = eventRepository.getReferenceById(eventId);
            List<Expense> expenses = event.getExpensesList();
            Optional<Expense> matching = expenses.stream()
                    .filter(item -> item.getId() == expenseId).findFirst();
            return matching.map(expense ->
                    ResponseEntity.ok(expense.getSplit())).
                    orElseGet(() -> ResponseEntity.notFound().build());
        }catch(EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves the participant Payment with the given id
     * @param eventId id of the event that the participant payment is in
     * @param expenseId expense that the participant payment is associated with
     * @param id id of the participant payment
     * @return participant payment if found
     */
    public ResponseEntity<ParticipantPayment> getParticipantPayment(
            long eventId, long expenseId, long id) {
        if(id < 0) return ResponseEntity.badRequest().build();
        ResponseEntity<List<ParticipantPayment>> result =
                getAllParticipantPayment(eventId,expenseId);
        if(result.getStatusCode() != OK){
            return result.getStatusCode()==BAD_REQUEST ?
                    ResponseEntity.badRequest().build() : ResponseEntity.notFound().build();
        }
        Optional<ParticipantPayment> participantPayment = result.getBody()
                .stream().filter(item -> item.getId() == id).findFirst();
        return participantPayment.map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.notFound().build());
    }

    /**
     * Creates a new participantpayment for the given expense and event
     * @param eventId the id of the event
     * @param expenseId the id of the expense
     * @param participantPayment the participantPayment to be added
     * @return the participantpayment if successfully added
     */
    public ResponseEntity<ParticipantPayment> createParticipantPayment(
            long eventId, long expenseId, ParticipantPayment participantPayment) {
        if(participantPayment == null) return ResponseEntity.badRequest().build();
        try{
            Participant participant = participantRepository
                    .getReferenceById(participantPayment.getParticipant().getId());
        }catch(EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
        if(getAllParticipantPayment(eventId, expenseId).getStatusCode() != OK){
            return getAllParticipantPayment(eventId,expenseId).getStatusCode()==BAD_REQUEST ?
                    ResponseEntity.badRequest().build() : ResponseEntity.notFound().build();
        }
        List<ParticipantPayment> participantPaymentList =
                getAllParticipantPayment(eventId,expenseId).getBody();
        participantPaymentRepository.save(participantPayment);
        participantPaymentList.add(participantPayment);
        return ResponseEntity.ok(participantPayment);
    }

    /**
     * Method to change the values of a given participant payment by id
     * @param eventId id of the event that the participant payment is found in
     * @param expenseId id of the expense that the participant payment is part of
     * @param id id of the participant payment to edit
     * @return editted participant payment if successful
     */
    public ResponseEntity<ParticipantPayment> updateParticipantPayment(
            long eventId, long expenseId, long id,ParticipantPayment participantPayment) {
        ResponseEntity<ParticipantPayment> searchResult =
                getParticipantPayment(eventId,expenseId,id);
        if(searchResult.getStatusCode() != OK){
            return searchResult;
        }
        Participant participant;
        try{
            participant =
                    participantRepository.getReferenceById(
                            participantPayment.getParticipant().getId());
        }catch(EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
        ParticipantPayment old = searchResult.getBody();
        if(old == null) return ResponseEntity.badRequest().build();
        old.setParticipant(participant);
        old.setPaymentAmount(participantPayment.getPaymentAmount());
        participantPaymentRepository.save(old);
        return ResponseEntity.ok(old);
    }

    /**
     * Method to remove a participant payment from the list in the expense
     * @param eventId id of the event that the expense and participant payment are part of
     * @param expenseId id of the expense that the participant payment is part of
     * @param id id of the participant payment
     * @return participant payment if found and successfully deleted
     */
    public ResponseEntity<ParticipantPayment> deleteParticipantPayment(
            long eventId, long expenseId, long id) {
        ResponseEntity<List<ParticipantPayment>> resultFindAll =
                getAllParticipantPayment(eventId,expenseId);
        ResponseEntity<ParticipantPayment> resultFindSpec =
                getParticipantPayment(eventId,expenseId,id);
        if(resultFindAll.getStatusCode() != OK || resultFindSpec.getStatusCode() != OK){
            return ResponseEntity.badRequest().build();
        }
        if(resultFindAll.getBody() == null || resultFindSpec.getBody() == null)
            return ResponseEntity.badRequest().build();
        List<ParticipantPayment> listForAll = resultFindAll.getBody();
        listForAll.remove(resultFindSpec.getBody());
        return ResponseEntity.ok(resultFindSpec.getBody());
    }
}
