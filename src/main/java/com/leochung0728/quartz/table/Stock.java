package com.leochung0728.quartz.table;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true)
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
	@NaturalId
	@Column()
	@ToString.Include
	private String stockCode;
	// 有價證券名稱
	@Column()
	private String stockName;
	// 市場別
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	@JoinColumn()
	private StockMarketType marketType;
	// 有價證券別
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	@JoinColumn()
	private StockIssueType issueType;
	// 產業別
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
	@JoinColumn()
	private StockIndustryType industryType;
	// 發行日
	@Column()
	private Date releaseDate;
	// CFI Code
	@Column()
	private String cfiCode;
	// 錯誤次數
	@Column(nullable = false, columnDefinition = "int default 0")
	private int errCount;
	// 錯誤訊息
	@Column()
	private String errMsg;
	
	@Version
	private long version;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;
	
	@OneToMany(mappedBy = "isinCode", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<StockTransaction> stockTransactions;

	public Stock(String isinCode, String stockCode, String stockName, StockMarketType marketType,
			StockIssueType issueType, StockIndustryType industryType, Date releaseDate, String cfiCode) {
		this.isinCode = isinCode;
		this.stockCode = stockCode;
		this.stockName = stockName;
		this.marketType = marketType;
		this.issueType = issueType;
		this.industryType = industryType;
		this.releaseDate = releaseDate;
		this.cfiCode = cfiCode;
	}

}
