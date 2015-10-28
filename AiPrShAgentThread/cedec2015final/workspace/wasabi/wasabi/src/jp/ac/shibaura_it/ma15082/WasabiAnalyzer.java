package jp.ac.shibaura_it.ma15082;


import java.util.ArrayList;
import java.util.List;



import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

//NOTE:GameInfo dead_data,gurad_data�Ȃ�
//��q��̐������ł��Ȃ�
public class WasabiAnalyzer {
	private Personality personality;
	private ListMap<Agent,JudgeInfo> seer_data;
	private ListMap<Agent,JudgeInfo> medium_data;
	private ListMap<Agent,PlayerInfo> player_data;
	private List<LineInfo> lis;
	
	private TalkInfo talkinfo;
	private int talkindex;
	private int talkday;
	private int[] talkcount;
	List<Message> estimatelist;
	
	public WasabiAnalyzer(GameInfo gi,Personality p){
		personality=p;
		seer_data=new ListMap<Agent,JudgeInfo>();
		medium_data=new ListMap<Agent,JudgeInfo>();
		player_data=new ListMap<Agent,PlayerInfo>();
		talkinfo=new TalkInfo(gi);
		talkindex=0;
		talkday=-1;
		estimatelist=new ArrayList<Message>();
		
		
		if(player_data.size()<=0){
			for(Agent a : gi.getAgentList()){
				player_data.put(a,new PlayerInfo(a));
			}
		}
		//max(s+m)=6
		//max((s+1)*(m+1))=16
		
	}
	
	
	public void update(GameInfo gi){
		List<Talk> talks=gi.getTalkList();
		Agent agent;
		estimatelist.clear();
		//�����f�[�^�̏�����
		if(gi.getDay()!=talkday){
			talkday=gi.getDay();
			talkindex=0;
			talkcount=new int[gi.getAgentList().size()+1];
			for(int i=0;i<talkcount.length;i++){
				talkcount[i]=0;
			}
		}
		//���S�f�[�^�̍X�V
		agent=gi.getAttackedAgent();
		if(agent!=null){
			player_data.get(agent).setVited();
		}
		agent=gi.getExecutedAgent();
		if(agent!=null){
			player_data.get(agent).setVoted();
		}
		
		//�����̉��
		int count=0;
		int prev=talkindex;
		
		for(;talkindex<talks.size();talkindex++){
			Talk talk=talks.get(talkindex);
			Utterance u=new Utterance(talk.getContent());
			agent=talk.getAgent();
			talkcount[agent.getAgentIdx()]++;
			switch(u.getTopic()){
			case COMINGOUT://CO ��\�Ɛ肢�������ׂ�B
				//�����̂��Ƃ������Ă��Ȃ������͖�������
				if(!agent.equals(u.getTarget())){
					player_data.get(agent).setRole(Role.WEREWOLF);;
					continue;
				}
				if(u.getRole()==Role.SEER && !seer_data.containsKey(agent)){
					seer_data.put(agent,new JudgeInfo(agent,gi.getDay()));
					boolean flag=player_data.get(agent).setRole(Role.SEER);
					if(flag){
						seer_data.remove(agent);
						medium_data.remove(agent);
					}
				}
				else if(u.getRole()==Role.MEDIUM && !medium_data.containsKey(agent)){
					medium_data.put(agent,new JudgeInfo(agent,gi.getDay()));
					boolean flag=player_data.get(agent).setRole(Role.MEDIUM);
					if(flag){
						seer_data.remove(agent);
						medium_data.remove(agent);
					}
				}
				else{
					boolean flag=player_data.get(agent).setRole(u.getRole());
					if(flag){
						seer_data.remove(agent);
						medium_data.remove(agent);
					}
				}
				break;
			case INQUESTED://sence
				//��\��CO���Ă���Ƃ������l������
				if(medium_data.containsKey(agent)){
					medium_data.get(agent).put(gi.getDay(), u.getTarget(), u.getResult(),talkcount[agent.getAgentIdx()]);
					player_data.get(u.getTarget()).setMedium(agent,u.getResult());
				}
				break;
			case DIVINED://inspect
				//�肢��CO���Ă���Ƃ������l������
				if(seer_data.containsKey(agent)){
					seer_data.get(agent).put(gi.getDay(), u.getTarget(), u.getResult(),talkcount[agent.getAgentIdx()]);
					player_data.get(u.getTarget()).setSeer(agent,u.getResult());
				}
				break;
			default://����ȊO�͋^���Ă��邩�M���Ă��邩�̔��肾������
				Colour c=Colour.analyze(u);
				if(c!=Colour.GREY){
					estimatelist.add(new Message(agent,u.getTarget(),c));
				}
				break;
			}
			//�ǂݍ��ޔ�������������Ǝ��ԓ��ɏ����ł��Ȃ��B
			if(++count>20){
				break;
			}
		}
		
		talkinfo.setMessages(estimatelist, gi);
		talkinfo.calcScore(personality.getWeightPrev(), gi);
		
		
		
		//�肢�E��\���C���̐M���x�̌v�Z
		lis=new ArrayList<LineInfo>();
		
		if(seer_data.size()+medium_data.size() > 6){
			lis.add(new LineInfo(null,null,player_data.valueList(),seer_data.size(),medium_data.size()));
			
			if(seer_data.containsKey(gi.getAgent())){
				lis.add(new LineInfo(seer_data.get(gi.getAgent()), null, player_data.valueList(),seer_data.size(),medium_data.size()));
			}
			else if(medium_data.containsKey(gi.getAgent())){
				lis.add(new LineInfo(null, medium_data.get(gi.getAgent()), player_data.valueList(),seer_data.size(),medium_data.size()));
			}
			return;
		}
		
		
		
		for(int s=0;s<seer_data.size();s++){
			JudgeInfo si=seer_data.getValue(s);
			for(int m=0;m<medium_data.size();m++){
				JudgeInfo mi=medium_data.getValue(m);
				lis.add(new LineInfo(si,mi,player_data.valueList(),seer_data.size(),medium_data.size()));
			}
			//int m=medium_data.size();
			JudgeInfo mi=null;
			lis.add(new LineInfo(si,mi,player_data.valueList(),seer_data.size(),medium_data.size()));
		}
		//int s=seer_data.size();
		JudgeInfo si=null;
		for(int m=0;m<medium_data.size();m++){
			JudgeInfo mi=medium_data.getValue(m);
			lis.add(new LineInfo(si,mi,player_data.valueList(),seer_data.size(),medium_data.size()));
		}
		//int m=medium_data.size();
		JudgeInfo mi=null;
		lis.add(new LineInfo(si,mi,player_data.valueList(),seer_data.size(),medium_data.size()));
		
		
	}
	
	
	
	
	
	public List<PlayerInfo> getPlayerInfos(){
		return player_data.valueList();
	}

	
	public double getScoreTwice(Agent from,Agent to,int d){
		
		return talkinfo.getScoreTwice(from, to, d);
	}
	
	
	public int seerSize(){
		return seer_data.size()+1;
	}
	public int MediumSize(){
		return medium_data.size()+1;
	}
	
	
	public List<Agent> getSeerList(){
		List<Agent> ret=new ArrayList<Agent>(seer_data.keyList());
		ret.add(null);
		return ret;
	}
	
	public List<Agent> getMediumList(){
		List<Agent> ret=new ArrayList<Agent>(medium_data.keyList());
		ret.add(null);
		return ret;
	}
	
	public List<JudgeInfo> getSeerInfos(){
		return seer_data.valueList();
	}
	public List<JudgeInfo> getMediumInfos(){
		return medium_data.valueList();
	}
	
	
	public LineInfo getLineInfo(Agent seer,Agent medium){
		for(LineInfo li : lis){
			if(
				((li.getSeer()==null && seer==null) || (li.getSeer()!=null && li.getSeer().getAgent().equals(seer)))
				&& 
				((li.getMedium()==null && medium==null) || (li.getMedium()!=null && li.getMedium().getAgent().equals(medium)))
			
			){
				return li;
			}
			
		}
		
		return null;
	}


	public List<LineInfo> getLineInfos() {
		return lis;
	}
	
	public TalkInfo getTalkInfo(){
		return talkinfo;
	}
	
	
	public JudgeInfo getSeerInfo(Agent key){
		return seer_data.get(key);
	}
	public JudgeInfo getMediumInfo(Agent key){
		return medium_data.get(key);
	}
	
	
}
