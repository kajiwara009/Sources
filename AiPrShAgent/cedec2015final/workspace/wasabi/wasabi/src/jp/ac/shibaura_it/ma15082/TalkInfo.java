package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.List;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;


public class TalkInfo {
	List<Agent> agents;
	double[][] s,t,u,r;
	double[][] p;
	List<double[]> d;
	double[] b,c,e;
	int day;
	
	
	
	public TalkInfo(GameInfo gi){
		agents=gi.getAgentList();
		day=-1;
		p=new double[agents.size()][agents.size()];
		u=new double[agents.size()][agents.size()];
		s=new double[agents.size()][agents.size()];
		t=new double[agents.size()][agents.size()];
		r=new double[agents.size()][agents.size()];
		c=new double[u.length];
		b=new double[u.length];
		d=new ArrayList<double[]>();
		e=new double[agents.size()];
		
	}

	public void setMessages(List<Message> mss,GameInfo gi){
		if(gi.getDay()>day){
			day=gi.getDay();
			Agent a=gi.getAttackedAgent();
			
			if(a!=null){
				
			double[] temp1=new double[agents.size()];
			double[] temp2=new double[agents.size()];
			double[] temp3=new double[agents.size()];
			
			for(int i=0;i<agents.size();i++){
				temp1[i]=getScoreTwice(agents.indexOf(a),i,0);
				temp2[i]=s[agents.indexOf(a)][i];
				temp3[i]=t[agents.indexOf(a)][i];
				
			}
			d.add(temp1);
			d.add(temp2);
			d.add(temp3);
			e=avrDead();
			}
			
			List<Vote> votes=gi.getVoteList();
			for(int i=0;i<u.length;i++){
				for(int j=0;j<u[i].length;j++){
					p[i][j]=u[i][j];
				}
			}
			for(int i=0;i<u.length;i++){
				for(int j=0;j<u[i].length;j++){
					u[i][j]=(i==j)?1:0;
				}
			}
			
			if(votes!=null)
			for(Vote vote : votes){
				int i=agents.indexOf(vote.getAgent());
				int j=agents.indexOf(vote.getTarget());
				u[i][j]-=5;
			}
			
			
		}
				
		
		
		for(Message m : mss){
			int i=agents.indexOf(m.getFrom());
			int j=agents.indexOf(m.getSubject());
			if(i<0 || j<0){
				continue;
			}
			double d=m.getObject().equals(Colour.WHITE)?1:-1;
			u[i][j]+=d;
			u[i][i]+=0.5;
		}
		
		
	}

	
	
	
	public void calcScore(double prev,GameInfo gi){
		for(int i=0;i<r.length;i++){
			for(int j=0;j<r[i].length;j++){
				r[i][j]=u[i][j]+prev*p[i][j];
			}
		}
		
		for(int i=0;i<c.length;i++){
			c[i]=0;
			b[i]=0;
			for(int j=0;j<c.length;j++){
				c[i]+=r[i][j]*r[i][j];
				b[i]+=r[j][i]*r[j][i];
			}
			c[i]=Math.sqrt(c[i]);
			b[i]=Math.sqrt(b[i]);
		}
		
		for(int i=0;i<u.length;i++){
			for(int j=0;j<u[i].length;j++){
				if(i==j){
					t[i][j]=1;
					s[i][j]=1;
					continue;
				}
				t[i][j]=0;
				s[i][j]=0;
				for(int k=0;k<u[i].length;k++){
					t[i][j]+=r[i][k]*r[k][j];
					s[i][j]+=r[i][k]*r[j][k];
				}
				t[i][j]/=c[i]*b[j];
				s[i][j]/=c[i]*c[j];
			
			}
			
		}
		
	}
	

	public double getScore(String from,String to,int d){
		return getScore(agents.indexOf(from),agents.indexOf(to));
	}
	
	
	public double getScore(int i,int j){
		return s[i][j];
	}
	


	public double getTwice(Agent from,Agent to,int d){
		return getTwice(agents.indexOf(from),agents.indexOf(to));
	}
	
	
	public double getTwice(int i,int j){
		return t[i][j];
	}

	public double getScoreTwice(Agent from,Agent to,int d){
		return getScoreTwice(agents.indexOf(from),agents.indexOf(to),d);
	}
	
	
	public double getScoreTwice(int i,int j,int d){
		double x=s[i][j]+t[i][j]+2+e[j];
		//return x*x/16;
		return x/5;
	}
	
	
	//
	public double[] avrDead(){
		double[] ret=new double[agents.size()];
		int[] rank=new int[agents.size()];
		for(double[] temp : d){
			for(int i=0;i<rank.length;i++){
				rank[i]=i;
			}
			for(int i=0;i<rank.length;i++){
				for(int j=i+1;j<rank.length;j++){
					if(temp[rank[i]]<temp[rank[j]]){
						int buf=rank[i];
						rank[i]=rank[j];
						rank[j]=buf;
					}
				}
			}			
			for(int i=0;i<temp.length;i++){
				ret[i]-=(double)rank[i]/d.size();
			}
		}
		
		for(int i=0;i<rank.length;i++){
			rank[i]=i;
		}
		for(int i=0;i<rank.length;i++){
			for(int j=i+1;j<rank.length;j++){
				if(ret[rank[i]]<ret[rank[j]]){
					int buf=rank[i];
					rank[i]=rank[j];
					rank[j]=buf;
				}
			}
		}
		//‹^‚í‚ê‚Ä‚¢‚é•û‚ªrank‚Í‘å‚«‚¢
		
		for(int i=0;i<rank.length;i++){
			ret[i]=(double)(rank.length-rank[i])/rank.length;
		}
		
		
		
		return ret;
	}
	
	
}
