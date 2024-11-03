package com.StorageAPI.CategoryManagement.model;


import com.StorageAPI.Exceptions.ConflictException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Category {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long catPk;

    @Version
    @Getter
    private long version;

    @Column(nullable = false, unique = true)
    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @Getter
    @Setter
    private String name;

    @Column(nullable = true)
    @Size(min = 0, max = 2048)
    @Getter
    @Setter
    private String description;

    public Category(){}

    public Category(String name, String description){
        setName(name);
        setDescription(description);
    }

    public void update(long desiredVersion,String name, String description){
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(name!=null){
            setName(name);
        }
        if(description!=null){
            setDescription(description);
        }
    }
}
