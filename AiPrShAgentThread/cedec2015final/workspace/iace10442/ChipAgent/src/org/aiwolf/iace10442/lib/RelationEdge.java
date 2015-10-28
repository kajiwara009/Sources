package org.aiwolf.iace10442.lib;



public class RelationEdge {
	public int tyming; // ���M���ꂽ�^�C�~���O�@�ڈ�
	public int srcID; // ���M��
	public int dstID; // ���M��@COMMINGOUT�ł�srcID�Ɠ���
	public RelationType type;
	
	// �R���X�g���N�^
	public RelationEdge(int tyming, int srcID, int dstID, RelationType type )
	{
		this.tyming = tyming;
		this.srcID = srcID;
		this.dstID = dstID;
		this.type = type;
	}
	public RelationEdge() {}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dstID;
		result = prime * result + srcID;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationEdge other = (RelationEdge) obj;
		if (dstID != other.dstID)
			return false;
		if (srcID != other.srcID)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
}
