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

    @GET
    public Uni<Author> doStuff() {
        final Author author = new Author("John Tolkien");
        final Book book1 = new Book("1234567890123", "Book1", author, LocalDate.now().minus(100, ChronoUnit.DAYS));
        final Book book2 = new Book("5678901234567", "Book2", author, LocalDate.now().minus(100, ChronoUnit.DAYS));
        author.getBooks().addAll(Arrays.asList(book1, book2));
        return sessionFactory
                .withTransaction((session, tx) -> sessionFactory
                        .withSession(session1 -> session1.persist(author).chain(session::flush) //
                                .onItem().transformToUni(x -> sessionFactory //
                                        .withSession(
                                                session2 -> session2
                                                        .find(session2.createEntityGraph(Author.class),
                                                                author.getId()) //
                                                        .onItem().transformToUni(author2 -> {
                                                            author2.setName("John Ronald Reuel Tolkien");
                                                            return session2.merge(author2).chain(session::flush);
                                                        })))) //
                        .onItem().transformToUni(
                                y -> sessionFactory.withSession(session3 ->
                                        session3.find(Author.class, author.getId()))));
    }
}
