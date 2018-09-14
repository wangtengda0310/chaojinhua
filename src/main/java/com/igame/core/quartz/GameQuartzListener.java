package com.igame.core.quartz;


import com.igame.core.log.ExceptionLog;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GameQuartzListener {

	public GameQuartzListener(){}

	public void minute(){
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.minute();
			}catch (Exception e){
				ExceptionLog.error("minute");
				e.printStackTrace();
			}
		});
    }

    public void minute5(){

        JobManager.listeners.values().forEach(jobListener -> {
            try{

                jobListener.minute5();
            }catch (Exception e){
                ExceptionLog.error("minute5");
                e.printStackTrace();
            }
        });


    }


	public void minute180(){

		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.minute180();
			}catch (Exception e){
				ExceptionLog.error("minute180");
				e.printStackTrace();
			}
		});
	}

    //零点执行
    public void zero(){
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.zero();
			}catch (Exception e){
				ExceptionLog.error("zero");
				e.printStackTrace();
			}
		});
    }

	//九点执行
	public void nine(){
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				//刷新所有在线玩家的一般商店
				jobListener.nine();
			}catch (Exception e){
				ExceptionLog.error("nine");
				e.printStackTrace();
			}
		});
	}

	//十二点执行
	public void twelve(){
		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.twelve();
			}catch (Exception e){
				ExceptionLog.error("twelve");
				e.printStackTrace();
			}
		});
	}

	//十四点执行
	public void fourteen(){

		JobManager.listeners.values().forEach(jobListener -> {
			try{

				jobListener.fourteen();
			}catch (Exception e){
				ExceptionLog.error("fourteen");
				e.printStackTrace();
			}
		});
	}

	//二十点执行
	public void twenty(){

		JobManager.listeners.values().forEach(jobListener -> {
			try{
				jobListener.twenty();
			}catch (Exception e){
				ExceptionLog.error("twenty");
				e.printStackTrace();
			}
		});
	}

	//每周一执行
	public void week7(){

    	JobManager.listeners.values().forEach(jobListener -> {
			try{
				jobListener.week7();
			}catch (Exception e){
				ExceptionLog.error("week7");
				e.printStackTrace();
			}
		});

	}


}
