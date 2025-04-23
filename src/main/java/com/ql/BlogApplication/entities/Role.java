package com.ql.BlogApplication.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @JsonManagedReference
    @OneToMany(mappedBy = "role", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

    @JsonManagedReference
    @OneToMany(mappedBy = "role", cascade=CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles;

}
