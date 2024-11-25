package org.example;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
public class Lend {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prestamos_id_gen")
    @SequenceGenerator(name = "prestamos_id_gen", sequenceName = "prestamos_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "fechaprestamo", nullable = false)
    private LocalDate fechaprestamo;

    @Column(name = "fechadevolucion")
    private LocalDate fechadevolucion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "libro", nullable = false)
    private Book libro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "usuario", nullable = false)
    private org.example.User usuario;

    @Override
    public String toString() {
        return  "Lend ID: " + this.id + "\n" +
                "Lend Date: " + this.fechaprestamo + "\n" +
                "Return Date: " + this.fechadevolucion + "\n" +
                "Book: " + this.libro + "\n" +
                "User: " + this.usuario;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFechaprestamo() {
        return fechaprestamo;
    }
    public void setFechaprestamo(LocalDate fechaprestamo) {
        this.fechaprestamo = fechaprestamo;
    }

    public LocalDate getFechadevolucion() {
        return fechadevolucion;
    }
    public void setFechadevolucion(LocalDate fechadevolucion) {
        this.fechadevolucion = fechadevolucion;
    }

    public Book getLibro() {
        return libro;
    }
    public void setLibro(Book libro) {
        this.libro = libro;
    }

    public org.example.User getUsuario() {
        return usuario;
    }
    public void setUsuario(org.example.User usuario) {
        this.usuario = usuario;
    }

}