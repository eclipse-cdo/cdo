/*
 * Copyright (c) 2012, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.file;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFile;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFileHandle;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeFileOperation;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;

import org.eclipse.net4j.util.io.IORuntimeException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class Vob extends LissomeFile
{
  private static final long serialVersionUID = 1L;

  public static final String EXTENSION = "vob";

  private final LissomeFileHandle writer;

  public Vob(LissomeStore store) throws IOException
  {
    super(store, store.getRepository().getName() + "." + EXTENSION);
    writer = openWriter();
    writer.seek(0);
    writer.writeXLong(System.currentTimeMillis());
  }

  @Override
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

  public long addRevision(final CDORevision revision)
  {
    return writer.append(new LissomeFileOperation()
    {
      @Override
      public void execute(LissomeFileHandle writer) throws IOException
      {
        writer.writeCDORevision(revision, CDORevision.UNCHUNKED);
      }
    });
  }

  public void delete(long[] pointers)
  {
    // TODO: implement Vob.enclosing_method(enclosing_method_arguments)
    throw new UnsupportedOperationException();
  }
}
