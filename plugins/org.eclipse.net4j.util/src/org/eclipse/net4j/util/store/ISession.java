package org.eclipse.net4j.util.store;


import org.eclipse.net4j.util.store.handlers.IQueryContentHandler;
import org.eclipse.net4j.util.store.handlers.IQueryMembersHandler;

import org.eclipse.core.runtime.IPath;


public interface ISession
{
  public IUser getUser();

  public ITransaction beginTransaction();

  public void queryMembers(IPath container, IQueryMembersHandler handler);

  public void queryContent(IPath file, IQueryContentHandler handler);

  public void setContent(IPath file, IQueryContentHandler handler);

  public void close();
}
