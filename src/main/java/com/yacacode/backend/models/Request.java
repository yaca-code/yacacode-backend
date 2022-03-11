package com.yacacode.backend.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="requests")
public class Request {

    @Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "seq_scores")
	@SequenceGenerator(name = "seq_scores", allocationSize = 1,sequenceName = "seq_scores")
	private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id")
    private Score score_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user_id;

    private Date input;

    private Date out_lunch;

    private Date lunch_entree;

    private Date exit;

    private String description;

    private Boolean active;

    private Date created_at;

    private Date deleted_at;

}
