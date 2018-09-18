package com.igame.work.fight.service;

import com.igame.work.fight.dto.FightBase;
import com.igame.work.monster.dto.Monster;


/**
 * 
 * @author Marcus.Z
 *
 */
public class EndFightTask implements Runnable {
	
	private FightBase fb;
	
	public EndFightTask(){
		
	}
	
	
	public EndFightTask(FightBase fb) {
		super();
		this.fb = fb;
	}



	@Override
	public void run() {
		fb.scBuffers.clear();
		for(Monster mm : fb.getFightA().monsters.values()){
			mm.getFightProp().hotList.clear();
			mm.getFightProp().effectList.clear();			
		}
		for(Monster mm : fb.getFightB().monsters.values()){
			mm.getFightProp().hotList.clear();
			mm.getFightProp().effectList.clear();			
		}
		PVPFightService.ins().fights.remove(fb.getId());
		fb.fightA.getPlayer().setFightBase(null);
		if(fb.fightB.getPlayer() != null){
			fb.fightB.getPlayer().setFightBase(null);
		}	

	}

}
