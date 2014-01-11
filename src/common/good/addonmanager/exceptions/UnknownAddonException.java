package common.good.addonmanager.exceptions;

@SuppressWarnings("serial")
public class UnknownAddonException extends Exception
{

	public UnknownAddonException(final String name)
	{
		super(String.format("Unknown addon: %s", name));
	}

}
