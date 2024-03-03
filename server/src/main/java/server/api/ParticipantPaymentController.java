package server.api;

import commons.ParticipantPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping( "api/events")
public class ParticipantPaymentController {

    private final ParticipantPaymentService participantPaymentService;

    /**
     * Constructor for the participantPayment controller
     * @param participantPaymentService the service to connect with the controller
     */
    @Autowired
    public ParticipantPaymentController(ParticipantPaymentService participantPaymentService) {
        this.participantPaymentService = participantPaymentService;
    }

    /**
     * Get method controller for retrieving all participantpayments for a certain expense
     * @param eventId id of the event the participantpayments belong to
     * @param expenseId id of the expense the participantpayments belong to
     * @return a list of participantpayments if found
     */
    @GetMapping(path="/{eventId}/expenses/{id}/participantpayment")
    public ResponseEntity<List<ParticipantPayment>> getParticipantPayment(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long expenseId
    ){
        return participantPaymentService.getAllParticipantPayment(eventId, expenseId);
    }

    /**
     * Get method to retrieve one specific participant payment by id
     * @param eventId id of the event the participantpayment is a part of
     * @param expenseId id of the expense the participantpayment is a part of
     * @param id id of the participantpayment itself
     * @return the participantpayment if found
     */
    @GetMapping(path="/{eventId}/expenses/{expenseId}/participantpayment/{id}")
    public ResponseEntity<ParticipantPayment> getParticipantPayment(
            @PathVariable("eventId") long eventId,
            @PathVariable("expenseId") long expenseId,
            @PathVariable("id") long id
    ){
        return participantPaymentService.getParticipantPayment(eventId, expenseId, id);
    }

    /**
     * Post method to create a new participant payment for a certain expense
     * @param eventId id of the event to add the participantpayment to
     * @param expenseId id of the expesne the participantpayment is part of
     * @param participantPayment the participantpayment to be added
     * @return the participanyPayment if successfully added
     */
    @PostMapping(path="/{eventId}/expenses/{id}/participantpayment")
    public ResponseEntity<ParticipantPayment> createParticipantPayment(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long expenseId,
            @RequestBody ParticipantPayment participantPayment
    ){
        return participantPaymentService.createParticipantPayment(
                eventId, expenseId, participantPayment);
    }

    /**
     * Put method to update values associated with the participantPayment
     * @param eventId id of the event the participantPayment is a part of
     * @param expenseId id of the expense associated with the participantPayment
     * @param id id of the participantPayment
     * @return the participantPayment if found
     */
    @PutMapping(path="/{eventId}/expenses/{expenseId}/participantpayment/{id}")
    public ResponseEntity<ParticipantPayment> updateParticipantPayment(
            @PathVariable("eventId") long eventId,
            @PathVariable("expenseId") long expenseId,
            @PathVariable("id") long id,
            @RequestBody ParticipantPayment participantPayment
    ){
        return participantPaymentService.updateParticipantPayment(
                eventId,expenseId,id, participantPayment);
    }

    /**
     * Delete method to remove a participantPayment
     * @param eventId id of the event the participantPayment is in
     * @param expenseId id of the expense the participantPayment belongs in
     * @param id id of the participantPayment
     * @return the participantPayment if successfully deleted
     */
    @DeleteMapping(path="/{eventId}/expenses/{expenseId}/participantpayment/{id}")
    public ResponseEntity<ParticipantPayment> deleteParticipantPayment(
            @PathVariable("eventId") long eventId,
            @PathVariable("expenseId") long expenseId,
            @PathVariable("id") long id
    ){
        return participantPaymentService.deleteParticipantPayment(eventId,expenseId,id);
    }

}
