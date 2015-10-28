package org.aiwolf.Satsuki.lib;

import org.aiwolf.common.data.Agent;

/**
 * ���񂾃v���C���[�̏ڍׁD�����ڂ�(�P��or���Y)�ɂ���Ď��񂾂��D
 * @author kengo
 *
 */
public class DeadCondition 
{
	private Agent deadAgent;
	private int dateOfDeath;
	private CauseOfDeath cause;


	public DeadCondition(Agent deadAgent, int dateOfDeath, CauseOfDeath cause)
	{
		this.deadAgent = deadAgent;
		this.dateOfDeath = dateOfDeath;
		this.cause = cause;
	}




	public Agent getDeadAgent() {
		return deadAgent;
	}


	public int getDateOfDeath() 
	{
		return dateOfDeath;
	}


	public CauseOfDeath getCause() 
	{
		return cause;
	}
}
