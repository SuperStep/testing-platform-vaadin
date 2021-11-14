package com.sust.testing.platform.backend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class VectorPsychotype extends AbstractEntity implements Cloneable {

    public enum Color {
        Red, Blue, Green
    }
    @NotNull
    @NotEmpty
    private String name;
    private String description;
    private VectorPsychotype.Color color;

    public VectorPsychotype() {
    }
    public VectorPsychotype(String name){
        this.name = name;
    }

    public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }
}
