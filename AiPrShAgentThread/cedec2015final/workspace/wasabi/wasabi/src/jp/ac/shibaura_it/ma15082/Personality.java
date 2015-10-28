package jp.ac.shibaura_it.ma15082;

public class Personality {
	private double openness_to_experimence;//好奇心
	private double conscientiousness;//勤勉性
	private double extroversion;//外向性
	private double agreeableness;//協調性
	private double neuroticism;//情緒不安定性
	
	
	
	
	public Personality(double oe,double co,double ex,double ag,double ne){
		openness_to_experimence=oe;
		conscientiousness=co;
		extroversion=ex;
		agreeableness=ag;
		neuroticism=ne;	
	}
	
	
	public String toString(){
		return openness_to_experimence+","+conscientiousness+","+extroversion+","+agreeableness+","+neuroticism;
	}
	
	
	public double getWeightPrev(){
		return 1-openness_to_experimence;
	}
	
	public double getWeightSubjective(){
		return 1-conscientiousness;
	}
	
	public double getWeightVolume(){
		return extroversion;
	}
	
	public double getWeightTalkWhite(){
		return agreeableness;
	}
	
	public double getWeightRandom(){
		return neuroticism;
	}

	
	
}
