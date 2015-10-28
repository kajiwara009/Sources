package com.gmail.yblueycarlo1967.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
/**
 *  狂人用のGameBrain
 * @author info
 *
 */
public class PGameBrain extends SpecialGameBrain {

	public PGameBrain(AbstractRole mine) {
		super(mine);
		//何の役職をCOするかを決める
		//int rnd=new Random().nextInt(3)+1;
		List<Role> fakeRoles = Arrays.asList(Role.MEDIUM);
		fakeRole = fakeRoles.get(new Random().nextInt(fakeRoles.size()));
	}
	public void dayStart(){
		super.dayStart();
		//騙っている場合の処理
		if(fakeRole==Role.VILLAGER) return;
		if(fakeRole==Role.MEDIUM){
			if(mine.getDay()>1){
				Judge judge=new Judge(mine.getDay(),mine.getMe(),gameInfo.getExecutedAgent(),Species.HUMAN);
				fakeJudgeList.add(judge);
			}
		}
		//占い先
		if(fakeRole==Role.SEER){
			if(mine.getDay()>0){
				List<Agent> candidate;
				candidate=this.getGrayAgents();
				candidate.remove(mine.getMe());
				//グレーがいなくなれば、CO者と、自分占った者を除いて占う
				if(candidate.size()==0){
					candidate=this.getAliveAgentsExceptMe();
					candidate.removeAll(this.getCOAgents());
					candidate.removeAll(this.getFakeJudgeTargets());
				}
				//それもいなければ自分を除いて占う
				if(candidate.size()==0){
					candidate=this.getAliveAgentsExceptMe();
				}
				Agent target=randomSelect(candidate);
				Judge judge=null;
				//x回まで黒出し
				//if(this.countBlackResultForJudgeList()<2) judge=new Judge(mine.getDay(),mine.getMe(),target,Species.WEREWOLF);
				judge=new Judge(mine.getDay(),mine.getMe(),target,Species.HUMAN);
				//else judge=new Judge(mine.getDay(),mine.getMe(),target,Species.HUMAN);
				fakeJudgeList.add(judge);
			}
		}
	}

}
