//package com.igame.work.user.dao;
//
//import java.util.Date;
//
//import org.bson.Document;
//import org.bson.types.ObjectId;
//import org.mongodb.morphia.query.Query;
//
//import com.igame.core.db.AbsDao;
//import com.igame.work.user.dto.Tes;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCursor;
//
//public class TesDAO extends AbsDao {
//
//	@Override
//	public String getTableName() {
//		return "tes";
//	}
//
//    private static final TesDAO domain = new TesDAO();
//
//    public static final TesDAO ins() {
//        return domain;
//    }
//
//    public void store(Tes t){
//    	t.setSid(new ObjectId());
//    	getDatastore(1).save(t);
////    	Document dc = new Document();
////    	dc.put("uid", t.getUid());
////    	dc.put("name", t.getName());
////    	getTable().insertOne(dc);
//
////    	FindIterable<Document> ff = getTable().find();
////    	MongoCursor<Document> it =  ff.iterator();
////    	while(it.hasNext()){
////    		System.err.println(it.next().get("name"));
////    	}
//    	Query<Tes> tts=  getDatastore(1).find(Tes.class);
//    	for(Tes tt : tts.asList()){
//    		System.err.println(tt.getSid());
//    		System.err.println(tt.getUid());
//    		System.err.println(tt.getName());
//		}
//    }
//
//}
