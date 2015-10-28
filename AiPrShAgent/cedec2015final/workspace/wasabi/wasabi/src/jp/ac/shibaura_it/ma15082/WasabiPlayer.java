package jp.ac.shibaura_it.ma15082;

import java.util.ArrayList;
import java.util.List;


import org.aiwolf.client.base.player.AbstractRole;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Judge;
import org.aiwolf.common.data.Player;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.net.GameInfo;
import org.aiwolf.common.net.GameSetting;


public class WasabiPlayer extends AbstractRole implements Player{

	private String name;
	private Agent agent;
	private GameInfo gi;
	private Personality personality;
	//情報の解析
	private WasabiAnalyzer wa;
	//自分視点の情報
	private List<PlayerInfo> view;
	//主観的な信頼度
	private ListMap<Agent,Double> score;
	private ListMap<ListMap<Agent,Double>,Double> scores;
	
	//狼・狂人のリスト
	private ListMap<Agent,Pair<Role,Double>> wolfs;
	private List<Agent> lunatics;
	//自分が騙る役職
	private Role myRole;

	private List<String> role_mess;
	private int skip_count;
	
	
	
	@Override
	public void initialize(GameInfo arg0, GameSetting arg1) {
		gi=arg0;
		agent=gi.getAgent();
		name="WASABI_"+gi.getAgent().getAgentIdx();
		view=null;
		score=new ListMap<Agent,Double>(16);
		scores=new ListMap<ListMap<Agent,Double>,Double>();
		double oe=0,co=0,ex=0,ag=0,ne=0;
		if(gi.getRole().getSpecies()==Species.HUMAN){
			oe=Tools.random()<0.5? Tools.random(0.2):1-Tools.random(0.2);
			co=Tools.random(0.2)+0.8;
			ex=Tools.random(0.3)+0.7;
			ag=Tools.random(0.3)+0.2;
			ne=Tools.random();
		}
		else{
			oe=Tools.random(0.2);
			co=Tools.random(0.3);
			ex=Tools.random(0.3)+0.7;
			ag=Tools.random(0.3)+0.6;
			ne=Tools.random(0.2);
		}
		
		personality=new Personality(oe,co,ex,ag,ne);
		
		wolfs=new ListMap<Agent,Pair<Role,Double>>();
		lunatics=new ArrayList<Agent>();

		wa=new WasabiAnalyzer(gi,personality);
		role_mess=new ArrayList<String>();
		
		//NOTE:騙りの計算はできない
		//狼が騙らないから狂人が騙る
		if(gi.getRole()==Role.WEREWOLF){
			for(Agent a : gi.getAgentList()){
				if(gi.getRoleMap().get(a)==Role.WEREWOLF){
					wolfs.put(a,null);
				}
			}
		}
		else if(gi.getRole()==Role.POSSESSED){
			myRole=Role.SEER;
		}
		
		
		
		return;
	}


	@Override
	public void update(GameInfo arg0) {
		gi=arg0;
		wa.update(gi);
		view=new ArrayList<PlayerInfo>(16);
		scores.clear();
		for(PlayerInfo p : wa.getPlayerInfos()){
			PlayerInfo pi=new PlayerInfo(p);
			view.add(pi);
			pi.setCertain(0.0);
			if(pi.getAgent().equals(agent)){
				score.put(pi.getAgent(),1.0);//自分は絶対に白
			}
			
			else{
				score.put(pi.getAgent(), Tools.random()*personality.getWeightRandom()+(1-personality.getWeightRandom())*wa.getScoreTwice(agent,pi.getAgent(),gi.getDay()));//主観的な疑い
			}
			
		}
		
		for(Agent s : wa.getSeerList()){
			for(Agent m : wa.getMediumList()){
				LineInfo li=wa.getLineInfo(s, m);
				if(li==null){
					continue;
				}
				ListMap<Agent,Double> lm=li.getScoreList();
				//自分を疑っているラインは疑う
				double score=li.getScore()*lm.get(agent);
				
				scores.put(lm,score);
			}
		}

		Tools.unit(scores);
	
		
		for(ListMap<Agent,Double> t : scores.keyList()){
			for(Agent s : t.keyList()){
				PlayerInfo pi=null;
				for(int i=0;i<view.size();i++){
					if(view.get(i).getAgent().equals(s)){
						pi=view.get(i);
						break;
					}
				}
				
				pi.setCertain(pi.getCertain()+scores.get(t)*Tools.calcCertain(t.get(s),score.get(s),personality.getWeightSubjective()));

			}
			
		}
		
		for(PlayerInfo pi : view){
			if(pi.getRole().getSpecies()==Species.WEREWOLF){
				pi.setCertain(0);
			}
		}
		
		return;
	}


	//NOTE:mess,role_messはdayStartで設定
	//role_messはどちらもCO,結果の2手
	//0日目はなにもしない
	//1日目から必ずCOする
	@Override
	public void dayStart() {
		Judge j;		
		if(gi.getDay()<=0){
			//mess=getTalkList();
			return;
		}
		role_mess.clear();
		switch(gi.getRole()){
		case SEER:
			j=gi.getDivineResult();
			role_mess.add(TemplateTalkFactory.comingout(agent,Role.SEER));
			if(j!=null){
				role_mess.add(TemplateTalkFactory.divined(j.getTarget(),j.getResult()));
			}
			break;
		case MEDIUM:
			j=gi.getMediumResult();
			role_mess.add(TemplateTalkFactory.comingout(agent,Role.MEDIUM));
			if(j!=null){
				role_mess.add(TemplateTalkFactory.inquested(j.getTarget(),j.getResult()));
			}
			break;
		case POSSESSED:
			if(myRole==Role.SEER){
				wolf_inspect();
			}
			break;
		case WEREWOLF:
			break;
		default:
			break;
			
		}
		
		skip_count=0;
	}

	public void wolf_inspect(){
		ListMap<Agent,Double> inspectlist=getInspectList();
		Agent p=Tools.selectKey(inspectlist);
		if(p==null){
		List<Agent> as=gi.getAliveAgentList();
		while(p==null){
			p=as.get(Tools.rand(as.size()));
			if(p==agent){
				p=null;
			}
		}
		}
		
		double a=0;
		PlayerInfo temp=null;
		for(PlayerInfo pi : view){
			if(pi.getAgent().equals(p)){
				a=pi.getCertain();
				temp=pi;
				break;
			}
		}
		//自分視点の信頼度から結果を決める
		Species c=Species.HUMAN;
		if(temp.getRole()!=Role.VILLAGER){
			JudgeInfo j=wa.getSeerInfo(agent);
			if(j!=null){
				int d=j.numBlack();
				c=(d>=3?Species.HUMAN:Species.WEREWOLF);
			}
			else{
				c=Species.WEREWOLF;
			}
		}
		else{
			c=((Tools.random())<a) ? Species.HUMAN:Species.WEREWOLF;
		}
		//System.err.println(p+" "+a+" "+c);
		role_mess.add(TemplateTalkFactory.comingout(agent,Role.SEER));
		role_mess.add(TemplateTalkFactory.divined(p,c));
		return;
	}
	
	
	
	@Override
	public void finish() {
	}
	
	@Override
	public String getName() {
		return name;
	}


	
	
	@Override
	public Agent vote() {
		ListMap<Agent,Double> votelist=getVoteList();
		
		Agent p=Tools.selectKey(votelist);
		if(p==null){
		List<Agent> as=gi.getAliveAgentList();
		while(p==null){
			p=as.get(Tools.rand(as.size()));
			if(p==agent){
				p=null;
			}
		}
		}
		
		return p;
	}



	//NOTE:任意で死体なしが出せる
	//4人のときは死体なしにする
	@Override
	public Agent attack() {
	ListMap<Agent,Double> attacklist=getAttackList();
	Agent p=Tools.selectKey(attacklist);
	if(p==null){
	List<Agent> as=gi.getAliveAgentList();
	while(p==null){
		p=as.get(Tools.rand(as.size()));
		if(wolfs.containsKey(p)){
			p=null;
		}
	}
	}
	if(gi.getAliveAgentList().size()==4){
		return null;
	}
	return p;
	}


	@Override
	public Agent divine() {
	ListMap<Agent,Double> inspectlist=getInspectList();
	Agent p=Tools.selectKey(inspectlist);
	if(p==null){
	List<Agent> as=gi.getAliveAgentList();
	while(p==null){
		p=as.get(Tools.rand(as.size()));
		if(p==agent){
			p=null;
		}
	}
	}
	return p;
	}


	@Override
	public Agent guard() {
		ListMap<Agent,Double> guardlist=getGuardList();
		Agent p=Tools.selectKey(guardlist);
		if(p==null){
		List<Agent> as=gi.getAliveAgentList();
		while(p==null){
			p=as.get(Tools.rand(as.size()));
			if(p==agent){
				p=null;
			}
		}
		}
		return p;
	}





	//NOTE:メッセージはgetTalkLstでまとめて取得せずgetTalkで毎回取得する
	//talk1回で1メッセージしか送信できない、updateの影響を考慮できる
	@Override
	public String talk() {
		if(role_mess!=null && role_mess.size()>0){
			return role_mess.remove(0);
		}
		return getTalk();
	}

	@Override
	public String whisper() {
		//return null;
		return getWhisper();
	}

	
	//NOTE:役職ＣＯした中で矛盾したものだけチェックする
	public void searchLunatics(){
		lunatics.clear();
		for(JudgeInfo ji : wa.getSeerInfos()){
			for(Judge j : ji.getJudgeList()){
				if(wolfs.containsKey(j.getTarget())){
					if(j.getResult()==Species.HUMAN){
						lunatics.add(ji.getAgent());
						break;
					}
				}
				else{
					if(j.getResult()==Species.WEREWOLF){
						lunatics.add(ji.getAgent());
						break;
					}
				}
			}
		}
		for(JudgeInfo ji : wa.getMediumInfos()){
			for(Judge j : ji.getJudgeList()){
				if(wolfs.containsKey(j.getTarget())){
					if(j.getResult()==Species.HUMAN){
						lunatics.add(ji.getAgent());
						break;
					}
				}
				else{
					if(j.getResult()==Species.WEREWOLF){
						lunatics.add(ji.getAgent());
						break;
					}
				}
			}
		}
				
		return;
	}

	

	public ListMap<Agent,Double> getAliveList(){
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		
		for(PlayerInfo pi : view){
			if(pi.isAlive()){
				ret.put(pi.getAgent(),1.0);
			}
			else{
				ret.put(pi.getAgent(),0.0);				
			}
		}
		return ret;
	}
	
	//true->white false->black
	public ListMap<Agent,Double> getAliveList(boolean flag){
		boolean nonzero=false;
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		
		for(PlayerInfo pi : view){
			if(pi.isAlive()){
				double x=(flag?pi.getCertain():1.0-pi.getCertain());
				ret.put(pi.getAgent(),x);
				if(x>0){
					nonzero=true;
				}
			}
			else{
				ret.put(pi.getAgent(),0.0);				
			}
		}
		
		if(nonzero){
			return ret;
		}
		
		return getAliveList();
		
	}
	
	
	public ListMap<Agent,Double> getVoteList(){
		ListMap<Agent,Double> ret;
		boolean flag;
		ret=getAliveList(false);
		
		for(PlayerInfo pi : view){
			if(pi.isAlive()){
				//残りの人数が多いときには占い師はつりにくくする
				if(pi.getRole()==Role.SEER){
					double a=((double)(wa.seerSize())/gi.getAliveAgentList().size());
					
					double r=ret.get(pi.getAgent());
					double th=1.0/gi.getAliveAgentList().size();
					
					if(r<th || (1-th)<r){
						ret.put(pi.getAgent(),r);
					}
					else if(r*a>1){
						ret.put(pi.getAgent(),1.0);
					}
					else{
						ret.put(pi.getAgent(),r*a);
					}
					
				}
			}
			
			else{
				ret.put(pi.getAgent(),0.0);				
			}
		}
		ret.put(agent,0.0);
		
		flag=Tools.cutList(ret,6,1);

		
		if(flag){
			return ret;
		}
		ret=getAliveList(false);
		ret.put(agent,0.0);
		return ret;
	}
	
	

	
	public ListMap<Agent,Double> getGuardList(){
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		boolean flag;
		for(PlayerInfo pi : view){
			if(pi.isAlive()){
				double a=0.1;
				if(pi.getRole()==Role.SEER && pi.getCertain()>(0.8/wa.seerSize())){
					a*=gi.getAliveAgentList().size()/2.0;
				}
				else if(pi.getRole()==Role.MEDIUM){a*=2;}
				ret.put(pi.getAgent(),pi.getCertain()*a);
				
			}
			
			else{
				ret.put(pi.getAgent(),0.0);				
			}
		}
		

		ret.put(agent,0.0);
		flag=Tools.cutList(ret,2,1);
		
		if(flag){
			return ret;
		}
		ret=getAliveList(true);
		ret.put(agent,0.0);
		return ret;
	}
	
	
	
	public ListMap<Agent,Double> getAttackList(){
		searchLunatics();
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		boolean flag;
		
		for(PlayerInfo pi : view){
			if(wolfs.containsKey(pi.getAgent())){
				ret.put(pi.getAgent(),0.0);
			}
			else if(pi.isAlive()){
				//ret.put(pi.getAgent(),pi.getCertain()*(1-score.get(pi.getAgent())));
				ret.put(pi.getAgent(),pi.getCertain());
				
			}
			
			else{
				ret.put(pi.getAgent(),0.0);				
			}
		}

		for(Agent s : lunatics){
			//if(!bleach.contains(s))
			ret.put(s, 0.0);
			
		}
		flag=Tools.cutList(ret,5,1);
		if(flag){
			return ret;
		}
		ret=getAliveList(true);
		for(Agent s : lunatics){
			ret.put(s, 0.0);
		}
		for(Agent s : wolfs.keyList()){
			ret.put(s,0.0);
		}
		return ret;
	}
	
	
	public ListMap<Agent,Double> getInspectList(){
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		boolean flag=false;
		for(PlayerInfo pi : view){
			if(pi.isAlive()){
				if(pi.isChecked()){
					ret.put(pi.getAgent(),0.0);
				}
				else if(pi.getRole()!=Role.VILLAGER){
					ret.put(pi.getAgent(),0.0);
				}
				else{
					//0 1に近くないほど占いにくい
					double x=pi.getCertain()*(1-pi.getCertain());
					ret.put(pi.getAgent(),(x));
				}
				
			}
			else{
				ret.put(pi.getAgent(),0.0);				
			}
			
		}
		
		flag=Tools.cutList(ret,2,1);
		
		if(flag){
			return ret;
		}
		ret=getAliveList(false);
		for(PlayerInfo pi : view){
			if(pi.isChecked(agent)){
				ret.put(pi.getAgent(), 0.0);
			}
			if(pi.getRole()==Role.SEER){
				ret.put(pi.getAgent(), 0.0);
			}
		}
		ret.put(agent,0.0);
		flag=Tools.cutList(ret);
		if(flag){
			return ret;
		}
		ret=getAliveList();
		ret.put(agent,0.0);
		return ret;
	}
	
	
	
	
	public ListMap<Agent,Double> getWhiteList(double th){
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		for(PlayerInfo pi : view){
			if(pi.isDead()){
				ret.put(pi.getAgent(),0.0);
			}
			else if(pi.getAgent().equals(agent)){
				ret.put(pi.getAgent(),0.0);
			}
			else if(th<pi.getCertain()){
				ret.put(pi.getAgent(),pi.getCertain());
			}
			else{
				ret.put(pi.getAgent(), 0.0);
			}
		}
		
		
		
		return ret;
	}
		
	
	public ListMap<Agent,Double> getBlackList(double th){
		ListMap<Agent,Double> ret=new ListMap<Agent,Double>();
		for(PlayerInfo pi : view){
			if(pi.isDead()){
				ret.put(pi.getAgent(),0.0);
			}
			else if(pi.getAgent().equals(agent)){
				ret.put(pi.getAgent(),0.0);
			}
			else if(pi.getCertain()<th){
				ret.put(pi.getAgent(),1-pi.getCertain());
			}
			else{
				ret.put(pi.getAgent(), 0.0);
			}
		}
		
		return ret;
	}
	
	
	
	
	//NOTE:白はその役職、黒は狼であると推測する発言をする
	public String getTalk(){
		String ret=skip_count>2?TemplateTalkFactory.over():TemplateTalkFactory.skip();
		Agent to;
		Role role;
		if(personality.getWeightVolume()<Tools.random()){
			skip_count++;
			return ret;
		}
		if(Tools.random()<personality.getWeightTalkWhite()){
			to=Tools.selectKey(getWhiteList(0.5));
			role=Role.VILLAGER;
			for(PlayerInfo pi : view){
				if(pi.getAgent()==to){
					role=pi.getRole();
					break;
				}
			}
		}
		else{
			to=Tools.selectKey(getBlackList(0.5));
			role=Role.WEREWOLF;
		}
		if(to!=null){
			ret=(TemplateTalkFactory.estimate(to,role));

		}
		
		return ret;
	}
	
	
	
	//NOTE:自分だったらどんな行動をするか、狂人の位置について話す
	public String getWhisper(){
		Agent p;
		String ret=skip_count>2?TemplateWhisperFactory.over():TemplateWhisperFactory.skip();
		if(personality.getWeightVolume()<Tools.random()){
			skip_count++;
			return ret;
		}
		switch(Tools.rand(6)){
		case 0://狂人について
		if(lunatics.size()>0){
			ret=TemplateWhisperFactory.estimate(lunatics.get(Tools.rand(lunatics.size())), Role.POSSESSED);
		}
		break;
		case 1://襲撃先について
			p=Tools.selectKey(getAttackList());
			if(p!=null){
				ret=TemplateWhisperFactory.attack(p);
			}
			break;
		case 2://占いについて
			p=Tools.selectKey(getInspectList());
			if(p!=null){
				Species s=wolfs.containsKey(p)?Species.WEREWOLF:Species.HUMAN;
				ret=TemplateWhisperFactory.divined(p,s);
			}
			break;
		case 3://護衛について
			p=Tools.selectKey(getGuardList());
			if(p!=null){
				ret=TemplateWhisperFactory.guarded(p);
			}
			break;
		case 4://投票について
			p=Tools.selectKey(getVoteList());
			if(p!=null){
				ret=TemplateWhisperFactory.vote(p);
			}
			break;
		default:
			break;
		}
		return ret;
	}
	
	
	
}

