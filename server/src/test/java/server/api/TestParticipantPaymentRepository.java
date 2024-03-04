package server.api;

import commons.ParticipantPayment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ParticipantPaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestParticipantPaymentRepository implements ParticipantPaymentRepository {

    public final List<String> calledMethods = new ArrayList<>();
    public final List<ParticipantPayment> participantPayments
            = new ArrayList<>();

    /**
     *
     * @param name
     */
    private void call(String name){
        calledMethods.add(name);
    }

    /**
     *
     * @return
     */
    @Override
    public List<ParticipantPayment> findAll(){
        calledMethods.add("findAll");
        return participantPayments;
    }

    /**
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public ParticipantPayment getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    /**
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public ParticipantPayment getById(Long id) {
        call("getById");
        return find(id).get();
    }

    /**
     *
     * @param id
     * @return
     */
    private Optional<ParticipantPayment> find(Long id) {
        return participantPayments.stream()
                .filter(q -> q.getId() == id)
                .findFirst();
    }

    /**
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public boolean existsById(Long id){
        call("existsById");
        return find(id).isPresent();
    }

    /**
     *
     * @param entity must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> S save(S entity) {
        call("save");
        entity.setId(participantPayments.size());
        participantPayments.add(entity);
        return entity;
    }

    /**
     *
     */
    @Override
    public void flush() {
        while(calledMethods.size()>0){
            calledMethods.remove(0);
        }
    }

    /**
     *
     * @param entity entity to be saved. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> S saveAndFlush(S entity) {
        return null;
    }

    /**
     *
     * @param entities entities to be saved. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> List<S> saveAllAndFlush(
            Iterable<S> entities) {
        return null;
    }

    /**
     *
     * @param entities entities to be deleted.
     *                 Must not be {@literal null}.
     */
    @Override
    public void deleteAllInBatch(Iterable<ParticipantPayment> entities) {

    }

    /**
     *
     * @param longs the ids of the entities to be deleted.
     *              Must not be {@literal null}.
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    /**
     *
     */
    @Override
    public void deleteAllInBatch() {

    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public ParticipantPayment getOne(Long aLong) {
        return null;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> Optional<S> findOne(
            Example<S> example) {
        return Optional.empty();
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> List<S> findAll(
            Example<S> example) {
        return null;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param sort the {@link Sort} specification to sort the results by,
     *            may be {@link Sort#unsorted()}, must not be
     *          {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> List<S> findAll(
            Example<S> example, Sort sort) {
        return null;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param pageable the pageable to request a paged result,
     *                can be {@link Pageable#unpaged()}, must not be
     *          {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> Page<S> findAll(
            Example<S> example, Pageable pageable) {
        return null;
    }

    /**
     *
     * @param example the {@link Example} to count instances for.
     *               Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> long count(Example<S> example) {
        return 0;
    }

    /**
     *
     * @param example the {@link Example} to use for the
     *                existence check. Must not be {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> boolean exists(Example<S> example) {
        return false;
    }

    /**
     *
     * @param example must not be {@literal null}.
     * @param queryFunction the query function defining
     *                      projection, sorting, and the result type
     * @return
     * @param <S>
     * @param <R>
     */
    @Override
    public <S extends ParticipantPayment, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>,
            R> queryFunction) {
        return null;
    }


    /**
     *
     * @param entities must not be {@literal null} nor must it
     *                 contain {@literal null}.
     * @return
     * @param <S>
     */
    @Override
    public <S extends ParticipantPayment> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<ParticipantPayment> findById(Long aLong) {
        return Optional.empty();
    }

    /**
     *
     * @param longs must not be {@literal null} nor contain any
     * {@literal null} values.
     * @return
     */
    @Override
    public List<ParticipantPayment> findAllById(Iterable<Long> longs) {
        return null;
    }

    /**
     *
     * @return
     */
    @Override
    public long count() {
        return 0;
    }

    /**
     *
     * @param aLong must not be {@literal null}.
     */
    @Override
    public void deleteById(Long id) {
        ParticipantPayment toDelete = find(id).get();
        participantPayments.remove(toDelete);
    }

    /**
     *
     * @param entity must not be {@literal null}.
     */
    @Override
    public void delete(ParticipantPayment entity) {

    }

    /**
     *
     * @param longs must not be {@literal null}. Must not
     *              contain {@literal null} elements.
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    /**
     *
     * @param entities must not be {@literal null}. Must not
     *                 contain {@literal null} elements.
     */
    @Override
    public void deleteAll(Iterable<? extends ParticipantPayment> entities) {

    }

    /**
     *
     */
    @Override
    public void deleteAll() {

    }

    /**
     *
     * @param sort the {@link Sort} specification to sort the
     *            results by, can be {@link Sort#unsorted()},
     *             must not be
     *          {@literal null}.
     * @return
     */
    @Override
    public List<ParticipantPayment> findAll(Sort sort) {
        return null;
    }

    /**
     *
     * @param pageable the pageable to request a paged
     *                 result, can be {@link Pageable#unpaged()}, must not be
     *          {@literal null}.
     * @return
     */
    @Override
    public Page<ParticipantPayment> findAll(Pageable pageable) {
        return null;
    }
}
