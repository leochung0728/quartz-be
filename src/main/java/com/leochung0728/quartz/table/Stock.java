package com.leochung0728.quartz.table;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "stock")
@EntityListeners(AuditingEntityListener.class)
public class Stock {
	// 國際證券代碼
	@Id
	@Column()
	private String isinCode;
	// 有價證券代號
	@Column()
	private String stockCode;
	// 有價證券名稱
	@Column()
	private String stockName;
	// 市場別
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn()
	private StockMarketType marketType;
	// 有價證券別
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn()
	private StockIssueType issueType;
	// 產業別
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn()
	private StockIndustryType industryType;
	// 發行日
	@Column()
	private Date releaseDate;
	// CFI Code
	@Column()
	private String cfiCode;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

}
