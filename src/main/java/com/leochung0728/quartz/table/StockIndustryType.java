package com.leochung0728.quartz.table;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stock_industry_type")
@EntityListeners(AuditingEntityListener.class)
public class StockIndustryType {
	@Id
	@Column()
	private String name;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;
	
	@OneToMany(mappedBy = "industryType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Stock> industryType;

	public StockIndustryType(String name) {
		this.name = name;
	}
}
