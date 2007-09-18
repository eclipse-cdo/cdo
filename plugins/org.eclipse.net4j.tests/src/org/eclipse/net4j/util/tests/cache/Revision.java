/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests.cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Revision
{
  private int id;

  private int version;

  private byte[] data = new byte[100000];

  public Revision(int id, int version)
  {
    this.id = id;
    this.version = version;
  }

  public int getID()
  {
    return id;
  }

  public int getVersion()
  {
    return version;
  }

  public byte[] getData()
  {
    return data;
  }

  @Override
  public String toString()
  {
    return "R" + id + "v" + version;
  }

  @SuppressWarnings("unchecked")
  private static Set finalized = new HashSet();

  @SuppressWarnings("unchecked")
  private static List refs = new ArrayList();

  @SuppressWarnings("unchecked")
  private static ReferenceQueue queue = new ReferenceQueue();

  @Override
  protected void finalize() throws Throwable
  {
    System.err.println("FINALIZE " + this);
    long token = id;
    token <<= 32;
    token |= version;
    if (!finalized.add(token))
    {
      System.err.println("************************************************************************");
    }

    refs.add(new PhantomReference<Revision>(this, queue));
  }
}
