package jp.ac.shibaura_it.ma15082;

import org.aiwolf.client.lib.Utterance;
import org.aiwolf.common.data.Species;

public enum Colour {
	WHITE("”’"),
	BLACK("•"),
	GREY("ŠD"),
	PANDA("ƒpƒ“ƒ_"),
	NONE("")
	;
	
	private String name;
	private Colour(final String s){
		name=s;
	}
	
	public String getName(){
		return name;
	}
	
	public static Colour convert(String str){
		for(Colour c : values()){
			if(c.toString().equals(str)){
				return c;
			}
		}
		return NONE;
	}
	
	public Colour join(Colour c){
		if(this==NONE || c==NONE){
			return NONE;
		}
		if(this==PANDA || c==PANDA){
			return PANDA;
		}
		if(this==c){
			return c;
		}
		if(this==GREY){
			return c;
		}
		if(c==GREY){
			return this;
		}
				
		return PANDA;
	}
	
	
	public static Colour analyze(Utterance u){
		Colour ret=Colour.GREY;
		//”­Œ¾‚ÌAgent‚ª•‚©”’‚©‚¾‚¯’²‚×‚é
		switch(u.getTopic()){
		case AGREE://È—ª
		case DISAGREE:
			break;
		case ESTIMATE://lŠO–ğE‚©”Û‚©‚Å•ª—Ş‚·‚é
			if(u.getRole().getSpecies()==Species.WEREWOLF){
				ret=Colour.BLACK;
			}
			else{
				ret=Colour.WHITE;
			}
			break;
		case GUARDED://Œì‰q‚µ‚½‚Ì‚Í”’‚¾‚©‚ç
		case ATTACK://UŒ‚‚³‚ê‚é‚Ì‚Í”’‚¾‚©‚ç
			ret=Colour.WHITE;
			break;
		case VOTE://“Š•[‚·‚é‚Ì‚Í•‚¾‚©‚ç
			ret=Colour.BLACK;
			break;
		default:
			break;
		
		}
		
		
		return ret;
	}
	
	
}
