package org.eclipse.net4j.internal.util.security;

import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;

/**
 * @author Eike Stepper
 */
public final class PasswordCredentialsProvider implements IPasswordCredentialsProvider
{
  private IPasswordCredentials credentials;

  public PasswordCredentialsProvider(IPasswordCredentials credentials)
  {
    this.credentials = credentials;
  }

  public boolean isInteractive()
  {
    return false;
  }

  public IPasswordCredentials getCredentials()
  {
    return credentials;
  }
}