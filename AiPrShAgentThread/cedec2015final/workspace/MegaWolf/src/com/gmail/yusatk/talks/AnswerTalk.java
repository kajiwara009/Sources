package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.TemplateWhisperFactory;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class AnswerTalk implements ITalkEvent {
	public enum AnswerType {
		AGREE,
		DISAGREE,
	}

	int targetId;
	int day;
	TalkType talkType;
	AnswerType answerType;
	
	
	public AnswerTalk(TalkType talkType, AnswerType answerType, int day, int targetId) {
		this.targetId = targetId;
		this.talkType = talkType;
		this.day = day;
		this.answerType = answerType;
	}
	
	@Override
	public String getTalk() {
		if(answerType == AnswerType.AGREE) {
			if(talkType == TalkType.TALK) {
				return TemplateTalkFactory.agree(talkType, day, targetId);
			}else if(talkType == TalkType.WHISPER) {
				return TemplateWhisperFactory.agree(talkType, day, targetId);
			}
		} else if(answerType == AnswerType.DISAGREE) {
			if(talkType == TalkType.TALK) {
				return TemplateTalkFactory.disagree(talkType, day, targetId);
			}else if(talkType == TalkType.WHISPER) {
				return TemplateWhisperFactory.disagree(talkType, day, targetId);
			}
		}
		assert false;
		return null;
	}

}
