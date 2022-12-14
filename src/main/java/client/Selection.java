package client;

public class Selection implements Cloneable{
	public enum ChampList {
		A, B
	}

	private int team = 1;
	private ChampList champKind = ChampList.A;
	private boolean isReady = false;

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public ChampList getChampKind() {
		return champKind;
	}

	public void setChampKind(ChampList champKind) {
		this.champKind = champKind;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	public Selection copy() {
		Selection selection = null;
		try {
			selection =  (Selection) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return selection;
	}
}
