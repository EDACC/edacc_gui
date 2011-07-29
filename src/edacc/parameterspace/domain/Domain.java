package edacc.parameterspace.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({ CategoricalDomain.class, FlagDomain.class, IntegerDomain.class,
		MixedDomain.class, OptionalDomain.class, RealDomain.class,
                OrdinalDomain.class})
public abstract class Domain implements Serializable {
        public static final String[] names = {CategoricalDomain.name, FlagDomain.name, IntegerDomain.name, MixedDomain.name, OptionalDomain.name, OrdinalDomain.name, RealDomain.name};
        
	public abstract boolean contains(Object value);

	public abstract Object randomValue(Random rng);

	public abstract String toString();
	
	public abstract Object mutatedValue(Random rng, Object value);
	
	public abstract List<Object> getDiscreteValues();
        
        public abstract String getName();
}
