package com.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/test")
@PermitAll
public class TestResource {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @Inject
    @RestClient
    SomeApi someApi;

    @GET
    public Uni<Author> doStuff() {
        final Author author = new Author("John Tolkien");
        final Book book1 = new Book("1234567890123", "Book1", author, LocalDate.now().minus(100, ChronoUnit.DAYS));
        final Book book2 = new Book("5678901234567", "Book2", author, LocalDate.now().minus(100, ChronoUnit.DAYS));
        author.getBooks().addAll(Arrays.asList(book1, book2));

        return sessionFactory
                .withTransaction((session, tx) -> sessionFactory
                        // Persist author + books
                        .withSession(session1 -> session1.persist(author).chain(session::flush))

                        // REST Call using Reactive REST Client
                        .onItem().call(() -> someApi.doSomething())

                        // Try to update author (in same transaction)
                        .onItem().transformToUni(ignore -> sessionFactory.withSession(session2 ->
                                session2.find(Author.class, author.getId())
                                        .onItem().ifNull().failWith(new IllegalStateException("Couldn't find author " + author.getId()))
                                        .onItem().ifNotNull().transformToUni(author2 -> {
                                            author2.setName("John Ronald Reuel Tolkien");
                                            return session2.merge(author2);
                                        }))))

                // Retrieve item from database (after transaction finishes)
                .onItem().transformToUni(ignore -> sessionFactory.withSession(session3 ->
                        session3.find(Author.class, author.getId())));
    }
}
