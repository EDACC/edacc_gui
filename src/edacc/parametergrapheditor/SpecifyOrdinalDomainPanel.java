package edacc.parametergrapheditor;

import edacc.parameterspace.domain.Domain;
import edacc.parameterspace.domain.OrdinalDomain;
import java.util.LinkedList;

/**
 *
 * @author simon
 */
public class SpecifyOrdinalDomainPanel extends SpecifyCategoricalDomainPanel {
    public SpecifyOrdinalDomainPanel() {
        super();
    }

    @Override
    public Domain getDomain() throws InvalidDomainException {
        if (model.isEmpty()) {
            throw new InvalidDomainException("You must specify at least one category for ordinal domain.");
        }
        LinkedList<String> list = new LinkedList<String>();
        for (int i = 0; i < model.size(); i++) {
            list.add((String) model.get(i));
        }
        return new OrdinalDomain(list);
    }
    
    
}
