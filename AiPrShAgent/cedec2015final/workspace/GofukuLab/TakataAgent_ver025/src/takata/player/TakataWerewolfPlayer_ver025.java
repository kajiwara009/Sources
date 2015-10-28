////ファイル名：TakataSimpleWerewolfPlayer_Deception1Agreement1
////作成者：高田和磨
////作成日時：2014.12.2
////バージョン：0.1.1
////種類：人狼
////特徴：人狼以外のプレイヤーをグレーリストに入れスタート．
////	人狼に対して不利な発言をしたプレイヤーをアタックリストに追加．
////	投票はグレーリストからランダムに行う．
////	最初2日間はグレーリストから襲撃を行い，3日目以降はアタックリストからアタックレベルの高いプレイヤーを襲撃．


package takata.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.aiwolf.client.base.player.AbstractWerewolf;
import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.net.GameInfo;

public class TakataWerewolfPlayer_ver025 extends AbstractWerewolf {

	//インポート
	COMapInfo comapInfo = new COMapInfo();
	//その日の最初の発言
	boolean FirstTalk = true;
	//騙りCO初日
    boolean FirstCODay = false;
    //騙り済み
    boolean Deceive = false;
    //CO済み
    boolean COTalked = false;
    //初日のみ意味のないCO済み
    boolean FirstDayCOed = false;
    //投票
    boolean vote = false;
    //囁きでの投票
    boolean whispervote = false;
    //投票宣言決意
    boolean DecideVote = false;
    //投票の一致
    boolean VoteInLine = false;
    //オーバー済み
    boolean Overed = false;
    //襲撃先発言済み
    boolean TalkAttackAgent = false;
    //騙るロール
    Role FakeRole = null;
    //その日の投票対象プレイヤー
    Agent PlanningVotedAgent = null;
    //その日の襲撃対象プレイヤー
    Agent PlanningAttackedAgent = null;
	//偽占い（誰を何と占うか）
	Agent DivinedAgent;
	Species DivinedColor;
	Map<Agent, Species> DivinedResult = new HashMap<Agent, Species>();
	//現在のカミングアウト集計マップ
	Map<Agent, Role> COedMap = new HashMap<Agent, Role>();
	//過去のカミングアウト集計リスト
	Role COedData[][] = new Role[16][99];
	//カミングアウト回数集計マップ
	Map<Agent, Integer> COedNumMap = new HashMap<Agent, Integer>();
	//回ってきたTalk回数
    int TalkTurnNum = 0;
    //回ってきたwhisper回数
    int whisperTurnNum = 0;
    //スキップ回数
    int SkipNum = 0;
    //回ってきたWhisper回数
    int WhisperTurnNum = 0;
    //偽占い回数
    int FakeDivinedNum = 0;
    //偽占い発言回数
    int FakeDivinedTalkNum = 0;
    //同調発言回数
    int AgreeNum = 0;
    //同調発言回数
    int DisagreeNum = 0;
    //囁き上の同調回数
    int WhisperAgreeNum = 0;
    //囁き上の反論回数
    int WhisperDisagreeNum = 0;
    //襲撃レベルリスト
    List<Agent> AttackList = new ArrayList<Agent>();
    int[] AttackLevel = new int[16];
    //あるプレイヤーの1ゲーム当たりのCO回数
    int[] CONum = new int[16];
    //あるプレイヤーの1日当たりの占い回数
    int[] DivineNum = new int[16];
    //あるプレイヤーの1日当たりの霊能回数
    int[] InquestedNum = new int[16];
    //占い結果リスト
	List<Agent> SeerDivinedWhiteAgentList = new ArrayList<Agent>(),
				SeerDivinedBlackAgentList = new ArrayList<Agent>(),
				FakeDivinedWhiteAgentList = new ArrayList<Agent>(),
				FakeDivinedBlackAgentList = new ArrayList<Agent>(),
				//自分の占い結果
				FakeMyDiviningAgentList = new ArrayList<Agent>(),
				FakeMyDivinedWhiteAgentList = new ArrayList<Agent>(),
				FakeMyDivinedBlackAgentList = new ArrayList<Agent>();
	//霊能結果リスト
	List<Agent> MediumInquestedWhiteAgentList = new ArrayList<Agent>(),
				MediumInquestedBlackAgentList = new ArrayList<Agent>(),
				FakeInquestedWhiteAgentList = new ArrayList<Agent>(),
				FakeInquestedBlackAgentList = new ArrayList<Agent>(),
				//自分の霊能結果
				FakeMyInquestingAgentList = new ArrayList<Agent>(),
				FakeMyInquestedWhiteAgentList = new ArrayList<Agent>(),
				FakeMyInquestedBlackAgentList = new ArrayList<Agent>();
	//被投票プレイヤーリスト
	List<Agent> VotedAgentList = new ArrayList<Agent>();
	//一日内の投票済みプレイヤー
	List<Agent> FinishVoteAgentList = new ArrayList<Agent>();
	//被襲撃者リスト
	List<Agent> AttackedAgentList = new ArrayList<Agent>();
	//死亡者リスト
	List<Agent> DeadAgentList = new ArrayList<Agent>();
	//over済みリスト
	List<Agent> OverList = new ArrayList<Agent>(),
				WhisOverList = new ArrayList<Agent>();
	//判定リスト
    List<Agent> VillagerAgentList = new ArrayList<Agent>(), 		//無職プレイヤー
				SeerAgentList = new ArrayList<Agent>(), 			//占い師プレイヤー
				MediumAgentList = new ArrayList<Agent>(), 			//霊媒師プレイヤー
				BodyGaurdAgentList = new ArrayList<Agent>(),		//狩人プレイヤー
				PossessedAgentList = new ArrayList<Agent>(),		//狂人プレイヤー
    			WerewolfAgentList = new ArrayList<Agent>(),			//人狼プレイヤー
    			GrayAgentList = new ArrayList<Agent>(), 			//グレー（未知）プレイヤー
    			BlackAgentList = new ArrayList<Agent>();			//排除したいプレイヤー
    //偽COリスト
    List<Agent> WolfFakeSeerCOAgentList = new ArrayList<Agent>(), 		//偽占い師プレイヤー
    			WolfFakeMediumCOAgentList = new ArrayList<Agent>(), 	//偽霊媒師プレイヤー
    			PosFakeSeerCOAgentList = new ArrayList<Agent>(), 		//偽占い師プレイヤー
 				PosFakeMediumCOAgentList = new ArrayList<Agent>(); 		//偽霊媒師プレイヤー
    //同調関係
    List<TemplateTalkFactory.TalkType> AgreeTalkTypeList = new ArrayList<TemplateTalkFactory.TalkType>();
    List<Integer> 	AgreeTalkDayList = new ArrayList<Integer>(),
    				AgreeTalkIDList = new ArrayList<Integer>();
    //反論関係
    List<TemplateTalkFactory.TalkType> DisagreeTalkTypeList = new ArrayList<TemplateTalkFactory.TalkType>();
    List<Integer> 	DisagreeTalkDayList = new ArrayList<Integer>(),
    				DisagreeTalkIDList = new ArrayList<Integer>();
    //囁き上の同調関係
    List<TemplateTalkFactory.TalkType> WhisperAgreeTalkTypeList = new ArrayList<TemplateTalkFactory.TalkType>();
    List<Integer> 	WhisperAgreeTalkDayList = new ArrayList<Integer>(),
    				WhisperAgreeTalkIDList = new ArrayList<Integer>();
    //囁き上の反論関係
    List<TemplateTalkFactory.TalkType> WhisperDisagreeTalkTypeList = new ArrayList<TemplateTalkFactory.TalkType>();
    List<Integer> 	WhisperDisagreeTalkDayList = new ArrayList<Integer>(),
    				WhisperDisagreeTalkIDList = new ArrayList<Integer>();

	@Override
  	public void dayStart() {
		//super.dayStart();
		//各初期化
		FirstTalk = true;
		whispervote = false;
		Overed = false;
		TalkAttackAgent = false;
		DecideVote = false;
		VoteInLine = false;
		PlanningVotedAgent = null;
		TalkTurnNum = 0;
		whisperTurnNum = 0;
		OverList.clear();
		for(int n=0; n<16; n++) {
			DivineNum[n] = 0;
			InquestedNum[n] = 0;
		}
		//投票済みプレイヤーリストおよび非投票者リストを初期化
		FinishVoteAgentList.clear();
		VotedAgentList.clear();
		//死者をリストから排除
		VillagerAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		SeerAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		MediumAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		BodyGaurdAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		PossessedAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		WerewolfAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		GrayAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		VillagerAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		SeerAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		MediumAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		BodyGaurdAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		PossessedAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		WerewolfAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		GrayAgentList.remove(getLatestDayGameInfo().getAttackedAgent());

		SeerDivinedWhiteAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		SeerDivinedBlackAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		FakeDivinedWhiteAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		FakeDivinedBlackAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
		SeerDivinedWhiteAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		SeerDivinedBlackAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		FakeDivinedWhiteAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		FakeDivinedBlackAgentList.remove(getLatestDayGameInfo().getAttackedAgent());

		AttackList.remove(getLatestDayGameInfo().getExecutedAgent());
		AttackList.remove(getLatestDayGameInfo().getAttackedAgent());

		FakeMyDiviningAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
		//死亡者を追加
		DeadAgentList.add(getLatestDayGameInfo().getExecutedAgent());
		DeadAgentList.add(getLatestDayGameInfo().getAttackedAgent());
	    //初日配置
		if(getLatestDayGameInfo().getDay() == 0){
			WerewolfAgentList.addAll(getWolfList());
			GrayAgentList.addAll(getLatestDayGameInfo().getAgentList());
			GrayAgentList.removeAll(WerewolfAgentList);
			FakeMyDiviningAgentList.addAll(getLatestDayGameInfo().getAgentList());
			FakeMyDiviningAgentList.remove(getMe());
		}
		//処刑者を偽霊能リストに追加
		if(getLatestDayGameInfo().getDay() > 1) {
			FakeMyInquestingAgentList.add(getLatestDayGameInfo().getExecutedAgent());
			AttackedAgentList.add(getLatestDayGameInfo().getAttackedAgent());
		}
	}

	@Override
	public void update(GameInfo gameInfo) {

		super.update(gameInfo);
	    //今日のログを取得
	    List<Talk> talkList = gameInfo.getTalkList();
	    List<Talk> whisperList = gameInfo.getWhisperList();

	    //全体会話に関する会話ログの処理
	    for(int ID = TalkTurnNum; ID < talkList.size(); ID++){
	        Talk talk = talkList.get(ID);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());

	        //自分の発言はスルー
	        if(talk.getAgent() == getMe()) continue;

	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
		    case COMINGOUT:

		    	//多すぎる複数COを排除
		    	if(CONum[getAgentNum(talk.getAgent())] > 9) {
		    		BlackAgentList.add(talk.getAgent());
		    		continue;
		    	}

		    	//カミングアウト結果を保存
		    	comapInfo.putCOMap(getAgentNum(talk.getAgent()), utterance.getRole(), CONum[getAgentNum(talk.getAgent())]);

		    	//COロールごとに処理
		    	switch (utterance.getRole()) {
		    	case SEER:
		    		if(GrayAgentList.contains(talk.getAgent())) {
		    			SeerAgentList.add(talk.getAgent());
		    			GrayAgentList.remove(talk.getAgent());
		    		}else if(WerewolfAgentList.contains(talk.getAgent())) {
		    			WolfFakeSeerCOAgentList.add(talk.getAgent());
		    		}
		    		break;
		    	case MEDIUM:
		    		if(GrayAgentList.contains(talk.getAgent())) {
			    		MediumAgentList.add(talk.getAgent());
			    		GrayAgentList.remove(talk.getAgent());
		    		}else if(WerewolfAgentList.contains(talk.getAgent())) {
		    			WolfFakeMediumCOAgentList.add(talk.getAgent());
		    		}
		    		break;
				case BODYGUARD:
					break;
				case POSSESSED:
					break;
				case VILLAGER:
					break;
				case WEREWOLF:
					break;
				default:
					break;
		    	}
		    	break;
		    case DIVINED:
		    	//超過Divineを排除
		    	if(DivineNum[getLatestDayGameInfo().getAgentList().indexOf(talk.getAgent())] >= getLatestDayGameInfo().getDay()) continue;
		    	DivineNum[getLatestDayGameInfo().getAgentList().indexOf(talk.getAgent())]++;
		    	//人狼が騙っていたら追加
		    	if(WerewolfAgentList.contains(talk.getAgent()) && !WolfFakeSeerCOAgentList.contains(talk.getAgent())) {
		    		WolfFakeSeerCOAgentList.add(talk.getAgent());
		    	}
		    	//人狼側を占った場合の処理
		    	if(WerewolfAgentList.contains(utterance.getTarget())) {
		    		switch (utterance.getResult()) {
		    		case HUMAN:
	         			SeerAgentList.remove(talk.getAgent());
			    		FakeDivinedWhiteAgentList.add(utterance.getTarget());
			    		break;
		    		case WEREWOLF:
		    			if(GrayAgentList.contains(talk.getAgent())) {
		    				SeerAgentList.add(talk.getAgent());
		         			GrayAgentList.remove(talk.getAgent());
		    			}
		    			if(!AttackList.contains(talk.getAgent()) && !WerewolfAgentList.contains(talk.getAgent())) {
			    			AttackList.add(talk.getAgent());
	         			}
	         			if(WerewolfAgentList.contains(talk.getAgent())){
	         				//何もしない
	         			}else {
	         				//アタックレベル上昇
	         				AttackLevel[AttackList.indexOf(talk.getAgent())] = AttackLevel[AttackList.indexOf(talk.getAgent())] + 3;
	         				TalkAttackAgent = false;
	         			}
		    			SeerDivinedBlackAgentList.add(utterance.getTarget());
						DisagreeTalkTypeList.add(TalkType.TALK);
						DisagreeTalkDayList.add(talk.getDay());
						DisagreeTalkIDList.add(talk.getIdx());
		    			break;
		    		}
		    	//人狼以外を占った場合
		    	}else {
			        switch (utterance.getResult()) {
			        case HUMAN:
		         		if(GrayAgentList.contains(talk.getAgent())) {
		         			SeerAgentList.add(talk.getAgent());
			           		GrayAgentList.remove(talk.getAgent());
		         		}
		         		if(!AttackList.contains(talk.getAgent()) && !WerewolfAgentList.contains(talk.getAgent())) {
				    		AttackList.add(talk.getAgent());
		         		}
		         		if(WerewolfAgentList.contains(talk.getAgent())){
		         			//何もしない
		         		}else {
		         			//アタックレベル上昇
		         			AttackLevel[AttackList.indexOf(talk.getAgent())]++;
		         			TalkAttackAgent = false;
		         		}
		         		SeerDivinedWhiteAgentList.add(utterance.getTarget());
			    		break;
			        //人間に黒判定した場合
			        case WEREWOLF:
	         			SeerAgentList.remove(talk.getAgent());
			    		FakeDivinedBlackAgentList.add(utterance.getTarget());
			    		break;
			        }
		    	}
	    		whispervote = false;
		    	break;
			case AGREE:
				//仲間の人狼に対して同調した場合，それに同調する（確率30%）
				if(WerewolfAgentList.contains(utterance.getTarget()) && utterance.getTarget() != getMe()) {
					int rand = new Random().nextInt(10);
					if(rand > 6) {
						AgreeTalkTypeList.add(TalkType.TALK);
						AgreeTalkDayList.add(talk.getDay());
						AgreeTalkIDList.add(talk.getIdx());
					}
				}
				break;
			case DISAGREE:
				//占い師に対して反対発言をした場合，それに同意する（30%）
				if(SeerAgentList.contains(utterance.getTarget())) {
					int rand = new Random().nextInt(10);
					if(rand > 6) {
						AgreeTalkTypeList.add(utterance.getTalkType());
						AgreeTalkDayList.add(utterance.getTalkDay());
						AgreeTalkIDList.add(utterance.getTalkID());
					}
				}
				break;
			case ESTIMATE:
				//人狼を対象とした場合
				if(WerewolfAgentList.contains(utterance.getTarget())) {
					if(utterance.getRole() == Role.WEREWOLF) {
						//人狼と予想した場合・・・反論
						int rand = new Random().nextInt(10);
						if(utterance.getTarget() == getMe()) {
							DisagreeTalkTypeList.add(TalkType.TALK);
							DisagreeTalkDayList.add(talk.getDay());
							DisagreeTalkIDList.add(talk.getIdx());
						}else if(rand > 4) {
							DisagreeTalkTypeList.add(TalkType.TALK);
							DisagreeTalkDayList.add(talk.getDay());
							DisagreeTalkIDList.add(talk.getIdx());
						}
					}else {
						//村人と予想した場合，同調候補リストへ追加（確率40%）
						int rand = new Random().nextInt(10);
						if(rand > 5) {
							AgreeTalkTypeList.add(TalkType.TALK);
							AgreeTalkDayList.add(talk.getDay());
							AgreeTalkIDList.add(talk.getIdx());
						}
					}
				//村人を対象とした場合
				}else {
					if(utterance.getRole() == Role.WEREWOLF) {
						//人狼と予想した場合，同調候補リストへ追加（確率50%）
						int rand = new Random().nextInt(10);
						if(rand > 4) {
							AgreeTalkTypeList.add(TalkType.TALK);
							AgreeTalkDayList.add(talk.getDay());
							AgreeTalkIDList.add(talk.getIdx());
						}
					}else {
						//村人と予想した場合，同調候補リストへ追加（確率10%）
						int rand = new Random().nextInt(10);
						if(rand > 8) {
							AgreeTalkTypeList.add(TalkType.TALK);
							AgreeTalkDayList.add(talk.getDay());
							AgreeTalkIDList.add(talk.getIdx());
						}
					}
				}
				break;
			case INQUESTED:
		    	//複数Inquestedを排除
		    	if(InquestedNum[getLatestDayGameInfo().getAgentList().indexOf(talk.getAgent())] > 1) continue;
		    	//人狼が騙っていたら追加
		    	if(WerewolfAgentList.contains(talk.getAgent()) && !WolfFakeMediumCOAgentList.contains(talk.getAgent())) {
		    		WolfFakeMediumCOAgentList.add(talk.getAgent());
		    	}
		    	//人狼を霊能した場合の処理
		    	if(WerewolfAgentList.contains(utterance.getTarget())) {
					switch (utterance.getResult()) {
					//人狼を白判定した場合
					case HUMAN:
			    		if(GrayAgentList.contains(talk.getAgent())) {
			    			GrayAgentList.remove(talk.getAgent());
			    			if(!WerewolfAgentList.contains(talk.getAgent())) {
			    				PosFakeMediumCOAgentList.add(talk.getAgent());
		         			}else {
		         				WolfFakeMediumCOAgentList.add(talk.getAgent());
		         			}
		         		}else if(MediumAgentList.contains(talk.getAgent())) {
		         			MediumAgentList.remove(talk.getAgent());
		         			if(!WerewolfAgentList.contains(talk.getAgent())) {
			         			PosFakeMediumCOAgentList.add(talk.getAgent());
		         			}else {
		         				WolfFakeMediumCOAgentList.add(talk.getAgent());
		         			}
		         		}
			    		FakeInquestedWhiteAgentList.add(utterance.getTarget());
			    		break;
			    	//人狼に黒判定した場合
					case WEREWOLF:
		         		if(GrayAgentList.contains(talk.getAgent())) {
		         			MediumAgentList.add(talk.getAgent());
			           		GrayAgentList.remove(talk.getAgent());
		         		}
		         		if(!AttackList.contains(talk.getAgent())) {
				    		AttackList.add(talk.getAgent());
		         		}
	         			if(WerewolfAgentList.contains(talk.getAgent())){
	         				//何もしない
	         			}else {
	         				//アタックレベル上昇
	         				AttackLevel[AttackList.indexOf(talk.getAgent())] = AttackLevel[AttackList.indexOf(talk.getAgent())] + 1;
	         				TalkAttackAgent = false;
	         			}
		         		MediumInquestedBlackAgentList.add(utterance.getTarget());
			    		break;
					}
					whispervote = false;
				//人狼以外を霊能
		    	}else {
					switch (utterance.getResult()) {
					case HUMAN:
			    		if(GrayAgentList.contains(talk.getAgent())) {
		         			MediumAgentList.add(talk.getAgent());
		         			GrayAgentList.remove(talk.getAgent());
		         		}
			    		MediumInquestedWhiteAgentList.add(utterance.getTarget());
						break;
					case WEREWOLF:
			    		if(GrayAgentList.contains(talk.getAgent())) {
		         			GrayAgentList.remove(talk.getAgent());
		         			if(!WerewolfAgentList.contains(talk.getAgent())) {
			         			PosFakeMediumCOAgentList.add(talk.getAgent());
		         			}else {
		         				WolfFakeMediumCOAgentList.add(talk.getAgent());
		         			}
		         		}else if(MediumAgentList.contains(talk.getAgent())) {
		         			MediumAgentList.remove(talk.getAgent());
		         		}
			    		FakeInquestedBlackAgentList.add(utterance.getTarget());
			    		break;
					}
				}
				InquestedNum[getLatestDayGameInfo().getAgentList().indexOf(talk.getAgent())]++;
				break;
			case OVER:
				if(!OverList.contains(talk.getAgent())) {
					OverList.add(talk.getAgent());
				}
				break;
			case SKIP:
				break;
			case VOTE:
				if(FinishVoteAgentList.contains(talk.getAgent())) {
					VotedAgentList.set(FinishVoteAgentList.indexOf(talk.getAgent()), utterance.getTarget());
				}else {
					FinishVoteAgentList.add(talk.getAgent());
					VotedAgentList.add(utterance.getTarget());
				}
				break;
			default:
				break;
	        }
	    }
	    TalkTurnNum = talkList.size();

	    //囁きについて
	    for(int ID = whisperTurnNum; ID < whisperList.size(); ID++){
	        Talk talk = whisperList.get(ID);
	        //発話をパース
	        Utterance utterance = new Utterance(talk.getContent());

	        //自分の発言はスルー
	    	if(talk.getAgent() == getMe()) continue;

	        //発話のトピックごとに処理
	        switch (utterance.getTopic()) {
			case AGREE:
				break;
			case ATTACK:
				//襲撃
    			if(!AttackList.contains(utterance.getTarget()) && !WerewolfAgentList.contains(utterance.getTarget())) {
	    			AttackList.add(utterance.getTarget());
     			}
         		if(WerewolfAgentList.contains(utterance.getTarget())){
         			//何もしない
         		}else if(AttackLevel[AttackList.indexOf(utterance.getTarget())] < 99){
         			//アタックレベル上昇
         			AttackLevel[AttackList.indexOf(utterance.getTarget())] = 99;
         			TalkAttackAgent = false;
					WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
					WhisperAgreeTalkDayList.add(talk.getDay());
					WhisperAgreeTalkIDList.add(talk.getIdx());
         		}
				break;
			case COMINGOUT:
		    	//複数COを排除
		    	if(CONum[getLatestDayGameInfo().getAgentList().indexOf(talk.getAgent())] > 1) continue;
		    	//COロールごとに処理
		    	switch (utterance.getRole()) {
		    	case SEER:
	    			WolfFakeSeerCOAgentList.add(talk.getAgent());
		    	case MEDIUM:
	    			WolfFakeMediumCOAgentList.add(talk.getAgent());
				default:
					break;
				}
		    	break;
			case DISAGREE:
				break;
			case DIVINED:
				//仲間の提案する占い先に対して同調する（確率50%）
				int rand = new Random().nextInt(10);
				if(rand > 4) {
					WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
					WhisperAgreeTalkDayList.add(talk.getDay());
					WhisperAgreeTalkIDList.add(talk.getIdx());
				}
				//たまに反論
				else if(rand < 1) {
					WhisperDisagreeTalkTypeList.add(TalkType.WHISPER);
					WhisperDisagreeTalkDayList.add(talk.getDay());
					WhisperDisagreeTalkIDList.add(talk.getIdx());
				}
				break;
			case ESTIMATE:
				//仲間の提案する予想先に対して同調する（確率50%）
				int rand2 = new Random().nextInt(10);
				if(rand2 > 4) {
					WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
					WhisperAgreeTalkDayList.add(talk.getDay());
					WhisperAgreeTalkIDList.add(talk.getIdx());
				}
				//たまに反論
				else if(rand2 < 1) {
					WhisperDisagreeTalkTypeList.add(TalkType.WHISPER);
					WhisperDisagreeTalkDayList.add(talk.getDay());
					WhisperDisagreeTalkIDList.add(talk.getIdx());
				}
				break;
			case GUARDED:
				int rand3 = new Random().nextInt(10);
				if(rand3 > 4) {
					WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
					WhisperAgreeTalkDayList.add(talk.getDay());
					WhisperAgreeTalkIDList.add(talk.getIdx());
				}
				break;
			case INQUESTED:
				int rand4 = new Random().nextInt(10);
				if(rand4 > 4) {
					WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
					WhisperAgreeTalkDayList.add(talk.getDay());
					WhisperAgreeTalkIDList.add(talk.getIdx());
				}
				break;
			case OVER:
				WhisOverList.add(talk.getAgent());
				break;
			case SKIP:
				break;
			case VOTE:
				//投票先一致行動
				if(PlanningVotedAgent != utterance.getTarget() || !VoteInLine) {
					int Voterand = new Random().nextInt(10);
					//生存者6人以下かつ人狼2人以上生存ならば人狼同士で投票先を合わせる
					if(getLatestDayGameInfo().getAliveAgentList().size() < 7 && WerewolfAgentList.size() > 1) {
						PlanningVotedAgent = utterance.getTarget();
						VoteInLine = true;
						WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
						WhisperAgreeTalkDayList.add(talk.getDay());
						WhisperAgreeTalkIDList.add(talk.getIdx());
						whispervote = false;
					}
					//生存者8人以下かつ人狼3人以上生存ならば人狼同士で投票先を合わせる
					else if(getLatestDayGameInfo().getAliveAgentList().size() < 9 && WerewolfAgentList.size() > 2) {
						PlanningVotedAgent = utterance.getTarget();
						VoteInLine = true;
						WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
						WhisperAgreeTalkDayList.add(talk.getDay());
						WhisperAgreeTalkIDList.add(talk.getIdx());
						whispervote = false;
					}
					//時々一致行動（確率90%）
					else if(Voterand > 0) {
						PlanningVotedAgent = utterance.getTarget();
						VoteInLine = true;
						WhisperAgreeTalkTypeList.add(TalkType.WHISPER);
						WhisperAgreeTalkDayList.add(talk.getDay());
						WhisperAgreeTalkIDList.add(talk.getIdx());
						whispervote = false;
					}
					//たまに反論
					else {
						WhisperDisagreeTalkTypeList.add(TalkType.WHISPER);
						WhisperDisagreeTalkDayList.add(talk.getDay());
						WhisperDisagreeTalkIDList.add(talk.getIdx());
					}
				}
				break;
			default:
				break;
	        }
	    }
	    whisperTurnNum = whisperList.size();
	}

	@Override
	public Agent attack() {
		return PlanningAttackedAgent;
	}

	//
	private Agent setAttackAgent() {
		if(AttackList.size() > 1) {
			int AttackTarget = 0;
			for(int n=1; n<AttackList.size(); n++) {
				if(AttackLevel[n] >= AttackLevel[n-1]) {
					AttackTarget = n;
				}
			}
			PlanningAttackedAgent = AttackList.get(AttackTarget);
		}else if(AttackList.size() == 1) {
			PlanningAttackedAgent = AttackList.get(0);
		}else if(GrayAgentList.size() > 0){
			PlanningAttackedAgent = randomSelect(GrayAgentList);
		}else if(MediumAgentList.size() > 0) {
			PlanningAttackedAgent = randomSelect(MediumAgentList);
		}else if(SeerAgentList.size() > 0) {
			PlanningAttackedAgent = randomSelect(SeerAgentList);
		}else {
			PlanningAttackedAgent = randomSelect(WerewolfAgentList);
		}
		//もし投票先と襲撃先が被ったら被らなくなるまで襲撃先を考え直す
		//ただし、残りプレイヤー数が3以下のときはこの限りではない
		if(getLatestDayGameInfo().getAliveAgentList().size() > 3) {
			for(int count=0; PlanningVotedAgent == PlanningAttackedAgent ; count++) {
				if(count > 20) {
					break;
				}else if(AttackList.size() > 1) {
					int AttackTarget = 0;
					for(int n=1; n<AttackList.size(); n++) {
						if(AttackLevel[n] > AttackLevel[n-1]) {
							AttackTarget = n;
						}
					}
					PlanningAttackedAgent = AttackList.get(AttackTarget);
				}else if(AttackList.size() == 1) {
					PlanningAttackedAgent = AttackList.get(0);
				}else if(GrayAgentList.size() > 0){
				}else if(MediumAgentList.size() > 0) {
					PlanningAttackedAgent = randomSelect(MediumAgentList);
				}else if(SeerAgentList.size() > 0) {
					PlanningAttackedAgent = randomSelect(SeerAgentList);
				}else {
					PlanningAttackedAgent = randomSelect(WerewolfAgentList);
				}
			}
		}
		//それでも被るなら
		if(PlanningVotedAgent == PlanningAttackedAgent){
			List<Agent> list = new ArrayList<Agent>();
			list = getLatestDayGameInfo().getAliveAgentList();
			list.remove(getMe());
			list.remove(PlanningVotedAgent);
			PlanningAttackedAgent = randomSelect(list);
		}
		return PlanningAttackedAgent;
	}

	@Override
	public String whisper() {

/*	    //3000ミリ秒待機する
	    try{
	      Thread.sleep(3000);
	    }catch (InterruptedException e){
	    }*/

		//初日について
		if(getLatestDayGameInfo().getDay() == 0) {
			//同調反論だけ
			if(WhisOverList.size()  < 3) {
				//同調
				if(WhisperAgreeTalkTypeList.size() > 0) {
					if(WhisperAgreeNum < WhisperAgreeTalkTypeList.size()) {
						TemplateTalkFactory.TalkType talktype = WhisperAgreeTalkTypeList.get(WhisperAgreeNum);
						int day = WhisperAgreeTalkDayList.get(WhisperAgreeNum);
						int id = WhisperAgreeTalkIDList.get(WhisperAgreeNum);
						WhisperAgreeNum++;
						String AgreeTalk = TemplateWhisperFactory.agree(talktype, day, id);
				    	return AgreeTalk;
					}
				}
				//反論
				if(WhisperDisagreeTalkTypeList.size() > 0) {
					if(WhisperDisagreeNum < WhisperDisagreeTalkTypeList.size()) {
						TemplateTalkFactory.TalkType talktype = WhisperDisagreeTalkTypeList.get(WhisperDisagreeNum);
						int day = WhisperDisagreeTalkDayList.get(WhisperDisagreeNum);
						int id = WhisperDisagreeTalkIDList.get(WhisperDisagreeNum);
						WhisperDisagreeNum++;
						String DisagreeTalk = TemplateWhisperFactory.disagree(talktype, day, id);
				    	return DisagreeTalk;
					}
				}
				int rand = new Random().nextInt(10);
				if(rand > 6) {
					return Talk.SKIP;
				}
			}
			return Talk.OVER;
		}

		//襲撃先決定および発言
		if(getLatestDayGameInfo().getDay() > 0 && !TalkAttackAgent) {
			PlanningAttackedAgent = setAttackAgent();
			TalkAttackAgent = true;
			String AttackTalk = TemplateWhisperFactory.attack(PlanningAttackedAgent);
			return AttackTalk;
		}

		//占われて黒判定されたとき、占い師あるいは霊媒師を騙る
		if(SeerDivinedBlackAgentList.contains(getMe()) && !Deceive) {
		    if(WolfFakeSeerCOAgentList.size() > 0) {
		    	FakeRole = Role.MEDIUM;
		    	WolfFakeMediumCOAgentList.add(getMe());
		    }else {
		    	FakeRole = Role.SEER;
		    	WolfFakeSeerCOAgentList.add(getMe());
		    }
	    	FirstCODay = true;
	    	Deceive = true;
		    String COTalk = TemplateWhisperFactory.comingout(getMe(), FakeRole);
/*		    //7000ミリ秒待機する
		    try{
		      Thread.sleep(7000);
		    }catch (InterruptedException e){
		    }*/
	    	return COTalk;
		}

		//全体会話でOverあるいは投票したら自分から騙らない
		if(!Overed || !vote) {
			//3人以上COしていなければ低確率で騙る．毎日少し確率UP．
			if(WolfFakeSeerCOAgentList.size() + WolfFakeMediumCOAgentList.size() < 1 && !Deceive) {
				int num = new Random().nextInt(20);
				num = num + getLatestDayGameInfo().getDay();
				if(num > 18) {
				    if(WolfFakeSeerCOAgentList.size() > 0 ) {
				    	FakeRole = Role.MEDIUM;
				    	WolfFakeMediumCOAgentList.add(getMe());
				    }else {
				    	FakeRole = Role.SEER;
					    WolfFakeSeerCOAgentList.add(getMe());
				    }
			    	FirstCODay = true;
			    	Deceive = true;
				    String COTalk = TemplateWhisperFactory.comingout(getMe(), FakeRole);
/*				    //7000ミリ秒待機する
				    try{
				      Thread.sleep(7000);
				    }catch (InterruptedException e){
				    }*/
			    	return COTalk;
				}
			}
		}

		//COしたら偽占いが終わるまでOverを返す
		if(FirstCODay && !COTalked) return Talk.OVER;

		//偽占い
		if(WolfFakeSeerCOAgentList.contains(getMe()) && FakeDivinedNum == FakeDivinedTalkNum) {
			//占いCOした日にそれまでも占っていたはずの回数だけ占い結果を発言．
			if(FirstCODay && COTalked) {
				//占うべき人がいないときは仕方がないので自分を占う．
				if(FakeMyDiviningAgentList.size() == 0) {
					DivinedAgent = getMe();
				//占い対象リストからプレイヤーをランダムに選出．
				}else {
					DivinedAgent = randomSelect(FakeMyDiviningAgentList);
				}
				//もし生き残っている村人プレイヤーが人狼+2以下であり，かつ対象のプレイヤーが人狼でなければ人狼判定を出す．
				if(getLatestDayGameInfo().getAliveAgentList().size() < WerewolfAgentList.size() + 2 && !WerewolfAgentList.contains(DivinedAgent)) {
					if(WerewolfAgentList.size() > 1 && FakeMyDivinedBlackAgentList.size() < 3) {
						DivinedColor = Species.WEREWOLF;
						FakeMyDivinedBlackAgentList.add(DivinedAgent);
						FakeDivinedBlackAgentList.add(DivinedAgent);
					}else {
						DivinedColor = Species.HUMAN;
						FakeMyDivinedWhiteAgentList.add(DivinedAgent);
						FakeDivinedWhiteAgentList.add(DivinedAgent);
					}
				//もし生き残っているプレイヤーが4人以下（人狼1）であり，かつ対象のプレイヤーが人狼でなければ人狼判定を出す．
				}else if(getLatestDayGameInfo().getAliveAgentList().size() < 5 && FakeMyDivinedBlackAgentList.size() < 2) {
					DivinedColor = Species.WEREWOLF;
					FakeMyDivinedBlackAgentList.add(DivinedAgent);
					FakeDivinedBlackAgentList.add(DivinedAgent);
				//占い対象プレイヤーが霊能者から黒出しされているプレイヤーなら便乗して黒判定を出しておく（既に死んだプレイヤー）．
				}else if(MediumInquestedBlackAgentList.contains(DivinedAgent)) {
					DivinedColor = Species.WEREWOLF;
					FakeMyDivinedBlackAgentList.add(DivinedAgent);
					FakeDivinedBlackAgentList.add(DivinedAgent);
				//それ以外．基本的に白判定．
				}else {
					DivinedColor = Species.HUMAN;
					FakeMyDivinedWhiteAgentList.add(DivinedAgent);
					FakeDivinedWhiteAgentList.add(DivinedAgent);
				}
				//人狼を黒判定した占い師が生きていれば黒判定をやり返す．
				if(SeerDivinedBlackAgentList.size()*SeerAgentList.size() > 0 && WerewolfAgentList.containsAll(SeerDivinedBlackAgentList)) {
					if(FakeMyDiviningAgentList.containsAll(SeerAgentList)){
						DivinedAgent = randomSelect(SeerAgentList);
						DivinedColor = Species.WEREWOLF;
						FakeMyDivinedBlackAgentList.add(DivinedAgent);
						FakeDivinedBlackAgentList.add(DivinedAgent);
					}
				}

				//占ったプレイヤーを除去
				FakeMyDiviningAgentList.remove(DivinedAgent);
				//占い回数カウント
				FakeDivinedNum++;
				//黒出しが死んでいたら除去
				if(!getLatestDayGameInfo().getAliveAgentList().containsAll(FakeDivinedBlackAgentList)) {
					FakeDivinedBlackAgentList.removeAll(DeadAgentList);
				}

			    //CO日までの占い結果を報告したら終わり
			    if(FakeDivinedNum >= getLatestDayGameInfo().getDay()) {
			    	FirstCODay = false;
			    }

				//発言
				String DivineTalk = TemplateWhisperFactory.divined(DivinedAgent, DivinedColor);
/*			    //5000ミリ秒待機する
			    try{
			      Thread.sleep(5000);
			    }catch (InterruptedException e){
			    }*/
		    	return DivineTalk;

		    //占い結果を一日一回発言．死んだプレイヤーも占っていた可能性ありとする．
			}else if(FakeDivinedNum < getLatestDayGameInfo().getDay() && !FirstCODay) {

				//占うべき人がいないときは仕方がないので自分を占う．
				if(FakeMyDiviningAgentList.size() == 0) {
					DivinedAgent = getMe();
				//占い対象リストからプレイヤーをランダムに選出．
				}else {
					DivinedAgent = randomSelect(FakeMyDiviningAgentList);
				}

				//人狼を黒判定した占い師が生きていれば黒判定をやり返す．
				if(SeerDivinedBlackAgentList.size()*SeerAgentList.size() > 0 && WerewolfAgentList.containsAll(SeerDivinedBlackAgentList)) {
					DivinedAgent = randomSelect(SeerAgentList);
					DivinedColor = Species.WEREWOLF;
					FakeMyDivinedBlackAgentList.add(DivinedAgent);
					FakeDivinedBlackAgentList.add(DivinedAgent);
				//もし生き残っているプレイヤーが6人以下（人狼2）であり，かつ対象のプレイヤーが人狼でなければ人狼判定を出す．
				}else if(getLatestDayGameInfo().getAliveAgentList().size() < 7 && !WerewolfAgentList.contains(DivinedAgent)) {
					if(WerewolfAgentList.size() > 1 && FakeMyDivinedBlackAgentList.size() < 2) {
						DivinedColor = Species.WEREWOLF;
						FakeMyDivinedBlackAgentList.add(DivinedAgent);
						FakeDivinedBlackAgentList.add(DivinedAgent);
					}else {
						DivinedColor = Species.HUMAN;
						FakeMyDivinedWhiteAgentList.add(DivinedAgent);
						FakeDivinedWhiteAgentList.add(DivinedAgent);
					}
				//もし生き残っているプレイヤーが4人以下（人狼1）であり，かつ対象のプレイヤーが人狼でなければ人狼判定を出す．
				}else if(getLatestDayGameInfo().getAliveAgentList().size() < 5 && FakeMyDivinedBlackAgentList.size() < 2) {
					DivinedColor = Species.WEREWOLF;
					FakeMyDivinedBlackAgentList.add(DivinedAgent);
					FakeDivinedBlackAgentList.add(DivinedAgent);
				//占い対象プレイヤーが霊能者から黒出しされているプレイヤーなら便乗して黒判定を出しておく（既に死んだプレイヤー）．
				}else if(MediumInquestedBlackAgentList.contains(DivinedAgent)) {
					DivinedColor = Species.WEREWOLF;
					FakeMyDivinedBlackAgentList.add(DivinedAgent);
					FakeDivinedBlackAgentList.add(DivinedAgent);
				//それ以外．基本的に白判定．
				}else {
					DivinedColor = Species.HUMAN;
					FakeMyDivinedWhiteAgentList.add(DivinedAgent);
					FakeDivinedWhiteAgentList.add(DivinedAgent);
				}

				//占ったプレイヤー，および死んだプレイヤーを除去
			    FakeMyDiviningAgentList.remove(DivinedAgent);
				FakeMyDiviningAgentList.remove(getLatestDayGameInfo().getExecutedAgent());
				FakeMyDiviningAgentList.remove(getLatestDayGameInfo().getAttackedAgent());
			    //占い回数カウント
				FakeDivinedNum++;
				//黒出しが死んでいたら除去
				if(!getLatestDayGameInfo().getAliveAgentList().containsAll(FakeDivinedBlackAgentList)) {
					FakeDivinedBlackAgentList.removeAll(DeadAgentList);
				}
				whispervote = false;
				//発言
			    String DivineTalk = TemplateWhisperFactory.divined(DivinedAgent, DivinedColor);
/*			    //7000ミリ秒待機する
			    try{
			      Thread.sleep(7000);
			    }catch (InterruptedException e){
			    }*/
				return DivineTalk;
			}
		}

		//同調
		if(WhisperAgreeTalkTypeList.size() > 0) {
			if(WhisperAgreeNum < WhisperAgreeTalkTypeList.size()) {
				TemplateTalkFactory.TalkType talktype = WhisperAgreeTalkTypeList.get(WhisperAgreeNum);
				int day = WhisperAgreeTalkDayList.get(WhisperAgreeNum);
				int id = WhisperAgreeTalkIDList.get(WhisperAgreeNum);
				WhisperAgreeNum++;
				String AgreeTalk = TemplateWhisperFactory.agree(talktype, day, id);
		    	return AgreeTalk;
			}
		}

		//反論
		if(WhisperDisagreeTalkTypeList.size() > 0) {
			if(WhisperDisagreeNum < WhisperDisagreeTalkTypeList.size()) {
				TemplateTalkFactory.TalkType talktype = WhisperDisagreeTalkTypeList.get(WhisperDisagreeNum);
				int day = WhisperDisagreeTalkDayList.get(WhisperDisagreeNum);
				int id = WhisperDisagreeTalkIDList.get(WhisperDisagreeNum);
				WhisperDisagreeNum++;
				String DisagreeTalk = TemplateWhisperFactory.disagree(talktype, day, id);
		    	return DisagreeTalk;
			}
		}

		//DecideVoteフラグが立ってまだvoteしていないときvote宣言
		if(VoteInLine && !whispervote) {
			whispervote = true;
			vote = false;
			String WerewolfVoteTalk = TemplateWhisperFactory.vote(PlanningVotedAgent);
		    //7000ミリ秒待機する
/*		    try{
		      Thread.sleep(7000);
		    }catch (InterruptedException e){
		    }*/
			return WerewolfVoteTalk;
		}
		//
		else if(DecideVote && !whispervote) {
			whispervote = true;
			vote = false;
			String WerewolfVoteTalk = TemplateWhisperFactory.vote(PlanningVotedAgent = setPlanningVotedAgent());
		    //7000ミリ秒待機する
/*		    try{
		      Thread.sleep(7000);
		    }catch (InterruptedException e){
		    }*/
			return WerewolfVoteTalk;
		}

		return Talk.OVER;
	}

	@Override
	public String talk() {
		//初日の行動
		if(getLatestDayGameInfo().getDay() == 0){
			//たまにCO
			int rand = new Random().nextInt(20);
			if(rand > 18 && !FirstDayCOed){
				String CO = TemplateTalkFactory.comingout(getMe(), Role.VILLAGER);
				FirstDayCOed = true;
				return CO;
			}else {
				return Talk.OVER;
			}
		}

		//とりあえず一旦投票先を選択
		if(FirstTalk) {
			PlanningVotedAgent = setPlanningVotedAgent();
			FirstTalk = false;
		}


		//カミングアウト
		if(FirstCODay && !COTalked) {
			String COTalk = TemplateTalkFactory.comingout(getMe(), FakeRole);
			COTalked = true;
			return COTalk;
		}

		//偽占い発言
	    if(FakeDivinedNum > FakeDivinedTalkNum) {
	    	String DivineTalk = TemplateTalkFactory.divined(DivinedAgent, DivinedColor);
	    	FakeDivinedTalkNum++;
	    	return DivineTalk;
	    }

		//偽霊能
		if(WolfFakeMediumCOAgentList.contains(getMe())) {
			//霊能対象リストに人がいる限り霊能結果を発言．
			if(FakeMyInquestingAgentList.size() > 0) {
				//誰を何と霊能するか
				Agent InquestedAgent = randomSelect(FakeMyInquestingAgentList);
				Species InquestedColor;

				//黒判定が出ていてかつ黒を出した占い師が襲撃されていれば人狼に対して黒判定を出す．
				if(SeerDivinedBlackAgentList.contains(InquestedAgent) && AttackedAgentList.containsAll(SeerAgentList)) {
					InquestedColor = Species.WEREWOLF;
					MediumInquestedBlackAgentList.add(InquestedAgent);
				//人狼に対しては白判定を出す．
				}else if(WerewolfAgentList.contains(InquestedAgent)) {
					InquestedColor = Species.HUMAN;
					MediumInquestedWhiteAgentList.add(InquestedAgent);
				//もし生き残りプレイヤー数が3ならば人狼判定を出す．
				}else if(getLatestDayGameInfo().getAliveAgentList().size() == 3) {
					InquestedColor = Species.WEREWOLF;
					MediumInquestedBlackAgentList.add(InquestedAgent);
				//もし偽占いにより黒判定を出したプレイヤーを霊能したら黒判定を出す．
				}else if(FakeMyDivinedBlackAgentList.contains(InquestedAgent)) {
					InquestedColor = Species.WEREWOLF;
					MediumInquestedBlackAgentList.add(InquestedAgent);
				//それ以外．基本的に白判定．
				}else {
					InquestedColor = Species.HUMAN;
					MediumInquestedWhiteAgentList.add(InquestedAgent);
				}

				//発言
			    String InquestTalk = TemplateTalkFactory.inquested(InquestedAgent, InquestedColor);
			    //霊能したプレイヤーを除去
			    FakeMyInquestingAgentList.remove(InquestedAgent);

		    	return InquestTalk;
			}
		}

		//同調
		if(AgreeTalkTypeList.size() > 0) {
			if(AgreeNum < AgreeTalkTypeList.size()) {
				TemplateTalkFactory.TalkType talktype = AgreeTalkTypeList.get(AgreeNum);
				int day = AgreeTalkDayList.get(AgreeNum);
				int id = AgreeTalkIDList.get(AgreeNum);
				AgreeNum++;
				String AgreeTalk = TemplateTalkFactory.agree(talktype, day, id);
		    	return AgreeTalk;
			}
		}

		//反論
		if(DisagreeTalkTypeList.size() > 0) {
			if(DisagreeNum < DisagreeTalkTypeList.size()) {
				TemplateTalkFactory.TalkType talktype = DisagreeTalkTypeList.get(DisagreeNum);
				int day = DisagreeTalkDayList.get(DisagreeNum);
				int id = DisagreeTalkIDList.get(DisagreeNum);
				DisagreeNum++;
				String DisagreeTalk = TemplateTalkFactory.disagree(talktype, day, id);
		    	return DisagreeTalk;
			}
		}

	    //投票
	    if(!vote && whispervote){
	    	String VoteTalk = TemplateTalkFactory.vote(PlanningVotedAgent);
	    	vote = true;
	    	return VoteTalk;
	    }

		//全員overしていたら自分もover
		if(OverList.size() + 1 == getLatestDayGameInfo().getAliveAgentList().size()) {
			Overed = true;
			return Talk.OVER;
		}
	    //Skip何回か
		if(SkipNum < 2) {
			int rand = new Random().nextInt(20);
			if(rand > 9) {
		    	DecideVote = true;
			}else {
			}
			SkipNum++;
			return Talk.SKIP;

		}

	    //話すことが無ければ会話終了
	    Overed = true;
	    return Talk.OVER;
	}

	@Override
	public Agent vote() {
		return PlanningVotedAgent;
	}

	//投票方法
	private Agent setPlanningVotedAgent() {
		//偽ブラックリストに誰か入っていればそのプレイヤーをランダムに選定
		if(!FakeDivinedBlackAgentList.isEmpty() && FakeDivinedBlackAgentList != null) {
			return randomSelect(FakeDivinedBlackAgentList);
		//ブラックリストに誰か入っていればそのプレイヤーの中からランダムに選定
		}else if(!BlackAgentList.isEmpty() && BlackAgentList != null){
			return randomSelect(BlackAgentList);
		//2重占い師COのとき相手に投票
		}else if(WolfFakeSeerCOAgentList.size()*SeerAgentList.size() > 0) {
			return randomSelect(SeerAgentList);
		//自分以外の人狼が占われ黒判定が出ていた場合、過半数がそのプレイヤーに投票しているようなら自分も同調する．
		}else if(SeerDivinedBlackAgentList.size()*VotedAgentList.size() > 0 && !SeerDivinedBlackAgentList.contains(getMe())) {
			//投票数の一番多いプレイヤーに投票
			int[] MostVotedAgent = new int[VotedAgentList.size()];
			for(int m=0; m<VotedAgentList.size(); m++) {
				for(int n=m+1; n<VotedAgentList.size(); n++) {
					if(VotedAgentList.get(m) == VotedAgentList.get(n)) {
						MostVotedAgent[m]++;
					}
				}
			}
			int MostVotedAgentNum = 0;
			for(int n=0; n<VotedAgentList.size(); n++) {
				if(MostVotedAgent[n] > MostVotedAgentNum) {
					MostVotedAgentNum = n;
				}
			}
			return VotedAgentList.get(MostVotedAgentNum);
		//占い師を騙っていた場合，白判定した相手には投票しない
		}else if(FakeMyDivinedWhiteAgentList.size() > 0 && GrayAgentList.size() > FakeMyDivinedWhiteAgentList.size()) {
			List<Agent> VoteList = new ArrayList<Agent>();
			VoteList.addAll(GrayAgentList);
			VoteList.removeAll(FakeMyDivinedWhiteAgentList);
			return randomSelect(VoteList);
		//人狼以外のまだ占われてないプレイヤーから投票
		}else if(GrayAgentList.size() > 0){
			if(GrayAgentList.size() > SeerDivinedWhiteAgentList.size()) {
				List<Agent> VoteList = new ArrayList<Agent>();
				VoteList.addAll(GrayAgentList);
				VoteList.removeAll(SeerDivinedWhiteAgentList);
				return randomSelect(VoteList);
			}else {
				return randomSelect(GrayAgentList);
			}
		//能無し→霊能→占い→自身の順で投票
		}else if(VillagerAgentList.size() > 0) { return randomSelect(VillagerAgentList);
		}else if(MediumAgentList.size() > 0) { return randomSelect(MediumAgentList);
		}else if(SeerAgentList.size() > 0) { return randomSelect(SeerAgentList);
		}else {	return randomSelect(WerewolfAgentList);
		}
	}

	//エージェント番号の取得
	private Integer getAgentNum(Agent agent) {
		return getLatestDayGameInfo().getAgentList().indexOf(agent);
	}

	//引数のAgentのリストからランダムにAgentを選択する
	private Agent randomSelect(List<Agent> agentList){
	    int num = new Random().nextInt(agentList.size());
	    return agentList.get(num);
	}

	//finishメソッド
	@Override
	public void finish() {
		// TODO 自動生成されたメソッド・スタブ
	}
}
