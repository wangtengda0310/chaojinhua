package com.igame.core.db;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Marcus.Z
 *
 */
public class BasicVO {
	
	@Id
	@JsonIgnore
	public ObjectId _id;

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	
	

}
