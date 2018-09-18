package com.igame.core;


import com.google.common.collect.Sets;
import com.igame.core.db.BasicDto;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import java.util.Set;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "SystemService", noClassnameStored = true)
public class SystemServiceDto extends BasicDto {
	{

		set_id(new ObjectId("5ad950a8f9745d21386f1a18"));
	}
    private Set<Long> clock = Sets.newHashSet();//0clock
    

	public Set<Long> getClock() {
		return clock;
	}

	public void setClock(Set<Long> clock) {
		this.clock = clock;
	}
	

}
