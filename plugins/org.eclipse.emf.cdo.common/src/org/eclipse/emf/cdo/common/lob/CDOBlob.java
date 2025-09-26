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
import java.io.OutputStream;

/**
 * An identifiable binary large object with streaming support.
 * <p>
 * CDOBlobs are immutable. Once created, their {@link #getID() ID} and {@link #getSize() size} do not change.
 * The ID is a digest of the content of the blob, SHA-1 by default (see {@link CDOLobStoreImpl#DEFAULT_DIGEST_ALGORITHM}).
 * <p>
 * CDOBlobs can be created from an {@link InputStream}, a <code>byte[]</code>, or a hex-encoded {@link String}.
 * On the client side, CDOBlobs are created with a reference to a {@link CDOLobStore}, which is used to store and retrieve
 * the blob's content. On the server side, CDOBlobs are created without a store reference and are always retrieved from
 * the repository's <code>IStore</code>.
 * <p>
 * The default <code>CDOLobStore</code> implementation is {@link CDOLobStoreImpl#INSTANCE}, which stores blobs in the local file system.
 * But on the client side, it's often more efficient to use a dedicated <code>CDOLobStore</code> instance that stores blobs in
 * a <code>CDOSession</code>-specific cache location. See <code>CDOSession.Options.setLobCache(CDOLobStore)</code> for details.
 * See also <code>CDOSession.newBlob(InputStream)</code> and <code>CDOSession.newBlob(byte[])</code> for convenient ways to create CDOBlobs.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOBlob extends CDOLob<InputStream>
{
  public CDOBlob(InputStream contents) throws IOException
  {
    this(contents, DEFAULT_STORE);
  }

  public CDOBlob(InputStream contents, CDOLobStore store) throws IOException
  {
    super(contents, store);
  }

  /**
   * @since 4.27
   */
  public CDOBlob(byte[] contents) throws IOException
  {
    this(contents, DEFAULT_STORE);
  }

  /**
   * @since 4.13
   */
  public CDOBlob(byte[] contents, CDOLobStore store) throws IOException
  {
    super(new ByteArrayInputStream(contents), store);
  }

  /**
   * @since 4.27
   */
  public CDOBlob(String contentsHexString) throws IOException
  {
    this(contentsHexString, DEFAULT_STORE);
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
    try (InputStream inputStream = getContents())
    {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtil.copy(inputStream, outputStream);
      return outputStream.toByteArray();
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

  /**
   * @since 4.27
   */
  public void copyTo(OutputStream out) throws IOException
  {
    try (InputStream in = getContents())
    {
      IOUtil.copy(in, out);
    }
  }

  @Override
  protected CDOLobInfo put(InputStream contents) throws IOException
  {
    return getStore().putBinary(contents);
  }
}
