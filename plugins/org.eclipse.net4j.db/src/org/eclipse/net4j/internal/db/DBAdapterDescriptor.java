package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapter;

/**
 * @author Eike Stepper
 */
public abstract class DBAdapterDescriptor
{
  private String name;

  public DBAdapterDescriptor(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public abstract IDBAdapter createDBAdapter();
}