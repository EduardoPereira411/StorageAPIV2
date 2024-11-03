package com.StorageAPI.ItemManagement.model;



import com.StorageAPI.CategoryManagement.model.Category;
import com.StorageAPI.Exceptions.ConflictException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Item {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemPk;

    @Version
    @Getter
    private long version;

    @Getter
    @Setter
    private LocalDate lastModifiedAt;

    @Column(nullable = false, unique = true)
    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Getter
    private int amount;

    @Getter
    @Setter
    private boolean isFavorite;

    @Getter
    private int updateFrequency;

    @Getter
    @Setter
    private boolean needsCheckup;

    @Size(min = 0, max = 2048)
    @Getter
    @Setter
    private String description;

    @Size(min = 0, max = 500)
    @Getter
    @Setter
    private String image;

    @ManyToOne
    @Getter
    @Setter
    private Category category;

    public Item(){
        setLastModifiedAt(LocalDate.now());
    }

    public Item(final String name, final Category category){
        setName(name);
        setCategory(category);
        setLastModifiedAt(LocalDate.now());
    }

    public Item(final String name, final String description,final Category category){
        setName(name);
        setDescription(description);
        setCategory(category);
        setLastModifiedAt(LocalDate.now());
    }

    public Item(final String name, final Integer updateFrequency,final Category category){
        setName(name);
        if(updateFrequency!=null){
            setUpdateFrequency(updateFrequency);
        }
        setCategory(category);
        setLastModifiedAt(LocalDate.now());
    }

    public Item(final String name, final Integer updateFrequency,final Category category, final String description){
        setName(name);
        if(updateFrequency!=null){
            setUpdateFrequency(updateFrequency);
        }
        setDescription(description);
        setCategory(category);
        setLastModifiedAt(LocalDate.now());
    }

    public void updateAmount(final long desiredVersion, final int amount) {
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(amount>=0) {
            this.amount = amount;
            setLastModifiedAt(LocalDate.now());
        }else{
            throw new DataIntegrityViolationException("The amount cannot be below '0' ");
        }
    }

    public void updateIsFavorite(final long desiredVersion, final Boolean isFavorite) {
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(isFavorite!=null) {
            setFavorite(isFavorite);
        }else{
            throw new DataIntegrityViolationException("Favorite must be true or false ");
        }
    }

    public void setUpdateFrequency(final Integer updateFrequency){
        if(updateFrequency!=null) {
            if (updateFrequency < 0) {
                throw new DataIntegrityViolationException("The update frequency cannot be below '0' ");
            }
            this.updateFrequency= updateFrequency;
        }

    }

    public void toggleFavorite(final long desiredVersion) {
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        setFavorite(!isFavorite());
    }

    public void updateItem(final long desiredVersion, final String name, final String description, final Category category, final Boolean isFavorite, final Integer updateFrequency, final String image){
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(name!=null){
            setName(name);
        }
        if(description!=null){
            setDescription(description);
        }
        if(category!=null){
            setCategory(category);
        }
        if(isFavorite!=null) {
            setFavorite(isFavorite);
        }
        if(updateFrequency!=null){
            setUpdateFrequency(updateFrequency);
        }
        if(image!=null){
            setImage(image);
        }
    }

    public void updateImage(final long desiredVersion, final String imageURI){
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(imageURI!=null){
            setImage(imageURI);
        }
    }

    public void refreshLastModified(){
        setLastModifiedAt(LocalDate.now());
    }

    public boolean needsCheckup() {
        if(updateFrequency !=0) {
            LocalDate today = LocalDate.now();
            return ChronoUnit.DAYS.between(lastModifiedAt, today) >= updateFrequency;
        }
        return false;
    }

    public void editUpdateFrequency(final long desiredVersion,final Integer updateFrequency){
        if (this.version != desiredVersion) {
            throw new ConflictException("Object was modified by another user");
        }
        if(updateFrequency!=null) {
            setUpdateFrequency(updateFrequency);
        }
    }
}
