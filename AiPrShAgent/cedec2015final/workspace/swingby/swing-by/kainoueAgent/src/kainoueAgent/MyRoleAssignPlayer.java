package kainoueAgent;
import org.aiwolf.client.base.player.AbstractRoleAssignPlayer;

public class MyRoleAssignPlayer extends AbstractRoleAssignPlayer {

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public MyRoleAssignPlayer(){
	//	System.out.print("1--------------------------\n");
		setSeerPlayer(new MyRoleAssignPlayerSeer());
		setVillagerPlayer(new MyRoleAssignPlayerVillager());
		setBodyguardPlayer(new MyRoleAssignPlayerBodyguard());
		setMediumPlayer(new MyRoleAssignPlayerMedium());
		setPossessedPlayer(new MyRoleAssignPlayerPossessed());
		setWerewolfPlayer(new MyRoleAssignPlayerWerewolf());
	}

}
