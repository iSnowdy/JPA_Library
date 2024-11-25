package org.example;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "libros")
public class Book {
    @Id
    @SequenceGenerator(name = "libros_id_gen", sequenceName = "prestamos_id_seq", allocationSize = 1)
    @Column(name = "isbn", nullable = false, length = 13)
    private String isbn;

    @Column(name = "titulo", nullable = false, length = 90)
    private String titulo;

    @ColumnDefault("1")
    @Column(name = "copias")
    private Integer copias;

    @Column(name = "editorial", length = 60)
    private String editorial;

    @OneToMany(mappedBy = "libro")
    private Set<org.example.Lend> prestamos = new LinkedHashSet<>();

    @Override
    public String toString() {
        return  "ISBN: " + this.isbn + "\n" +
                "Title: " + this.titulo + "\n" +
                "Copies: " + this.copias + "\n" +
                "Publisher: " + this.editorial;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getCopias() {
        return copias;
    }
    public void setCopias(Integer copias) {
        this.copias = copias;
    }

    public String getEditorial() {
        return editorial;
    }
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public Set<org.example.Lend> getPrestamos() {
        return prestamos;
    }
    public void setPrestamos(Set<org.example.Lend> prestamos) {
        this.prestamos = prestamos;
    }

}