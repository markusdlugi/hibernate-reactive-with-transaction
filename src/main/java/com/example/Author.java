package com.example;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "authors")
public class Author implements Serializable {
    @Id @GeneratedValue
    private Integer id;

    @NotNull @Size(max = 100)
    private String name;

    @OneToMany(mappedBy = "author", cascade = PERSIST, fetch = EAGER)
    private List<Book> books = new ArrayList<>();

    public Author(String name) {
        this.name = name;
    }

    public Author() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }
}
