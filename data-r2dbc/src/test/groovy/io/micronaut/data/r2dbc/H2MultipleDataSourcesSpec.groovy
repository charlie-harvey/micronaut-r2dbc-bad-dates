package io.micronaut.data.r2dbc

import io.micronaut.context.annotation.Property
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.RxJavaCrudRepository
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

import javax.inject.Inject
import java.time.LocalDateTime

@MicronautTest(rollback = false)
@H2Properties
@Property(name = "r2dbc.datasources.other.url", value = "r2dbc:h2:mem:///otherdb;DB_CLOSE_ON_EXIT=FALSE")
@Property(name = "r2dbc.datasources.other.schema-generate", value = "CREATE_DROP")
@Property(name = "r2dbc.datasources.other.dialect", value = "H2")
class H2MultipleDataSourcesSpec extends Specification {

    @Inject OtherRepository otherRepository
    @Inject H2OwnerWithLocalDateTimeRepository ownerRepository

    void 'test multiple datasources'() {
        when:"An entity is saved in one datasource"
        LocalDateTime ldt = LocalDateTime.now();
        Flux.from(ownerRepository.saveAll([new OwnerWithLocalDateTime("Fred", ldt),
                                           new OwnerWithLocalDateTime("Bob", ldt)])).collectList().block()
        Mono.from(otherRepository.saveAll([new OwnerWithLocalDateTime("Joe", ldt)])).block()

        then:"Only reflected in one"
        otherRepository.findByName("Joe").isPresent()
        !otherRepository.findByName("Fred").isPresent()
        otherRepository.count().blockingGet() == 1
        Mono.from(ownerRepository.count()).block() == 2
    }

    @R2dbcRepository(value = "other", dialect = Dialect.H2)
    static interface OtherRepository extends RxJavaCrudRepository<OwnerWithLocalDateTime, Long> {
        Optional<OwnerWithLocalDateTime> findByName(String name)
    }
}
