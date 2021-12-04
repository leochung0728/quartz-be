package com.leochung0728.quartz.table.key;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StockTransactionKeys implements Serializable {
	private String isinCode;
	private Date date;

	public StockTransactionKeys(String isinCode, Date date) {
		this.isinCode = isinCode;
		this.date = date;
	}
}
