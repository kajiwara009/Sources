package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Species;

public class JudgeInfo {
	Agent agent;
	ListMap<Judge,Integer> judgelist;
	int day;
	boolean safe;
	
	
	public JudgeInfo(Agent a,int d){
		agent=a;
		judgelist=new ListMap<Judge,Integer>();
		day=(d>0)?d:1;
		safe=true;
	}
	
	//NOTE:Talk��day��-1�ɂȂ��Ă��邩�炢�̌��ʂ��킩��Ȃ�
	//�Ȃ���talkindex������������ČJ��Ԃ��ǂ܂��Ƃ�������	
	public void put(int day,Agent p,Species s,int count){
		//���łɌ��ʂ������Ă���Ȃ疳������
		for(Judge j : judgelist.keyList()){
			if(j.getTarget()==p){
				safe=false;
				return;
			}
		}
		judgelist.put(new Judge(day,agent,p,s),count);
		
	}
	
	public Agent getAgent(){
		return agent;
	}
	
	
	public Colour getColour(Agent target){
		for(Judge j : judgelist.keyList()){
			if(j.getTarget().equals(target)){
				return j.getResult()==Species.HUMAN? Colour.WHITE : Colour.BLACK;
			}
			
		}
		return Colour.GREY;
	}
	
	public List<Judge> getJudgeList(){
		return judgelist.keyList();
	}
	
	public int getDay(){
		return day;
	}
	
	public int size(){
		return judgelist.size();
	}
	
	public int numBlack(){
		int ret=0;
		for(Judge j : judgelist.keyList()){
			if(j.getResult()==Species.WEREWOLF){
				ret++;
			}
		}
		return ret;
	}
	
	public double avrCount(){
		double ret=0;
		for(int i : judgelist.valueList()){
			ret+=i;
		}
		if(ret==0){
			return 0;
		}
		return ret/judgelist.size();
	}

}
