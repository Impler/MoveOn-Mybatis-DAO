package cn.impler.framework.mybatis.dao.domain;

public class App {
	private int id;
	private String name;
	private String domain_url;
	private String domain_desc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain_url() {
		return domain_url;
	}

	public void setDomain_url(String domain_url) {
		this.domain_url = domain_url;
	}

	public String getDomain_desc() {
		return domain_desc;
	}

	public void setDomain_desc(String domain_desc) {
		this.domain_desc = domain_desc;
	}

	@Override
	public String toString() {
		return "App [id=" + id + ", name=" + name + ", domain_url="
				+ domain_url + ", domain_desc=" + domain_desc + "]";
	}
	
}
