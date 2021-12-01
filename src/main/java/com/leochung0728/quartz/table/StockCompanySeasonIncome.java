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
@IdClass(StockCompanySeasonIncome.CompositeKeys.class)
@Table(name = "stock_company_season_income")
@EntityListeners(AuditingEntityListener.class)
public class StockCompanySeasonIncome {
	// 有價證券代號
	@Id
	@Column(name = "stock_code")
	private String stockCode;
	// 年
	@Id
	private int year;
	// 季
	@Id
	private int season;

	// 每股盈餘
	private Double eps;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

	public StockCompanySeasonIncome(String stockCode, int year, int season, Double eps) {
		this.stockCode = stockCode;
		this.year = year;
		this.season = season;
		this.eps = eps;
	}

	@ToString
	@Getter
	@Setter
	@NoArgsConstructor
	public static class CompositeKeys implements Serializable {
		private String stockCode;
		private int year;
		private int season;

		public CompositeKeys(String stockCode, int year, int season) {
			this.stockCode = stockCode;
			this.year = year;
			this.season = season;
		}
	}
}


