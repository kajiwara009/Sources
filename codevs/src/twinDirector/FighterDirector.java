package twinDirector;

import java.util.ArrayList;
import java.util.List;

import stateDirector.StateDirector;
import unitDirector.UnitDirector;
import unitDirector.fighter.CastleAttackDirector;
import unitDirector.fighter.CastleDefencer;
import codevs.God;

public class FighterDirector extends TwinDirector {
	/*
	 * UnitDirectorを加えたら，order()にもぶち込む
	 */
	protected CastleAttackDirector cad = new CastleAttackDirector(god);
	protected CastleDefencer cdD = new CastleDefencer(god);

	public FighterDirector(God god) {
		super(god);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void think(StateDirector sd) {
		order();
	}

	@Override
	public List<UnitDirector> getUnitDirectors() {
		List<UnitDirector> uds = new ArrayList<>();
		uds.add(cad);
		uds.add(cdD);
		return uds;
	}

	public CastleAttackDirector getCad() {
		return cad;
	}

	public CastleDefencer getCdD() {
		return cdD;
	}

}
