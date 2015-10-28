package twinDirector;

import java.util.List;

import stateDirector.StateDirector;
import unitDirector.UnitDirector;
import codevs.God;

/**
 * ただのUnitDirectorの管理者
 * 戦略を担うことはないはず
 * もしthinkの順序を変えたかったりしたら，StateDirectorの方で直接thinkを書いて
 * @author kajiwarakengo
 *
 */
public abstract class TwinDirector {
	protected God god;

	public TwinDirector(God god) {
		this.god = god;
	}
	
	public abstract void think(StateDirector sd);
	
	public void resetUsableResource(){
		for(UnitDirector ud: getUnitDirectors()){
			ud.setUsableResource(0);
		}
	}
	
	
	
	public abstract List<UnitDirector> getUnitDirectors();
	
	protected void order(){
		for(UnitDirector ud: getUnitDirectors()){
			ud.moveUnits();
		}
	}
	
}
