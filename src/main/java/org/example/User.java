package org.example;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class User {
    @Id
    @SequenceGenerator(name = "usuarios_id_gen", sequenceName = "prestamos_id_seq", allocationSize = 1)
    @Column(name = "codigo", nullable = false, length = 8)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 25)
    private String nombre;

    @Column(name = "apellidos", nullable = false, length = 25)
    private String apellidos;

    @Column(name = "fechanacimiento")
    private LocalDate fechanacimiento;

    @OneToMany(mappedBy = "usuario")
    private Set<Lend> prestamos = new LinkedHashSet<>();

    @Override
    public String toString() {
        return  "Code: " + this.codigo +
                "\nName: " + this.nombre +
                "\nSurnames: " + this.apellidos +
                "\nDate of Birth: " + this.fechanacimiento.toString();
    }

    // Getters and Setters
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechanacimiento() {
        return fechanacimiento;
    }
    public void setFechanacimiento(LocalDate fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public Set<Lend> getPrestamos() {
        return prestamos;
    }
    public void setPrestamos(Set<Lend> prestamos) {
        this.prestamos = prestamos;
    }

}