/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests.cache;

import org.eclipse.net4j.util.io.IOUtil;

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
    return "R" + id + "v" + version; //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Override
  @Deprecated
  protected void finalize() throws Throwable
  {
    IOUtil.ERR().println("FINALIZE " + this); //$NON-NLS-1$
    revisionManager.finalizeRevision(this);
  }
}
