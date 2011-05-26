/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */

package org.eclipse.emf.cdo.tests.db4o;

import org.eclipse.emf.cdo.server.internal.db4o.DB4OStore;

import com.db4o.ObjectContainer;
import com.db4o.ext.ExtDb4o;
import com.db4o.ext.MemoryFile;

/**
 * In-memory based DB4OStore. Just meant to be used to accelerate DB4OStore test-cases.
 * 
 * @author Victor Roldan Betancort
 */
public final class MEMDB4OStore extends DB4OStore
{

  private static ObjectContainer objectContainer;

  public MEMDB4OStore()
  {
    super(null, 0);
  }

  @Override
  public ObjectContainer openClient()
  {
    if (objectContainer == null)
    {
      objectContainer = ExtDb4o.openMemoryFile(new MemoryFile());
    }
    return objectContainer;
  }

  @Override
  protected void initObjectServer()
  {
    // no server is defined. Objects are mantained in-memory in ObjectContainer instances
  }

  @Override
  protected void tearDownObjectServer()
  {
    // no server is defined. Objects are mantained in-memory in ObjectContainer instances
  }

  @Override
  protected void closeClient(ObjectContainer container)
  {
    // no server is defined. Objects are mantained in-memory in ObjectContainer instances
  }

  public static void clearContainer()
  {
    objectContainer = ExtDb4o.openMemoryFile(new MemoryFile());
  }
}
