package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;



public class LineInfo {

	private JudgeInfo si;
	private JudgeInfo mi;
	private double score;
	private ListMap<Agent,Double> clist;
	
	public LineInfo(JudgeInfo seer,JudgeInfo medium,List<PlayerInfo> pis,int s_num,int m_num){
		si=seer;
		mi=medium;
		clist=new ListMap<Agent,Double>(16);
		score=-1;
		init(pis,s_num,m_num);
		//役がかけているときは信頼度を低くする
		if(mi==null){
			score*=1.0/pis.size();
		}
		if(si==null){
			score*=1.0/pis.size();
		}
		
	}
	
	
	public void init(List<PlayerInfo> plist,int s_num,int m_num){
		List<Agent> nonelist=new ArrayList<Agent>();
		List<Agent> deadlist=new ArrayList<Agent>();
		int seer_liar=0;
		int medium_liar=0;
		int max_black=0;
		int min_black=0;
		int black_num=0;
		int black_dead=0;
		int black_liar=0;
		int white_liar=0;
		int vited_liar=0;
		double seer_certain=0;
		double medium_certain=0;
		int temp;
		
		//役職は固定
		black_liar+=3;
		white_liar+=1;
		seer_liar=s_num-1;
		medium_liar=m_num-1;
		
		
		for(PlayerInfo pi : plist){
			Colour cm=Colour.GREY;
			Colour cs=Colour.GREY;
			Colour cd=Colour.GREY;
			Colour c=Colour.GREY;
			
			if(pi.getRole().equals(Role.SEER)){
				if(si==null){
					clist.put(pi.getAgent(),seer_certain);
					//c=Colour.NONE;
					continue;
				}
				else if(pi.getAgent().equals(si.getAgent())){
					clist.put(pi.getAgent(),1.0);
					c=Colour.WHITE;
				}
				else{
					clist.put(pi.getAgent(),seer_certain);
					if(pi.isVited()){
						vited_liar++;
					}
					else if(pi.isVoted()){
						if(mi!=null && mi.getColour(pi.getAgent())==Colour.WHITE){
							vited_liar++;
						}
					}
					//c=Colour.NONE;
					continue;
				}
				//continue;
			}
			else if(pi.getRole().equals(Role.MEDIUM)){
				if(mi==null){
					clist.put(pi.getAgent(),medium_certain);
					//c=Colour.NONE;
					continue;
				}
				else if(pi.getAgent().equals(mi.getAgent())){
					clist.put(pi.getAgent(),1.0);
					c=Colour.WHITE;
				}
				else{
					clist.put(pi.getAgent(),medium_certain);
					if(pi.isVited()){
						vited_liar++;
					}
					else if(pi.isVoted()){
						if(mi!=null && mi.getColour(pi.getAgent())==Colour.WHITE){
							vited_liar++;
						}
					}
					//c=Colour.NONE;
					continue;
				}
				//continue;
			}
			
			
			
			
			if(mi!=null){
				cm=mi.getColour(pi.getAgent());
			}
			
			if(si!=null){
				cs=si.getColour(pi.getAgent());
			}
			
			
			if(pi.isVited()){
				cd=Colour.WHITE;
			}
			else{
				cd=Colour.GREY;
			}
			
			
			c=c.join(cs);
			c=c.join(cm);
			c=c.join(cd);
			switch(c){
			case GREY:
				if(pi.isAlive()){
					nonelist.add(pi.getAgent());
				}
				else{
					deadlist.add(pi.getAgent());
				}
				break;
			case WHITE:
				clist.put(pi.getAgent(),1.0);
				break;
			case BLACK:
				clist.put(pi.getAgent(),0.0);
				black_num++;
				if(pi.isDead()){
					black_dead++;
				}
				break;
			default:
				clist.put(pi.getAgent(),0.0);
				score=0;
				break;
				
			}
			
			
		}
		
		
		
		temp=(seer_liar+medium_liar)-white_liar;
		if(temp < 0){
			max_black=black_liar-black_num;
		}
		else{
			max_black=black_liar-temp-black_num;
		}

		temp=(seer_liar+medium_liar);
		if(temp < 0){
			min_black=black_liar-black_num;
		}
		else{
			min_black=black_liar-temp-black_num;
		}

		//破綻
		//見つけた黒の数が多すぎる
		if(max_black<0){
			score=0;
		}
		//見つかっている黒の数がすくなすぎる
		else if(min_black > nonelist.size()){
			score=0;
		}
		//噛まれた対抗が多すぎる
		else if(vited_liar>white_liar){
			score=0;
		}
		//死んだ黒が多すぎる
		else if(black_dead>=3){
			score=0;
		}
		
		
		//破たんしているとき
		if(score==0.0){
			for(Agent a : nonelist){
				clist.put(a,0.0);
			}
			for(Agent a : deadlist){
				clist.put(a,0.0);
			}
			return;
		}

		
		
		
		
		
		
		
		
		//破綻してないとき
		//NOTE:1日目にCOされない可能性がある
		//(噛まれた対抗が0なら1になる)
		score=1.0/(vited_liar+1.0);
		//COが遅いとき疑う
		if(si!=null){
			score/=1+Tools.twice(si.getDay()-1)/9.0;
			score/=1+Tools.twice(si.avrCount()-2)/16.0;
		}
		if(mi!=null){
			score/=1+Tools.twice(mi.getDay()-1)/9.0;
			score/=1+Tools.twice(mi.avrCount()-2)/16.0;
		}
		
		//基本的に黒は潜伏していると考える
		//見つかっていない黒の数が多いときは潜伏死を考える
		double point=(double)(max_black);
		double w=1/5.0;
		double d_point=(deadlist.size()-point*w)/deadlist.size();
		double n_point=(nonelist.size()-point*(1-w))/nonelist.size();
		if(n_point<0){
			n_point=(deadlist.size()+nonelist.size()-point)/(deadlist.size()+nonelist.size());
			d_point=n_point;
		}
		
		//d_point=n_point=(double)(deadlist.size()+nonelist.size()-max_black)/(deadlist.size()+nonelist.size());
		
		for(Agent s : nonelist){
			clist.put(s,n_point);
		}
		for(Agent s : deadlist){
			clist.put(s,d_point);
		}
		return;
	}
	
	
	public double getScore()
	{
		return score;
	}
	
	public ListMap<Agent,Double> getScoreList(){
		return clist;
	}
	
	public JudgeInfo getSeer(){
		return si;
	}

	public JudgeInfo getMedium(){
		return mi;
	}
	
	
}
