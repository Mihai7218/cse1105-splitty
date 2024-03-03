package server.api;

import com.jayway.jsonpath.internal.filter.ExpressionNode;
import commons.Event;
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

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public <S extends Expense> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public List<Expense> findAll() {
        calledMethods.add("findAll");
        return expenses;
    }

    @Override
    public List<Expense> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public void flush() {
    }

    public <S extends Expense> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Expense> entities) {
    }


    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
    }

    @Override
    public void deleteAllInBatch() {
    }

    @Override
    public Expense getOne(Long id) {
        return null;
    }

    @Override
    public Expense getById(Long id) {
        call("getById");
        return find(id).get();
    }


    /**
     *
     * @param id
     * @return
     */
    @Override
    public Expense getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    /**
     *
     * @param id
     * @return
     */
    private Optional<Expense> find(Long id) {
        return expenses.stream().filter(q -> q.getId() == id).findFirst();
    }

    /**
     *
     * @param example
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * @param example
     * @param sort
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<Expense> findAll(Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * @param entity
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> S save(S entity) {
        call("save");
        expenses.add(entity);
        return entity;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public Optional<Expense> findById(Long id) {
        for(Expense expense : expenses) {
            if (expense.getId() == id) {
                return Optional.of(expense);
            }
        }
        return null;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public boolean existsById(Long id) {
        call("existsById");
        return find(id).isPresent();
    }

    /**
     *
     * @return
     */
    @Override
    public long count() {
        return expenses.size();
    }

    /**
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
    }

    /**
     *
     * @param entity
     */
    @Override
    public void delete(Expense entity) {
    }

    /**
     *
     * @param ids
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
    }

    /**
     *
     * @param entities
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
     *
     * @param example
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> Optional<S> findOne(Example<S> example) {
        return null;
    }

    /**
     *
     * @param example
     * @param pageable
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> Page<S> findAll(Example<S> example,
                                               Pageable pageable) {
        return null;
    }

    /**
     *
     * @param example
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> long count(Example<S> example) {
        return 0;
    }

    /**
     *
     * @param example
     * @return
     * @param <S>
     */
    @Override
    public <S extends Expense> boolean exists(Example<S> example) {
        return false;
    }

    /**
     *
     * @param example
     * @param queryFunction
     * @return
     * @param <S>
     * @param <R>
     */
    @Override
    public <S extends Expense, R> R findBy(Example<S> example,
                                         Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Expense> findAll(Sort sort) {
        return null;
    }
}
