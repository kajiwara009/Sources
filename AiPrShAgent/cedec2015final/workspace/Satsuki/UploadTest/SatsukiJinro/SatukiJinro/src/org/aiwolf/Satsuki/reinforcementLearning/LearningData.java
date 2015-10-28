package org.aiwolf.Satsuki.reinforcementLearning;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.aiwolf.Satsuki.lib.PossessedFakeRoleChanger;
import org.aiwolf.Satsuki.lib.WolfFakeRoleChanger;
import org.aiwolf.common.data.Role;

public class LearningData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 783381753058154993L;
	
	private int learningDataNumber; //�����̃C���X�^���X��ʁX�ɕۊǂ��邽�߂̔ԍ�
	/** Scene��Hash�l��Qvalues�̃}�b�v�P */
	private int playNum = 0;

	private Map<Integer, Qvalues> sceneMap = new TreeMap<Integer, Qvalues>();

	private Map<COtimingNeo, Double>
		seerCO = new HashMap<COtimingNeo, Double>(),
		mediumCO = new HashMap<COtimingNeo, Double>(),
		possessedCO = new HashMap<COtimingNeo, Double>(),
		wolfCO = new HashMap<COtimingNeo, Double>();
	private Map<WolfFakeRoleChanger, Double> wolfFakeRoleChanger = new HashMap<WolfFakeRoleChanger, Double>();
	private Map<PossessedFakeRoleChanger, Double> possessedFakeRoleChanger = new HashMap<PossessedFakeRoleChanger, Double>();
	
	private static Map<Integer, LearningData> instance = new HashMap<Integer, LearningData>();
//	private static LearningData instance = new LearningData();

	public void LDStart()
	{
		if(playNum != 0)
		{
			return;
		}
		try 
		{
			// (7)FileInputStream�I�u�W�F�N�g�̐���
			FileInputStream inFile = new FileInputStream("LDdata_" + learningDataNumber + ".txt");
			// (8)ObjectInputStream�I�u�W�F�N�g�̐���
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			// (9)�I�u�W�F�N�g�̓ǂݍ���
//			System.out.println("LDStart:�ǂݍ��݊J�n  " + System.currentTimeMillis());
			LearningData ld = (LearningData) inObject.readObject();
			instance.put(ld.learningDataNumber, ld);
//			System.out.println("LDStart:�ǂݍ��ݏI��  " + System.currentTimeMillis());

			inFile.close();
			inObject.close();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void LDStart(int readDataNum) 
	{
		if(playNum != 0){
			return;
		}
		try {
			int preLearningNumber = learningDataNumber;
			// (7)FileInputStream�I�u�W�F�N�g�̐���
			FileInputStream inFile = new FileInputStream("LDdata_" + readDataNum + ".txt");
			// (8)ObjectInputStream�I�u�W�F�N�g�̐���
			ObjectInputStream inObject = new ObjectInputStream(inFile);
			// (9)�I�u�W�F�N�g�̓ǂݍ���
			System.out.println("LDStart:�ǂݍ��݊J�n  " + System.currentTimeMillis());
			LearningData ld = (LearningData) inObject.readObject();
			ld.learningDataNumber = preLearningNumber;
			instance.put(ld.learningDataNumber, ld);
			System.out.println("LDStart:�ǂݍ��ݏI��  " + System.currentTimeMillis());

			inFile.close();
			inObject.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		
	}

	private LearningData(int ldNumber){
		learningDataNumber  = ldNumber;

		for(int i = 0; i < 3; i++){
			for(int v = 0; v < 2; v++)for(int w = 0; w < 2; w++)for(int f = 0; f < 2; f++)for(int a = 0; a < 2; a++){
				boolean vote = v == 0? true: false;
				boolean wolf = w == 0? true: false;
				boolean found = f == 0? true: false;
				boolean against = a == 0? true: false;
				COtimingNeo cot = new COtimingNeo(i, vote, wolf, found, against);
				seerCO.put(cot, 0.0);
				mediumCO.put(cot, 0.0);
				possessedCO.put(cot, 0.0);
				wolfCO.put(cot, 0.0);
			}
		}
		
		List<Role> fakes = WolfFakeRoleChanger.getFakeroles();
		int size = fakes.size();
		for(int i = 0; i < size; i++)for(int w = 0; w < size; w++)for(int ev = 0; ev < size; ev++)
			for(int es = 0; es < size; es++)for(int em = 0; em < size; em++)for(int sc = 0; sc < size; sc++)
				for(int mc = 0; mc < size; mc++)for(int iv = 0; iv < size; iv++){
					Role ini = fakes.get(i);
					Role wol = fakes.get(w);
					Role evi = fakes.get(ev);
					Role ese = fakes.get(es);
					Role eme = fakes.get(em);
					Role sco = fakes.get(sc);
					Role mco = fakes.get(mc);
					Role ivo = fakes.get(iv);
					WolfFakeRoleChanger wfrc = new WolfFakeRoleChanger();
					wfrc.setInitial(ini);
					wfrc.setExistVillagerWolf(evi);
					wfrc.setExistSeerWolf(ese);
					wfrc.setExistMediumWolf(eme);
/*					wfrc.setSeerCO(sco);
					wfrc.setMediumCO(mco);
*/					wolfFakeRoleChanger.put(wfrc, 0.0);
				}
		
		for(int i = 0; i < size; i++){
				Role ini = fakes.get(i);
				PossessedFakeRoleChanger pfrc = new PossessedFakeRoleChanger();
				pfrc.setInitial(ini);
				possessedFakeRoleChanger.put(pfrc, 0.0);
			}
/*			
		Role[] roles = {Role.VILLAGER, Role.SEER, Role.MEDIUM};
		for(Role r: roles){
			possessedFakeRole.put(r, 0.0);
		}
		for(WolfRolePattern wrp: WolfRolePattern.values()){
			wolfRolePattern.put(wrp, 0.0);
		}

*/

	}
	
	public void LDFinish(int version){
		try {
			int num = learningDataNumber + version * 1000;
			// (2)FileOutputStream�I�u�W�F�N�g�̐���
			FileOutputStream outFile = new FileOutputStream("LDdata_" + num + ".txt");
//			FileOutputStream outFile = new FileOutputStream("LDdata:" + learningDataNumber + "_" + version + ".txt");
			// (3)ObjectOutputStream�I�u�W�F�N�g�̐���
			ObjectOutputStream outObject = new ObjectOutputStream(outFile);
			// (4)�N���XHello�̃I�u�W�F�N�g�̏�������
			outObject.writeObject(this);

			outObject.close(); // (5)�I�u�W�F�N�g�o�̓X�g���[���̃N���[�Y
			outFile.close(); // (6)�t�@�C���o�̓X�g���[���̃N���[�Y
/*			// (7)FileInputStream�I�u�W�F�N�g�̐���
						FileInputStream inFile = new FileInputStream("object.txt");
						// (8)ObjectInputStream�I�u�W�F�N�g�̐���
						ObjectInputStream inObject = new ObjectInputStream(inFile);
						// (9)�I�u�W�F�N�g�̓ǂݍ���
						instance = (LearningData) inObject.readObject();

						inFile.close();
						inObject.close();

*/
			} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			// TODO: handle exception

		}

	}


	public void LDFinish(){
		try {
			// (2)FileOutputStream�I�u�W�F�N�g�̐���
			FileOutputStream outFile = new FileOutputStream("LDdata_" + learningDataNumber + ".txt");
			// (3)ObjectOutputStream�I�u�W�F�N�g�̐���
			ObjectOutputStream outObject = new ObjectOutputStream(outFile);
			// (4)�N���XHello�̃I�u�W�F�N�g�̏�������
			outObject.writeObject(this);

			outObject.close(); // (5)�I�u�W�F�N�g�o�̓X�g���[���̃N���[�Y
			outFile.close(); // (6)�t�@�C���o�̓X�g���[���̃N���[�Y
/*			// (7)FileInputStream�I�u�W�F�N�g�̐���
						FileInputStream inFile = new FileInputStream("object.txt");
						// (8)ObjectInputStream�I�u�W�F�N�g�̐���
						ObjectInputStream inObject = new ObjectInputStream(inFile);
						// (9)�I�u�W�F�N�g�̓ǂݍ���
						instance = (LearningData) inObject.readObject();

						inFile.close();
						inObject.close();

*/
			} catch (Exception e) {
			e.printStackTrace();
			System.out.println();
			// TODO: handle exception

		}

	}

	public static synchronized LearningData getInstance(int ldNum)
	{
		if(! instance.containsKey(ldNum))
		{
			instance.put(ldNum, new LearningData(ldNum));
		}
		return instance.get(ldNum);
	}




	public Qvalues getQvalue(int hash)
	{
		if(sceneMap.containsKey(hash))
		{
			return sceneMap.get(hash);
		}
		else
		{
			sceneMap.put(hash, new Qvalues());
			return sceneMap.get(hash);
		}
	}

	//getter and setter
	public Map<Integer, Qvalues> getSceneMap() {
		return sceneMap;
	}

	public void setSceneMap(Map<Integer, Qvalues> sceneMap) {
		this.sceneMap = sceneMap;
	}
	public int getLearningDataNumber() {
		return learningDataNumber;
	}

	public int getPlayNum() {
		return playNum;
	}

	public void setPlayNum(int playNum) {
		this.playNum = playNum;
	}

	public Map<COtimingNeo, Double> getSeerCO() {
		return seerCO;
	}

	public Map<COtimingNeo, Double> getMediumCO() {
		return mediumCO;
	}

	public Map<COtimingNeo, Double> getPossessedCO() {
		return possessedCO;
	}

	public Map<COtimingNeo, Double> getWolfCO() {
		return wolfCO;
	}

	public Map<WolfFakeRoleChanger, Double> getWolfFakeRoleChanger() {
		return wolfFakeRoleChanger;
	}

	public Map<PossessedFakeRoleChanger, Double> getPossessedFakeRoleChanger() {
		return possessedFakeRoleChanger;
	}

	public static Map<Integer, LearningData> getInstance() {
		return instance;
	}


}
