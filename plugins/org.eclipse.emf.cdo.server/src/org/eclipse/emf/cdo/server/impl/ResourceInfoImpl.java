/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.emf.cdo.server.ResourceInfo;


public class ResourceInfoImpl implements ResourceInfo
{
  private String path;

  private int rid;

  private long nextOIDFragment;

  public ResourceInfoImpl()
  {
  }

  public ResourceInfoImpl(String path, int rid, long nextOIDFragment)
  {
    this.path = path;
    this.rid = rid;
    this.nextOIDFragment = nextOIDFragment;
  }

  public String getPath()
  {
    return path;
  }

  public void setPath(String path)
  {
    this.path = path;
  }

  public int getRID()
  {
    return rid;
  }

  public void setRID(int rid)
  {
    this.rid = rid;
  }

  public long getNextOIDFragment()
  {
    return nextOIDFragment++;
  }

  public void setNextOIDFragment(long nextOIDFragment)
  {
    this.nextOIDFragment = nextOIDFragment;
  }

  @Override
  public String toString()
  {
    return "ResourceInfo(rid=" + rid + ", path=" + path + ", nextOIDFragment=" + nextOIDFragment
        + ")";
  }
}
