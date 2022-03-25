package baseApi;

public class ReportNode {
	private int id;
	private String parent;
	private String selfName;
	
	public ReportNode(int id, String parent, String selfName) {
		super();
		this.id = id;
		this.parent = parent;
		this.selfName = selfName;
	}
	
	public int getId() {
		return id;
	}
	
	public String getparent() {
		return parent;
	}
	
	public String getselfName() {
		return selfName;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setparent(String parent) {
		this.parent = parent;
	}
	
	public void setselfName(String selfName) {
		this.selfName = selfName;
	}
}
