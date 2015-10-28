package com.gmail.yusatk.talks;

import org.aiwolf.client.lib.TemplateTalkFactory;
import org.aiwolf.client.lib.TemplateTalkFactory.TalkType;
import org.aiwolf.client.lib.TemplateWhisperFactory;

import com.gmail.yusatk.interfaces.ITalkEvent;

public class OverTalk implements ITalkEvent {
	TalkType talkType = null;
	public OverTalk(TalkType talkType) {
		this.talkType = talkType;
		assert talkType != null;
	}
	@Override
	public String getTalk() {
		if(talkType == TalkType.TALK) {
			return TemplateTalkFactory.over();
		} else if (talkType == TalkType.WHISPER) {
			return TemplateWhisperFactory.over();
		}
		assert false;
		return null;
	}

}
