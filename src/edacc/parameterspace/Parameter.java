package edacc.parameterspace;

import javax.xml.bind.annotation.XmlID;

import edacc.parameterspace.domain.Domain;

public class Parameter implements Comparable<Parameter> {
	private String name;
	private Domain domain;
	
	public Parameter() {
		
	}
	
	public Parameter(String name, Domain domain) {
		this.name = name;
		this.domain = domain;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Parameter other = (Parameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@XmlID public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

    @Override
    public String toString() {
        return "Parameter{" + "name=" + name + ", domain=" + domain + '}';
    }

	@Override
	public int compareTo(Parameter o) {
		return this.name.compareTo(o.getName());
	}
}
