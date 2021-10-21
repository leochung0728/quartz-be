package com.leochung0728.quartz.table;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.leochung0728.quartz.util.SecurityUtils;

@Entity
@Table(name = "WebListEntryMASearch")
@EntityListeners(AuditingEntityListener.class)
public class WebListEntryMASearch {
	@Id
	private String id;

	@Lob
	@Column(nullable = false)
	private String link;

	private String name;

	private String city;

	private String district;

	@CreatedDate
	@Column(updatable = false, nullable = false)
	private Date createDate;

	@LastModifiedDate
	@Column(nullable = false)
	private Date modifyDate;

	public WebListEntryMASearch() {
	}

	public WebListEntryMASearch(String link) {
		this.id = SecurityUtils.sha256(link);
		this.link = link;
	}
	
	public WebListEntryMASearch(String link, String name, String city, String district) {
		this(link);
		this.name = name;
		this.city = city;
		this.district = district;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

}
