package io.micronaut.data.r2dbc;

import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.r2dbc.annotation.R2dbcRepository;
import io.micronaut.data.r2dbc.operations.R2dbcOperations;
import io.micronaut.data.repository.reactive.ReactiveStreamsCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

@R2dbcRepository(dialect = Dialect.H2)
abstract class H2OwnerWithLocalDateTimeRepository implements ReactiveStreamsCrudRepository<OwnerWithLocalDateTime, Long> {

    private final H2PetWithDateRepository petRepository;
    private final R2dbcOperations operations;
    private final LocalDateTime ldt = LocalDateTime.now();
    private final Date dt = Date.from(ldt.toInstant(ZoneOffset.UTC));

    protected H2OwnerWithLocalDateTimeRepository(H2PetWithDateRepository petRepository, R2dbcOperations operations) {
        this.petRepository = petRepository;
        this.operations = operations;
    }


    @Transactional
    public Mono<Void> setupData() {
        return Mono.from(operations.withTransaction(status ->
                Flux.from(save(new OwnerWithLocalDateTime("Fred", ldt)))
                .flatMap(owner ->
                        petRepository.saveAll(Arrays.asList(
                                new PetWithDate("Dino", dt, owner),
                                new PetWithDate("Hoppy", dt, owner)
                        ))
                ).then(Flux.from(save(new OwnerWithLocalDateTime("Barney", ldt)))
                        .flatMap(owner ->
                                petRepository.save(new PetWithDate("Rabbid", dt, owner))
                        ).then()
                )));
    }

    @Transactional
    public Mono<Void> testSetRollbackOnly() {
        return Mono.from(operations.withTransaction(status ->
                Flux.from(save(new OwnerWithLocalDateTime("Fred", ldt)))
                        .flatMap(owner ->
                                petRepository.saveAll(Arrays.asList(
                                        new PetWithDate("Dino", dt, owner),
                                        new PetWithDate("Hoppy", dt, owner)
                                ))
                        ).then(Flux.from(save(new OwnerWithLocalDateTime("Barney", ldt)))
                        .flatMap(owner ->
                                petRepository.save(new PetWithDate("Rabbid", dt, owner))
                        ).map(pet -> {
                            status.setRollbackOnly();
                            return pet;
                        }).then()
                )));
    }


    @Transactional
    public Mono<Void> testRollbackOnException() {
        return Flux.from(save(new OwnerWithLocalDateTime("Fred", ldt)))
                        .flatMap(owner ->
                                petRepository.saveAll(Arrays.asList(
                                        new PetWithDate("Dino", dt, owner),
                                        new PetWithDate("Hoppy", dt, owner)
                                ))
                        ).then(Flux.from(save(new OwnerWithLocalDateTime("Barney", ldt)))
                        .flatMap(owner ->
                                petRepository.save(new PetWithDate("Rabbid", dt, owner))
                        ).flatMap(pet -> Mono.error(new RuntimeException("Something bad happened"))).then()
                );
    }
}