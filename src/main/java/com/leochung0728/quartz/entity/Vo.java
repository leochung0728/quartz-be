package com.leochung0728.quartz.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vo<E> implements Serializable {
	private static final long serialVersionUID = 2981504616892010774L;
	
	private boolean isSucc;
	private E data;
	private String msg;

	public static <E> Vo<E> failure(String msg) {
		return new Vo<E>(false, null, msg);
	}

	public static <E> Vo<E> failure(Exception e) {
		return new Vo<E>(false, null, e.getMessage());
	}

	public static <E> Vo<E> failure() {
		return new Vo<E>(false, null, null);
	}

	public static <E> Vo<E> success() {
		return new Vo<E>(true, null, null);
	}

	public static <E> Vo<E> success(String msg) {
		return new Vo<E>(true, null, msg);
	}
	
	public static <E> Vo<E> success(E data, String msg) {
		return new Vo<E>(true, data, msg);
	}

	public void setData(E data) {
		this.data = data;
	}
}
