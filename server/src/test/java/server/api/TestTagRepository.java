/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.Tag;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestTagRepository implements TagRepository {

    public final List<Tag> tags = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    /**
     * @param name
     */
    private void call(String name) {
        calledMethods.add(name);
    }

    /**
     * @return
     */
    @Override
    public List<Tag> findAll() {
        calledMethods.add("findAll");
        return tags;
    }

    /**
     * @param sort
     * @return
     */
    @Override
    public List<Tag> findAll(Sort sort) {
        return null;
    }

    /**
     * @param ids
     * @return
     */
    @Override
    public List<Tag> findAllById(Iterable<Long> ids) {
        return null;
    }

    /**
     * @param entities
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    /**
     *
     */
    @Override
    public void flush() {

    }

    /**
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> S saveAndFlush(S entity) {
        return null;
    }

    /**
     * @param entities
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    /**
     * @param entities
     */
    @Override
    public void deleteAllInBatch(Iterable<Tag> entities) {

    }

    /**
     * @param ids
     */
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {

    }

    /**
     *
     */
    @Override
    public void deleteAllInBatch() {

    }

    /**
     * @param id
     * @return
     */
    @Override
    public Tag getOne(Long id) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Tag getById(Long id) {
        call("getById");
        return find(id).get();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Tag getReferenceById(Long id) {
        call("getReferenceById");
        return find(id).get();
    }

    /**
     * @param id
     * @return
     */
    private Optional<Tag> find(Long id) {
        return tags.stream().filter(q -> q.getId() == id).findFirst();
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> List<S> findAll(Example<S> example) {
        return null;
    }

    /**
     * @param example
     * @param sort
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    /**
     * @param pageable
     * @return
     */
    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return null;
    }

    /**
     * @param entity
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> S save(S entity) {
        call("save");
        entity.setId(tags.size());
        tags.add(entity);
        return entity;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Tag> findById(Long id) {
        call("findById");
        if (tags.size() > id) {
            return Optional.of(tags.get(Math.toIntExact(id)));
        }

        return null;
    }

    /**
     * @param id
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
    public long count() {
        return tags.size();
    }

    /**
     * @param id
     */
    @Override
    public void deleteById(Long id) {

    }

    /**
     * @param entity
     */
    @Override
    public void delete(Tag entity) {

    }

    /**
     * @param ids
     */
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {

    }

    /**
     * @param entities
     */
    @Override
    public void deleteAll(Iterable<? extends Tag> entities) {

    }

    /**
     *
     */
    @Override
    public void deleteAll() {

    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> Optional<S> findOne(Example<S> example) {
        return null;
    }

    /**
     * @param example
     * @param pageable
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> long count(Example<S> example) {
        return 0;
    }

    /**
     * @param example
     * @param <S>
     * @return
     */
    @Override
    public <S extends Tag> boolean exists(Example<S> example) {
        return false;
    }

    /**
     * @param example
     * @param queryFunction
     * @param <S>
     * @param <R>
     * @return
     */
    @Override
    public <S extends Tag, R> R findBy(Example<S> example,
                                       Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }
}