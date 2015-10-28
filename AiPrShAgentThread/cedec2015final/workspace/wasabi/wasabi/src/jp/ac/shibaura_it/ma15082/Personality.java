package jp.ac.shibaura_it.ma15082;

public class Personality {
	private double openness_to_experimence;//�D��S
	private double conscientiousness;//�Εא�
	private double extroversion;//�O����
	private double agreeableness;//������
	private double neuroticism;//��s���萫
	
	
	
	
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
