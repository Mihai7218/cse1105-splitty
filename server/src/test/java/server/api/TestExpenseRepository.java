package server.api;

import commons.Expense;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestExpenseRepository implements ExpenseRepository {
    public final List<Expense> expenses = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    /**
     * @param name
     */
    private void call(String name) {
        calledMethods.add(name);
    }

    /**
     *
     */
    @Override
    public void flush() {
        while (calledMethods.size() > 0) {
            calledMethods.remove(0);
        }
    }

    /**
     * @param entity entity to be saved. Must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> S saveAndFlush(S entity) {
        return null;
    }

    /**
     * @param entities entities to be saved.
     *                 Must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    /**
     * @param entities entities to be deleted.
     *                 Must not be {@literal null}.
     */
    @Override
    public void deleteAllInBatch(Iterable<Expense> entities) {

    }

    /**
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
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Expense getOne(Long aLong) {
        return null;
    }

    /**
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public Expense getById(Long id) {
        call("getById");
        return find(id).get();
    }

    /**
     * @param id
     * @return
     */
    private Optional<Expense> find(Long id) {
        return expenses.stream().
                filter(q -> q.getId() == id).
                findFirst();
    }

    /**
     * @param aLong must not be {@literal null}.
     * @return
     */
    @Override
    public Expense getReferenceById(Long aLong) {
        return null;
    }

    /**
     * @param example must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    /**
     * @param example must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> List<S> findAll(Example<S> example) {
        return null;
    }

    /**
     * @param example must not be {@literal null}.
     * @param sort    the {@link Sort} specification to sort the
     *                results by, may be {@link Sort#unsorted()}, must not be
     *                {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    /**
     * @param example  must not be {@literal null}.
     * @param pageable the pageable to request a paged result,
     *                 can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> Page<S> findAll(Example<S> example,
                                               Pageable pageable) {
        return null;
    }

    /**
     * @param example the {@link Example} to count instances
     *                for. Must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> long count(Example<S> example) {
        return 0;
    }

    /**
     * @param example the {@link Example} to use for the
     *                existence check. Must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> boolean exists(Example<S> example) {
        return false;
    }

    /**
     * @param example       must not be {@literal null}.
     * @param queryFunction the query function defining projection,
     *                      sorting, and the result type
     * @param <S>
     * @param <R>
     * @return
     */
    @Override
    public <S extends Expense, R> R findBy(Example<S> example,
           Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    /**
     * @param entity must not be {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> S save(S entity) {
        call("save");
        entity.setId(expenses.size());
        expenses.add(entity);
        return entity;
    }

    /**
     * @param entities must not be {@literal null} nor must it
     *                 contain {@literal null}.
     * @param <S>
     * @return
     */
    @Override
    public <S extends Expense> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public Optional<Expense> findById(Long id) {
        call("findById");
        for (Expense expense : expenses) {
            if (expense.getId() == id) {
                return Optional.of(expense);
            }
        }
        return null;
    }

    /**
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    /**
     * @return
     */
    @Override
    public List<Expense> findAll() {
        calledMethods.add("findAll");
        return expenses;
    }

    /**
     * @param longs must not be {@literal null} nor contain any
     *              {@literal null} values.
     * @return
     */
    @Override
    public List<Expense> findAllById(Iterable<Long> longs) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public long count() {
        return expenses.size();
    }

    /**
     * @param aLong must not be {@literal null}.
     */
    @Override
    public void deleteById(Long aLong) {

    }

    /**
     * @param entity must not be {@literal null}.
     */
    @Override
    public void delete(Expense entity) {

    }

    /**
     * @param longs must not be {@literal null}. Must not contain
     *              {@literal null} elements.
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    /**
     * @param entities must not be {@literal null}. Must not contain
     *                 {@literal null} elements.
     */
    @Override
    public void deleteAll(Iterable<? extends Expense> entities) {

    }

    /**
     *
     */
    @Override
    public void deleteAll() {

    }

    /**
     * @param sort the {@link Sort} specification to sort the results by,
     *             can be {@link Sort#unsorted()}, must not be
     *             {@literal null}.
     * @return
     */
    @Override
    public List<Expense> findAll(Sort sort) {
        return null;
    }

    /**
     * @param pageable the pageable to request a paged result,
     *                 can be {@link Pageable#unpaged()}, must not be
     *                 {@literal null}.
     * @return
     */
    @Override
    public Page<Expense> findAll(Pageable pageable) {
        return null;
    }
}
