package pl.put.photo360.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "roles" )
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleEntity implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( nullable = false, unique = true )
    private String name;

    @Column
    private String description;
}
