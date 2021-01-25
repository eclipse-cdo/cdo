/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * An identifiable character large object with streaming support.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOClob extends CDOLob<Reader>
{
  public CDOClob(Reader contents) throws IOException
  {
    super(contents, CDOLobStoreImpl.INSTANCE);
  }

  public CDOClob(Reader contents, CDOLobStore store) throws IOException
  {
    super(contents, store);
  }

  /**
   * @since 4.13
   */
  public CDOClob(String contents, CDOLobStore store) throws IOException
  {
    super(new StringReader(contents), store);
  }

  CDOClob(byte[] id, long size)
  {
    super(id, size);
  }

  CDOClob(ExtendedDataInput in) throws IOException
  {
    super(in);
  }

  @Override
  public Reader getContents() throws IOException
  {
    return getStore().getCharacter(this);
  }

  /**
   * @since 4.13
   */
  @Override
  public String getString() throws IOException
  {
    Reader reader = null;

    try
    {
      reader = getContents();

      CharArrayWriter writer = new CharArrayWriter();
      IOUtil.copyCharacter(reader, writer);
      return writer.toString();
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  @Override
  protected CDOLobInfo put(Reader contents) throws IOException
  {
    return getStore().putCharacter(contents);
  }
}
