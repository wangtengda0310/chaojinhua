//package com.igame.work.user.dto;
//
//
//import org.bson.types.ObjectId;
//import org.mongodb.morphia.annotations.Entity;
//import org.mongodb.morphia.annotations.Indexed;
//import org.mongodb.morphia.utils.IndexDirection;
//
//import com.igame.core.db.BasicDto;
//
//
//
///**
// *
// * @author Marcus.Z
// *
// */
//@Entity(value = "tes", noClassnameStored = true)
//public class Tes extends BasicDto {
//
//
//	@Indexed(value= IndexDirection.ASC,  unique = true)
//	private int uid;
//
//	private ObjectId sid;
//
//    private String name;
//
//    public Tes(){}
//
//
//
//
//
//	public Tes(int uid, String name) {
//		super();
//		this.uid = uid;
//		this.name = name;
//
//	}
//
//	public int getUid() {
//		return uid;
//	}
//
//	public void setUid(int uid) {
//		this.uid = uid;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//
//
//
//
//	public ObjectId getSid() {
//		return sid;
//	}
//
//
//
//
//
//	public void setSid(ObjectId sid) {
//		this.sid = sid;
//	}
//
//
//
//
//}
