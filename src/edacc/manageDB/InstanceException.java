package edacc.manageDB;

/**
 * An Exception class used by the InstanceParser class for reporting parsing errors.
 *
 * @author Raffael Bild
 */


import java.lang.Exception;


public class InstanceException extends Exception
{
	/**
	 * Construct a new InstanceException
	 */
	public InstanceException(){
            super();
	}

	/**
	 * Construct a new InstanceException with the specified detail message
	 *
	 * @param s The detail message.
	 */
	public InstanceException(String s)
	{
		super(s);
	}
}

