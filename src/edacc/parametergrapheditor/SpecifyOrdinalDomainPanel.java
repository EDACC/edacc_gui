package edacc.parametergrapheditor;

import edacc.EDACCApp;
import edacc.parameterspace.Parameter;
import edacc.parameterspace.domain.Domain;
import edacc.parameterspace.domain.OrdinalDomain;
import java.util.LinkedList;

/**
 *
 * @author simon
 */
public class SpecifyOrdinalDomainPanel extends SpecifyCategoricalDomainPanel {
    public SpecifyOrdinalDomainPanel(SpecifyDomainDialog main) {
        super(main);
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
    
    @Override
    public void setDomain(Domain orDomain, Domain andDomain) {
        if (!(orDomain instanceof OrdinalDomain) || andDomain != null) {
            return;
        }
        model.clear();
        for (String s : ((OrdinalDomain) orDomain).getOrdered_list()) {
            model.addElement(s);
        }
    }

    @Override
    public void useDomain() {
        UseDomainDialog dialog = new UseDomainDialog(EDACCApp.getApplication().getMainFrame(), true, main.getParameters(), OrdinalDomain.class);
        dialog.setName("UseDomainDialog");
        EDACCApp.getApplication().show(dialog);
        Parameter p;
        if ((p = dialog.getSelectedItem()) != null && p.getDomain() instanceof OrdinalDomain) {
            this.setDomain(p.getDomain(), null);
        }
    }
    
    
}
