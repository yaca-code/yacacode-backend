package com.ph.ponto.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "seq_users")
	@SequenceGenerator(name = "seq_users", allocationSize = 1,sequenceName = "seq_users")
	private Long id;

    @NotNull(message = "Name may not be null")
    private String name;

    @NotNull(message = "Password may not be null")
    private String password;

    @NotNull(message = "Hierarchy may not be null")
    private Integer hierarchy;

    @Value("${some.key:true}")
    private Boolean active;

    private String image;

    private Date created_at;

    private Date deleted_at;

    private Integer extras;
    
}