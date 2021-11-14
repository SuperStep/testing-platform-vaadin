package com.sust.testing.platform.backend.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Settings extends AbstractEntity implements Cloneable {

    @NotNull
    @NotEmpty
    private String key;

    @NotNull
    @NotEmpty
    private String value;

}
