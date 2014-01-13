package org.sensationcraft.addonmanager.exceptions;

@SuppressWarnings("serial")
public class InvalidAddonException extends Exception
{

	public InvalidAddonException(final String error)
	{
		super(error);
	}

	public InvalidAddonException(final String error, final Throwable ex)
	{
		super(error);
		this.setStackTrace(ex.getStackTrace());
	}

}
