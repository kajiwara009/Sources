package org.aiwolf.client.lib;

import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.common.data.Agent;
import org.aiwolf.common.data.Role;
import org.aiwolf.common.data.Species;
import org.aiwolf.common.data.Talk;

/**
 * 人狼の囁き用の発話を生成するクラス
 * @author kengo
 *
 */
public class TemplateWhisperFactory {


	public static String attack(Agent target){
		String[] split = {Topic.ATTACK.toString(), String.valueOf(target.toString())};
		return wordAttachment(split);
	}

	public static String estimate(Agent target, Role role){
		String[] split = {Topic.ESTIMATE.toString(), String.valueOf(target.toString()), role.toString()};
		return wordAttachment(split);
	}

	public static String comingout(Agent target, Role role){
		String[] split = {Topic.COMINGOUT.toString(), String.valueOf(target.toString()), role.toString()};
		return wordAttachment(split);
	}

	public static String divined(Agent target, Species species){
		String[] split = {Topic.DIVINED.toString(), String.valueOf(target.toString()), species.toString()};
		return wordAttachment(split);
	}

	public static String inquested(Agent target, Species species){
		String[] split = {Topic.INQUESTED.toString(), String.valueOf(target.toString()), species.toString()};
		return wordAttachment(split);
	}

	public static String guarded(Agent target){
		String[] split = {Topic.GUARDED.toString(), String.valueOf(target.toString())};
		return wordAttachment(split);
	}

	public static String vote(Agent target){
		String[] split = {Topic.VOTE.toString(), String.valueOf(target.toString())};
		return wordAttachment(split);
	}

	public static String agree(TalkType talkType, int day, int id){
		String[] split = {Topic.AGREE.toString(), talkType.toString(), "day"+String.valueOf(day), "ID:"+String.valueOf(id)};
		return wordAttachment(split);
	}

	public static String disagree(TalkType talkType, int day, int id){
		String[] split = {Topic.DISAGREE.toString(), talkType.toString(), "day"+String.valueOf(day), "ID:"+String.valueOf(id)};
		return wordAttachment(split);
	}


	public static String over(){
		return Talk.OVER;
	}

	public static String skip(){
		return Talk.SKIP;
	}

	private static String wordAttachment(String[] split){
		String answer = "";
		for(int i = 0; i < split.length; i++){
			answer += split[i] + " ";
		}
		return answer.trim();
	}
}