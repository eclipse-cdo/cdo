/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * An identifiable binary large object with streaming support.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOBlob extends CDOLob<InputStream>
{
  public CDOBlob(InputStream contents) throws IOException
  {
    super(contents, CDOLobStoreImpl.INSTANCE);
  }

  public CDOBlob(InputStream contents, CDOLobStore store) throws IOException
  {
    super(contents, store);
  }

  /**
   * @since 4.13
   */
  public CDOBlob(byte[] contents, CDOLobStore store) throws IOException
  {
    super(new ByteArrayInputStream(contents), store);
  }

  /**
   * @since 4.13
   */
  public CDOBlob(String contentsHexString, CDOLobStore store) throws IOException
  {
    super(new ByteArrayInputStream(HexUtil.hexToBytes(contentsHexString)), store);
  }

  CDOBlob(byte[] id, long size)
  {
    super(id, size);
  }

  CDOBlob(ExtendedDataInput in) throws IOException
  {
    super(in);
  }

  @Override
  public File getStoreFile()
  {
    return getStore().getBinaryFile(getID());
  }

  @Override
  public InputStream getContents() throws IOException
  {
    return getStore().getBinary(this);
  }

  /**
   * @since 4.13
   */
  public byte[] getBytes() throws IOException
  {
    InputStream inputStream = null;

    try
    {
      inputStream = getContents();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtil.copy(inputStream, outputStream);
      return outputStream.toByteArray();
    }
    finally
    {
      IOUtil.close(inputStream);
    }
  }

  /**
   * @since 4.13
   */
  @Override
  public String getString() throws IOException
  {
    return HexUtil.bytesToHex(getBytes());
  }

  @Override
  protected CDOLobInfo put(InputStream contents) throws IOException
  {
    return getStore().putBinary(contents);
  }
}
