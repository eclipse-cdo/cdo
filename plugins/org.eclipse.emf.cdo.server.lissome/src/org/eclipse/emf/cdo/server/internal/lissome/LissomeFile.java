/*
 * Copyright (c) 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Eike Stepper
 */
public class LissomeFile extends File
{
  public static final String READ_MODE = "r";

  public static final String WRITE_MODE = "rws";

  private static final long serialVersionUID = 1L;

  protected final LissomeStore store;

  public LissomeFile(LissomeStore store, String path) throws FileNotFoundException
  {
    super(store.getFolder(), path);
    this.store = store;
  }

  public LissomeStore getStore()
  {
    return store;
  }

  public LissomeFileHandle openReader()
  {
    return openHandle(READ_MODE);
  }

  public LissomeFileHandle openWriter()
  {
    return openHandle(WRITE_MODE);
  }

  protected LissomeFileHandle openHandle(String mode)
  {
    try
    {
      return new LissomeFileHandle(this, mode);
    }
    catch (FileNotFoundException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface RevisionProvider
  {
    public CDORevision getRevision(long pointer);
  }
}
