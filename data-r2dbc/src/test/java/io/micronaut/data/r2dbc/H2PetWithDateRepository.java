package io.micronaut.data.r2dbc;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@R2dbcRepository(dialect = Dialect.H2)
interface H2PetWithDateRepository extends ReactiveStreamsCrudRepository<PetWithDate, Long> {

    @Join("owner")
    Mono<PetWithDate> findByName(String name);

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    <S extends PetWithDate> Publisher<S> save(@Valid @NotNull S entity);

    @Transactional(Transactional.TxType.MANDATORY)
    @Override
    <S extends PetWithDate> Publisher<S> saveAll( @Valid @NotNull Iterable<S> entities);
}
