/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edacc.parametergrapheditor;

import edacc.parameterspace.domain.Domain;
import edacc.parameterspace.domain.OrdinalDomain;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author simon
 */
public class SelectOrdinalDomainPanel extends SelectCategoricalDomainPanel {

    public SelectOrdinalDomainPanel(OrdinalDomain domain) {
        super(domain.getOrdered_list());
    }

    @Override
    public Domain getDomain() throws InvalidDomainException {
        List<String> list = getSelectedCategories();
        if (list.isEmpty()) {
            throw new InvalidDomainException("You must select at least one category for ordinal domain.");
        }
        return new OrdinalDomain(list);
    }

    @Override
    public void setDomain(Domain orDomain, Domain andDomain) throws InvalidDomainException {
        if (!(orDomain instanceof OrdinalDomain) || !(andDomain instanceof OrdinalDomain)) {
            throw new InvalidDomainException("Got domains " + orDomain.getName() + "/" + andDomain.getName() + ", expected ordinal/ordinal domains.");
        }
        HashSet<String> values = new HashSet<String>();
        for (String s : ((OrdinalDomain) andDomain).getOrdered_list()) {
            values.add(s);
        }
        setSelectedCategories(values);
    }
}
