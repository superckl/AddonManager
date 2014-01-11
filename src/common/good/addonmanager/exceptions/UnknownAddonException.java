package common.good.addonmanager.exceptions;

/**
 *
 * @author DarkSeraphim
 */
@SuppressWarnings("serial")
public class UnknownAddonException extends Exception
{

	public UnknownAddonException(final String name)
	{
		super(String.format("Unknown addon: %s", name));
	}

}
