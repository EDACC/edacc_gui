package edacc.parametergrapheditor;

import edacc.parameterspace.domain.Domain;

/**
 *
 * @author simon
 */
public interface ICreateNodeDialog {
    public boolean isCancelled();
    public Domain getDomain() throws InvalidDomainException;
    public String getParameterName();
}
