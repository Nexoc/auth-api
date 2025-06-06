package org.fhmdb.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "The name field can't be blank")
    private String name;

    @NotBlank(message = "The email field can't be blank")
    @Email(message = "Please enter email in proper format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "The password field can't be blank")
    @Size(min = 5, message = "The password must be at least 5 characters")
    private String password;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(nullable = true)
    private String provider; // e.g. "github", "google", "local"

    @Column(nullable = true)
    private Boolean oauthUser; // nullable Boolean for builder compatibility

    @Column(unique = true, nullable = true)
    private Long faceId;

    // Spring Security methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isOauthUser() {
        return Boolean.TRUE.equals(oauthUser);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

}
