package com.shopqr.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Role role; // ADMIN, MANAGER, USER
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    private boolean isApproved = false;
    
    public enum Role {
        ADMIN, MANAGER, USER
    }
}
