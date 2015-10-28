package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateWhisperFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.*;
import org.aiwolf.common.data.Agent;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class VoteTalk implements ITalkEvent {
	Agent target = null;
	TalkType talkType = null;
	public VoteTalk(TalkType talkType, Agent target) {
		this.target = target;
		assert target != null;
		
		this.talkType = talkType;
	}
	@Override
	public String getTalk() {
		if(talkType == TalkType.TALK) {
			return TemplateTalkFactory.vote(target);
		} else if(talkType == TalkType.WHISPER) {
			return TemplateWhisperFactory.vote(target);
		}
		assert false;
		return null;
	}
}
