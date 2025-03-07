package com.dhia.Upvertise.models.user;


import com.dhia.Upvertise.models.common.BaseEntity;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

//@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
//@Table(name = "_user")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
//This column "role" determines which subclass an entity belongs to.
public abstract class User extends BaseEntity  {

    private String firstName;
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;


}
