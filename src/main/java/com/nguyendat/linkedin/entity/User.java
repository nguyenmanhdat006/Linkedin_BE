package com.nguyendat.linkedin.entity;

import com.nguyendat.linkedin.entity.enums.EmploymentType;
import com.nguyendat.linkedin.entity.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Index;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_full_name", columnList = "full_name"),
    @Index(name = "idx_users_location", columnList = "location")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // AUTHENTICATION 
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String firstname;
    
    private String lastname;
    
    private boolean enabled;
    
    @Column(name = "verification_code")
    private String verificationCode;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    // PROFILE 
    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;
    
    @Column(length = 500)
    private String headline;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(name = "banner_url")
    private String bannerUrl;
    
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String about;
    
    private String phone;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    private String website;
    
    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;
    
    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;
    
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Experience> experiences = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Education> educations = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "user_skills",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();
    
    @OneToMany(mappedBy = "requester")
    @Builder.Default
    private List<Connection> sentConnections = new ArrayList<>();
    
    @OneToMany(mappedBy = "receiver")
    @Builder.Default
    private List<Connection> receivedConnections = new ArrayList<>();
    
    @OneToMany(mappedBy = "follower")
    @Builder.Default
    private List<Follow> following = new ArrayList<>();
    
    @OneToMany(mappedBy = "following")
    @Builder.Default
    private List<Follow> followers = new ArrayList<>();
    
    // HELPER METHODS
    
    public String getFullName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        return (firstname != null ? firstname : "") + " " + (lastname != null ? lastname : "");
    }
    
    public void addExperience(Experience experience) {
        experiences.add(experience);
        experience.setUser(this);
    }
    
    public void removeExperience(Experience experience) {
        experiences.remove(experience);
        experience.setUser(null);
    }
    
    public void addEducation(Education education) {
        educations.add(education);
        education.setUser(this);
    }
    
    public void removeEducation(Education education) {
        educations.remove(education);
        education.setUser(null);
    }
    
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}