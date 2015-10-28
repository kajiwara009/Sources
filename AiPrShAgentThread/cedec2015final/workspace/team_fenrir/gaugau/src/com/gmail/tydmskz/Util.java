package com.gmail.tydmskz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Status;
import org.aiwolf.common.data.Talk;
import org.aiwolf.common.data.Vote;
import org.aiwolf.common.net.GameInfo;

import com.gmail.tydmskz.TalkParser.TalkType;

public class Util {
	static
	{
		// 事前計算
		m_roleNumber = org.aiwolf.common.data.Role.values().length;
	}
	
	public static Random Rand = new Random();

	class AgentConnection
	{
		Agent _from = null;
		Agent _to = null;
		public Agent From()
		{
			return _from;
		};
		public Agent To()
		{
			return _to;
		}
		public AgentConnection(Agent from, Agent to)
		{
			_from = from;
			_to = to;
		}
	}


	public enum AgentType { HUMAN, WEREWOLF };
	//特定のn人の票が重なる確率 どれぐらいの奇跡を起こしたか
	static public float VoteProbability(int nPlayer, int nVote)
	{
		float tmp = (float)(nPlayer - nVote)*(float)Math.pow(1.0 / (nPlayer - nVote), nVote);
		return 1.0f - tmp;
	}

	//票を入れた人たちがグルだったと仮定してどれ位殺意を抱いていたか,その票を入れた人以外がランダムに投票した場合どれぐらいの確率で吊られるか
	static public float HangProbability(int nPlayer, int nVote)
	{
		float[][] table = {
				//0:
				{Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//1:
				{Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//2:
				{Float.NaN,0.4999822f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//3:
				{Float.NaN,0.1666342f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//4:
				{Float.NaN,0.05551672f,0.8333067f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//5:
				{Float.NaN,0.01716697f,0.6560883f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//6:
				{Float.NaN,0.005232632f,0.500997f,0.9800364f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//7:
				{Float.NaN,0.001603365f,0.3833491f,0.9452298f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//8:
				{Float.NaN,0.0004850626f,0.2965814f,0.9033717f,0.9983379f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//9:
				{Float.NaN,0.0001575947f,0.2328744f,0.8586784f,0.9947422f,1f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//10:
				{Float.NaN,5.078316E-05f,0.1849449f,0.8137271f,0.989453f,0.9999076f,1f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//11:
				{Float.NaN,1.507998E-05f,0.1484817f,0.7697916f,0.9829987f,0.9996563f,1f,1f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,Float.NaN,},
				//12:
				{Float.NaN,5.066395E-06f,0.1203018f,0.7276019f,0.9755434f,0.9992366f,0.999996f,1f,1f,1f,1f,1f,Float.NaN,Float.NaN,Float.NaN,},
				//13:
				{Float.NaN,1.966953E-06f,0.09817439f,0.6872217f,0.9673867f,0.9986212f,0.9999829f,1f,1f,1f,1f,1f,1f,Float.NaN,Float.NaN,},
				//14:
				{Float.NaN,8.34465E-07f,0.08062029f,0.6494149f,0.958775f,0.997843f,0.9999598f,0.9999998f,1f,1f,1f,1f,1f,1f,Float.NaN,},
				//15:
				{Float.NaN,1.192093E-07f,0.06668413f,0.6135311f,0.9497429f,0.9969248f,0.9999207f,0.9999994f,1f,1f,1f,1f,1f,1f,1f,},


		};

		return table[nPlayer][nVote];
	}

	//確率のテーブルから少なくとも一回起こる確率を計算する(1-一回も起こらない確率)
	static public float[][] MultipleProbability(float[][][] p)
	{
		int il = p.length;//[i][j][k]
		int jl = p[0].length;
		int kl = p[0][0].length;

		float[][] ret = new float[jl][kl];

		for(int k=0;k<kl;k++)
		{
			for(int j=0;j<jl;j++)
			{
				float tmp = 1.f;
				for(int i=0;i<il;i++)
				{
					tmp *= 1.f - p[i][j][k];//怒らない確率
				}

				ret[j][k] = 1.f - tmp;
			}
		}

		return ret;
	}
	static public float[][] AverageProbability(float[][][] p)
	{
		int il = p.length;//[i][j][k]
		int jl = p[0].length;
		int kl = p[0][0].length;

		float[][] ret = new float[jl][kl];

		for(int k=0;k<kl;k++)
		{
			for(int j=0;j<jl;j++)
			{
				float tmp = 0.f;
				for(int i=0;i<il;i++)
				{
					tmp += p[i][j][k];//怒らない確率
				}

				ret[j][k] = tmp / (float)il;
			}
		}

		return ret;
	}
	//狼に投票した割合
	static float VoteRatio(Map<Agent, Integer> c, Map<Agent, Float> fact)
	{
		int cnt = 0;
		float sum = 0.f;
		for(Map.Entry<Agent, Integer> x : c.entrySet())
		{
			Agent a = x.getKey();

			float rate = 0.01f;//グレー
			if(fact.containsKey(a))
			{
				rate = fact.get(a);
			}

			cnt += x.getValue();
			sum += rate * x.getValue();
		}

		return sum / cnt;
	}

	static public Map<Agent, Float> FactVillage(Map<Integer, GameInfo> gameInfos)
	{
		Map<Agent, Float> factVillage = new HashMap<Agent, Float>();

		//for(int day = 0;day<nDay;day++)
		List<Agent> attacked = new ArrayList<Agent>();

		for(Map.Entry<Integer, GameInfo> x : gameInfos.entrySet())
		{
			GameInfo info = x.getValue();
			//夜に襲撃された人は狼ではない

			Agent a = info.getAttackedAgent();
			if(a!=null)
			{
				factVillage.put(a, 0.f);
				attacked.add(a);
			}

		}

		//襲撃された人が入っていたことは全て真実として扱う
		/*
		if(false)
		{
			Set<Agent> realSeer = new HashSet<Agent>();
			Set<Agent> realMedium = new HashSet<Agent>();
			Set<Agent> realBodyguard = new HashSet<Agent>();
			List<Judge> realJudge = new ArrayList<Judge>();


			GameInfo latestDay = gameInfos.get(gameInfos.size() - 1);
			List<Agent> alive = latestDay.getAliveAgentList();
			List<Agent> agents = latestDay.getAgentList();

			for(Map.Entry<Integer, GameInfo> x : gameInfos.entrySet())
			{
				for(Talk t: x.getValue().getTalkList())
				{
					String tstr = t.getContent();

					if(attacked.contains(t.getAgent()))
					{

						TalkType tp = TalkParser.GetTalkType(tstr);

						if(tp==TalkType.COMINGOUT)
						{
							Agent a = TalkParser.GetTalkTarget(tstr, agents);
							Role r = TalkParser.GetTalkComingoutRole(tstr);

							if(r==Role.SEER)
							{
								realSeer.add(a);
							}
							else if(r==Role.MEDIUM)
							{
								realMedium.add(a);
							}
							else if(r==Role.BODYGUARD)
							{
								realBodyguard.add(a);
							}
						}
						else if(tp==TalkType.DIVINED)
						{
							Agent a = TalkParser.GetTalkTarget(tstr, agents);
							Species s = TalkParser.GetTalkSpecies(tstr);

							Judge j = new Judge(t.getDay(), t.getAgent(), a, s);
							realJudge.add(j);
						}
						else if(tp==TalkType.INQUESTED)
						{
							Agent a = TalkParser.GetTalkTarget(tstr, agents);
							Species s = TalkParser.GetTalkSpecies(tstr);

							Judge j = new Judge(t.getDay(), t.getAgent(), a, s);
							realJudge.add(j);
						}
						else if(tp==TalkType.ESTIMATE)
						{
							Agent a = TalkParser.GetTalkTarget(tstr, agents);
							Role r = TalkParser.GetTalkComingoutRole(tstr);

							if(r==Role.SEER)
							{
								realSeer.add(a);
							}
							else if(r==Role.MEDIUM)
							{
								realMedium.add(a);
							}
							else if(r==Role.BODYGUARD)
							{
								realBodyguard.add(a);
							}
						}
					}
				}
			}

			List<Agent> fakeSeer = Util.GetCOAgents(gameInfos, Role.SEER);
			fakeSeer.removeAll(realSeer);
			for(Agent a : fakeSeer)
			{
				factVillage.put(a, 0.5f);
			}


			List<Agent> fakeMedium = Util.GetCOAgents(gameInfos, Role.MEDIUM);
			fakeMedium.removeAll(realMedium);

			List<Agent> fakeBodyguard = Util.GetCOAgents(gameInfos, Role.BODYGUARD);
			fakeBodyguard.removeAll(realBodyguard);
		}*/

		return factVillage;
	}
	//今の状態からそれぞれが狼である確率を計算する
	static public Map<Agent, Float> WerewolfProbability(Map<Integer, GameInfo> gameInfos, Map<Agent, Float> fact)
	{
		final int nPlayer = 15;
		final int nDay = gameInfos.size();

		//それぞれのagentの生存日数
		Map<Agent, Integer> life = new HashMap<Agent, Integer>();
		for(int day = 0;day<nDay;day++)
		{
			GameInfo info = gameInfos.get(day);
			for(Agent a : info.getAliveAgentList())
			{
				if(!life.containsKey(a))
					life.put(a, 0);

				life.put(a, life.get(a)+1);
			}
		}



		//投票の統計
		List<Vote> votes = new ArrayList<Vote>();
		for(int day = 0;day<nDay;day++)
		{
			GameInfo info = gameInfos.get(day);
			votes.addAll(info.getVoteList());
		}

		//いろいろ考える
		Map<Agent, Float> ret = new HashMap<Agent, Float>();
		for(Agent a : gameInfos.get(0).getAgentList())
		{

			//aが投票した先
			Map<Agent, Integer> voteTarget = new HashMap<Agent, Integer>();
			//aと一緒に投票した人たち
			Map<Agent, Integer> voteWith = new HashMap<Agent, Integer>();

			for(Vote v : votes)
			{

				if(v.getAgent()==a)
				{
					Agent t = v.getTarget();

					if(!voteTarget.containsKey(t))
						voteTarget.put(t, 0);

					voteTarget.put(t, voteTarget.get(t) + 1);
				}

				if(v.getAgent()==a)
				{
					Agent t = v.getTarget();
					int d = v.getDay();

					for(Vote u : votes)
					{
						if(u.getTarget()==t && u.getDay()==d)
						{
							Agent tmp = u.getAgent();
							if(!voteWith.containsKey(tmp))
								voteWith.put(tmp, 0);

							voteWith.put(tmp, voteWith.get(tmp)+1);
						}
					}
				}
			}


			float tRatio = 1.f - VoteRatio(voteTarget, fact);
			float wRatio = VoteRatio(voteWith, fact);
			float mix = 0.5f;//パラメータ

			float[] values = { tRatio, wRatio };
			float[] coefficients = { mix, 1.f-mix };
			float result = LinearCombine(coefficients, values);

			if(fact.containsKey(a))
			{
				ret.put(a, fact.get(a));
			}
			else
			{
				ret.put(a, result);
			}
		}

		return ret;
	}

	// 引数valuesと係数coefficientsをそれぞれの要素に対して積算し、全て足したものを返す
	static public float LinearCombine(float[] coefficients, float[] values)
	{
		if(coefficients.length != values.length)
		{
			throw new IllegalArgumentException("coefficentsとvalueの長さが違います.");
		}

		float result = 0.0f;
		for(int i=0; i<coefficients.length; i++)
		{
			result += coefficients[i] * values[i];
		}
		return result;
	}

	//自分を除いたランダムなagentを返す
	static public Agent SelectRandom(List<Agent> agents, Agent me)
	{
		List<Agent> tmp = new ArrayList<Agent>();
		tmp.addAll(agents);
		tmp.remove(me);

		if(tmp.size()==0)
			return null;

		Random rnd = new Random();
		return tmp.get(rnd.nextInt(tmp.size()));
	}

	//今の状態で狼が最大何人イルカ？
	static public int MaxWolfCount(GameInfo info)
	{
		final int game_max_wolf = 3;
		int np = info.getAgentList().size();

		int ret = Math.min(game_max_wolf, (np-1)/2);
		return ret;
	}

	static public List<Map.Entry<Agent,Float>> GetSortedList(Map<Agent, Float> agents)
	{
		List<Map.Entry<Agent,Float>> sorted = new ArrayList<Map.Entry<Agent,Float>>(agents.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<Agent,Float>>() {

			@Override
			public int compare(
					Entry<Agent,Float> entry1, Entry<Agent,Float> entry2) {
				return ((Float)entry1.getValue()).compareTo((Float)entry2.getValue());
			}

		});
		return sorted;
	}
	//もっとも大きいvを持つkeyを返す
	static public Agent SelectMostLikeWolf(Map<Agent, Float> agents)
	{

		if(agents.size()==0)
			return null;

		Random rnd = new Random();

		Map<Agent, Float> tmp = new HashMap<Agent, Float>();

		for(Map.Entry<Agent, Float> x : agents.entrySet())
		{
			if(x.getValue().equals(Float.NaN))
			{
				tmp.put(x.getKey(), rnd.nextFloat());
			}
			else
			{
				tmp.put(x.getKey(), x.getValue());
			}
		}

		float hi = -Float.MAX_VALUE;
		for(Map.Entry<Agent, Float> x : tmp.entrySet())
		{
			float ft = x.getValue();
			hi = Math.max(hi, ft);
		}


		List<Agent> retCandidate = new ArrayList<Agent>();
		for(Map.Entry<Agent, Float> x : tmp.entrySet())
		{
			if(hi==x.getValue())
			{
				retCandidate.add(x.getKey());
			}
		}

/*
		if(retCandidate.size()==0)
		{
			int du=0;du++;
			SelectMostLikeWolf(agents);
		}
	*/
		Agent ret = retCandidate.get(rnd.nextInt(retCandidate.size()));

		return ret;

	}

	//もっとも小さいvをもつkeyを返す
	static public Agent SelectgMostLikeHuman(Map<Agent, Float> agents)
	{
		if(agents.size()==0)
			return null;

		Random rnd = new Random();

		Map<Agent, Float> tmp = new HashMap<Agent, Float>();

		for(Map.Entry<Agent, Float> x : agents.entrySet())
		{
			if(x.getValue().equals(Float.NaN))
			{
				tmp.put(x.getKey(), rnd.nextFloat());
			}
			else
			{
				tmp.put(x.getKey(), x.getValue());
			}
		}

		float lo = Float.MAX_VALUE;
		for(Map.Entry<Agent, Float> x : tmp.entrySet())
		{
			float ft = x.getValue().floatValue();
			lo = Math.min(lo, ft);
		}


		List<Agent> retCandidate = new ArrayList<Agent>();
		for(Map.Entry<Agent, Float> x : tmp.entrySet())
		{
			if(lo==x.getValue())
			{
				retCandidate.add(x.getKey());
			}
		}


		Agent ret = retCandidate.get(rnd.nextInt(retCandidate.size()));

		return ret;
	}

	//過去に特定のロールを宣言したagentを探す
	/*
	 //遅いのでvillageinformationに移動
	static public List<Agent> GetCOAgents(Map<Integer, GameInfo> gameInfos, Role r)
	{
		List<Agent> agents = gameInfos.get(0).getAgentList();

		List<Agent> ret = new ArrayList<Agent>();

		for(Map.Entry<Integer, GameInfo> info : gameInfos.entrySet())
		{
			List<Talk> talks = info.getValue().getTalkList();

			for(Talk t : talks)

			{
				String tstr = t.getContent();
				TalkParser.TalkType tt = TalkParser.GetTalkType(tstr);

				if(tt==TalkParser.TalkType.COMINGOUT)
				{
					Role cr = TalkParser.GetTalkComingoutRole(tstr);
					if(cr!=r) continue;

					Agent a = TalkParser.GetTalkTarget(tstr, agents);
					Agent b = t.getAgent();

					if(a!=b) continue;//本人以外によるCO
					if(ret.contains(a)) continue; //すでにCO

					ret.add(a);
				}
			}
		}
		return ret;
	}*/

	//むちゃくちゃなことを言っているagentを検出する
	static public List<Agent> GetRandomAgent(Map<Integer, GameInfo> gameInfos)
	{
		List<Agent> ret = new ArrayList<Agent>();

		for(Map.Entry<Integer, GameInfo> info : gameInfos.entrySet())
		{
			List<Talk> talks = info.getValue().getTalkList();

			//agreeとdisagree、自分自身の狼のCOを見る
			for(Talk t : talks)
			{
				TalkParser.TalkType tt = TalkParser.GetTalkType(t.getContent());

				if(tt==TalkType.AGREE || tt==TalkType.DISAGREE)
				{
					TalkParser.TalkArea ta = TalkParser.GetTalkArea(t.getContent());
					int day = TalkParser.GetTalkDay(t.getContent());
					int id = TalkParser.GetTalkID(t.getContent());


					//チェック
					if(gameInfos.get(day) == null)
					{
						if(!ret.contains(t.getAgent()))
						{
							ret.add(t.getAgent());
						}
					}
					else
					{
						List<Talk> ttmp = gameInfos.get(day).getTalkList();

						boolean lier = true;
						for(Talk u : ttmp)
						{
							if(u.getIdx() == id)
							{
								lier = false;
								break;
							}
						}

						if(lier)
						{
							if(!ret.contains(t.getAgent()))
							{
								ret.add(t.getAgent());
							}
						}
					}

				}
			}
		}


		return ret;
	}

	static List<Agent> DeadList(GameInfo latestGameInfo)
	{
		List<Agent> ret = new ArrayList<Agent>();

		for(Map.Entry<Agent, Status> x : latestGameInfo.getStatusMap().entrySet())
		{
			if(x.getValue()==Status.DEAD)
			{
				ret.add(x.getKey());
			}
		}

		return ret;
	}

	//みんなが考えている投票先から自分の候補内の一つを選択する、勝ちやすい投票先を選ぶ
	public static Agent SelectVoteTargetByOtherVote(List<Agent> myCandidate, Map<Agent, Agent> voteTarget)
	{

		Random rand = new Random();
	//	Map<Agent, Agent> voteTarget = vi.VoteTarget();

		Agent voteTargetTmp = null;
		{
			Map<Agent, Integer> voteCount = new HashMap<Agent, Integer>();
			for(Map.Entry<Agent, Agent> x : voteTarget.entrySet())
			{
				Agent tgt = x.getValue();

				if(myCandidate.contains(tgt))
				{
					if(!voteCount.containsKey(tgt))
						voteCount.put(tgt, 0);

					voteCount.put(tgt, voteCount.get(tgt)+1);
				}
			}


			if(voteCount.size()>0)
			{
				int max = 0;
				for(Map.Entry<Agent, Integer> x : voteCount.entrySet())
				{
					max = Math.max(max, x.getValue());
				}

				List<Agent> maxAgents = new ArrayList<Agent>();
				for(Map.Entry<Agent, Integer> x : voteCount.entrySet())
				{
					if(x.getValue()==max)
					{
						maxAgents.add(x.getKey());
					}
				}

				voteTargetTmp = maxAgents.get(rand.nextInt(maxAgents.size()));
			}
			else
			{
				voteTargetTmp = myCandidate.get(rand.nextInt(myCandidate.size()));
			}
			return voteTargetTmp;
		}
	}
	
	public static Agent coAnySuspeciousAgent(List<Agent> aliveAgentList, AdvanceGameInfo agi, int today)
	{
		for(Map.Entry<Agent, Role> entry: agi.getComingoutMap().entrySet())
		{
			if(!aliveAgentList.contains(entry.getKey())) continue;
			
			if(entry.getValue() == Role.WEREWOLF
					|| entry.getValue() == Role.POSSESSED)
			{
				return entry.getKey();
			}
		}
		
		for(Map.Entry<Agent, Role> entry: agi.getComingoutMap().entrySet())
		{
			if(!aliveAgentList.contains(entry.getKey())) continue;
			
			if(entry.getValue() == Role.BODYGUARD
					|| (today <= 3 && entry.getValue() == Role.MEDIUM))
			{
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	// list1とlist2の両方に含まれる要素のみを持つ、新しいListを返す
	public static <T> List<T> AndList(List<T> list1, List<T> list2)
	{
		List<T> newList = new ArrayList<T>();
		for(T a1:list1)
		{
			if(list2.contains(a1))
			{
				newList.add(a1);
			}
		}
		return newList;
	}

	// 役職数
	private static int m_roleNumber; // 未初期化
	public static int GetRoleNumber() { return m_roleNumber; }


}
