/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests.cache;

/**
 * @author Eike Stepper
 */
public class Revision
{
  private RevisionManager revisionManager;

  private int id;

  private int version;

  private byte[] data = new byte[100000];

  public Revision(RevisionManager revisionManager, int id, int version)
  {
    this.revisionManager = revisionManager;
    this.id = id;
    this.version = version;
  }

  public RevisionManager getRevisionManager()
  {
    return revisionManager;
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

  // @SuppressWarnings("unchecked")
  // private static Set finalized = new HashSet();
  //
  // @SuppressWarnings("unchecked")
  // private static List refs = new ArrayList();
  //
  // @SuppressWarnings("unchecked")
  // private static ReferenceQueue queue = new ReferenceQueue();
  //
  @Override
  protected void finalize() throws Throwable
  {
    System.err.println("FINALIZE " + this);
    revisionManager.finalizeRevision(this);
    // long token = id;
    // token <<= 32;
    // token |= version;
    // if (!finalized.add(token))
    // {
    // System.err.println("************************************************************************");
    // }
    //  
    // refs.add(new PhantomReference<Revision>(this, queue));
  }
}
