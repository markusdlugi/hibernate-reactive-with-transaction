package com.example;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "books")
public class Book implements Serializable {
    @Id @GeneratedValue
    private Integer id;

    @Size(min = 13, max = 13)
    private String isbn;

    @NotNull @Size(max = 100)
    private String title;

    @Basic(fetch = EAGER)
    @NotNull @Past
    private LocalDate published;

    @NotNull
    @ManyToOne(fetch = EAGER)
    @JsonIgnore
    private Author author;

    public Book(String isbn, String title, Author author, LocalDate published) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.published = published;
    }

    public Book() {
    }

    public Integer getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public LocalDate getPublished() {
        return published;
    }
}
