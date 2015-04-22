/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import java.sql.Blob;
import java.sql.Clob;

/**
 * Object used to store clob and lob.
 *
 * @author Martin Taal
 */
public class HibernateStoreLob
{
  // note size -1 is used to flag a new state
  private static int NEW_FLAG_SIZE = -1;

  private String id;

  private Clob clob;

  private Blob blob;

  private int size = NEW_FLAG_SIZE;

  public boolean isNew()
  {
    return size == NEW_FLAG_SIZE;
  }

  public int getSize()
  {
    return size;
  }

  public void setSize(int size)
  {
    this.size = size;
  }

  public String getId()
  {
    return id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  public Clob getClob()
  {
    return clob;
  }

  public void setClob(Clob clob)
  {
    this.clob = clob;
  }

  public Blob getBlob()
  {
    return blob;
  }

  public void setBlob(Blob blob)
  {
    this.blob = blob;
  }
}
