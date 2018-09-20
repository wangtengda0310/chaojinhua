package test;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CBasicVO {
	
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
