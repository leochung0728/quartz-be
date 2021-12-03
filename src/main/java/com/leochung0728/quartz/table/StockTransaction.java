package com.leochung0728.quartz.table;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Entity
@IdClass(StockTransaction.CompositeKeys.class)
@Table(name = "stock_transaction")
@EntityListeners(AuditingEntityListener.class)
public class StockTransaction {
	// 有價證券代號
	@Id
	@Column(name = "isin_code")
	private String isinCode;
	// 日期
	@Id
	private Date date;
	// 開盤
	private Double open;
	// 最高
	private Double high;
	// 最低
	private Double low;
	// 收盤
	private Double close;
	// 收盤(adj)
	private Double adjColse;
	// 成交量
	private Long volume;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;
	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, optional = false)
	@JoinColumn(name = "isin_code", insertable = false, updatable = false)
	private Stock stock;

	public StockTransaction(String isinCode, Date date, Double open, Double high, Double low, Double close,
							Double adjColse, Long volume) {
		this.isinCode = isinCode;
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.adjColse = adjColse;
		this.volume = volume;
	}

	@ToString
	@Getter
	@Setter
	@EqualsAndHashCode
	public class CompositeKeys implements Serializable {
		private String isinCode;
		private Date date;

		public CompositeKeys() {}

		public CompositeKeys(String isinCode, Date date) {
			this.isinCode = isinCode;
			this.date = date;
		}
	}

}
