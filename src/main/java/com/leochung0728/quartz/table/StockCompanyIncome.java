package com.leochung0728.quartz.table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(StockCompanyIncome.CompositeKeys.class)
@Table(name = "stock_company_income")
@EntityListeners(AuditingEntityListener.class)
public class StockCompanyIncome {
	// 有價證券代號
	@Id
	@Column(name = "stock_code")
	private String stockCode;
	// 年
	@Id
	private int year;
	// 月
	@Id
	private int month;

	// 當月營收(千元)
	private Double income;
	// 上月營收(千元)
	private Double lastMonthIncome;
	// 去年當月營收(千元)
	private Double lastYearIncome;
	// 上月比較增減(%)
	private Double lastMonthIncreaseRatio;
	// 去年同月增減(%)
	private Double lastYearIncreaseRatio;
	// 當月累計營收(千元)
	private Double cumulativeIncome;
	// 去年累計營收(千元)
	private Double lastYearCumulativeIncome;
	// 去年累計增減(%)
	private Double lastYearCumulativeIncreaseRatio;
	// 備註
	private String remark;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, optional = false)
	@JoinColumn(name = "stock_code", referencedColumnName = "stockCode", insertable = false, updatable = false)
	private Stock stock;

	public StockCompanyIncome(String stockCode, int year, int month, Double income, Double lastMonthIncome,
							  Double lastYearIncome, Double lastMonthIncreaseRatio, Double lastYearIncreaseRatio, Double cumulativeIncome,
							  Double lastYearCumulativeIncome, Double lastYearCumulativeIncreaseRatio, String remark) {
		this.stockCode = stockCode;
		this.year = year;
		this.month = month;
		this.income = income;
		this.lastMonthIncome = lastMonthIncome;
		this.lastYearIncome = lastYearIncome;
		this.lastMonthIncreaseRatio = lastMonthIncreaseRatio;
		this.lastYearIncreaseRatio = lastYearIncreaseRatio;
		this.cumulativeIncome = cumulativeIncome;
		this.lastYearCumulativeIncome = lastYearCumulativeIncome;
		this.lastYearCumulativeIncreaseRatio = lastYearCumulativeIncreaseRatio;
		this.remark = remark;
	}

	@ToString
	@Getter
	@Setter
	@NoArgsConstructor
	public class CompositeKeys implements Serializable {
		private String stockCode;
		private int year;
		private int month;

		public CompositeKeys(String stockCode, int year, int month) {
			this.stockCode = stockCode;
			this.year = year;
			this.month = month;
		}
	}
}


