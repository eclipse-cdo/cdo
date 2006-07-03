package org.eclipse.net4j.util.store;


public interface IStore
{
  public ISession createSession(IUser user);
}
