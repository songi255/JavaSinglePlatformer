package lobbyServer.dbconn;

import client.Selection;
import client.Selection.ChampList;

public class Account {
	private int num; 	//회원번호
	private String id;	//아이디
	private transient String pw;	//비밀번호
	private Selection selection;

	public Account() {
		this.selection = new Selection();
	}

	public Account(int num, String id, String pw) {
		super();
		this.selection = new Selection();
		this.num = num;
		this.id = id;
		this.pw = pw;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Account) {
			Account account = (Account) obj;
			if (this.num == account.num) {
				return true;
			}
		}
		return false;
	}

	public void clearSelection() {
		this.selection.setReady(false);
		this.selection.setChampKind(ChampList.A);
		this.selection.setTeam(1);
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public Selection getSelection() {
		return this.selection;
	}
}
