/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2019, 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import org.eclipse.emf.cdo.spi.common.CDOLobStoreImpl;

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

/**
 * An identifiable character large object with streaming support.
 * <p>
 * CDOClobs are immutable. Once created, their {@link #getID() ID} and {@link #getSize() size} do not change.
 * The ID is a digest of the content of the clob, SHA-1 by default (see {@link CDOLobStoreImpl#DEFAULT_DIGEST_ALGORITHM}).
 * <p>
 * CDOClobs can be created from a {@link Reader}, or a {@link String}.
 * On the client side, CDOClobs are created with a reference to a {@link CDOLobStore}, which is used to store and retrieve
 * the clob's content. On the server side, CDOClobs are created without a store reference and are always retrieved from
 * the repository's <code>IStore</code>.
 * <p>
 * The default <code>CDOLobStore</code> implementation is {@link CDOLobStoreImpl#INSTANCE}, which stores clobs in the local file system.
 * But on the client side, it's often more efficient to use a dedicated <code>CDOLobStore</code> instance that stores clobs in
 * a <code>CDOSession</code>-specific cache location. See <code>CDOSession.Options.setLobCache(CDOLobStore)</code> for details.
 * See also <code>CDOSession.newClob(Reader)</code> and <code>CDOSession.newClob(String)</code> for convenient ways to create CDOClobs.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public final class CDOClob extends CDOLob<Reader>
{
  public CDOClob(Reader contents) throws IOException
  {
    this(contents, DEFAULT_STORE);
  }

  public CDOClob(Reader contents, CDOLobStore store) throws IOException
  {
    super(contents, store);
  }

  /**
   * @since 4.27
   */
  public CDOClob(String contents) throws IOException
  {
    this(contents, DEFAULT_STORE);
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
  public File getStoreFile()
  {
    return getStore().getCharacterFile(getID());
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
    try (Reader reader = getContents())
    {
      CharArrayWriter writer = new CharArrayWriter();
      IOUtil.copyCharacter(reader, writer);
      return writer.toString();
    }
  }

  /**
   * @since 4.27
   */
  public void copyTo(Writer writer) throws IOException
  {
    try (Reader reader = getContents())
    {
      IOUtil.copyCharacter(reader, writer);
    }
  }

  @Override
  protected CDOLobInfo put(Reader contents) throws IOException
  {
    return getStore().putCharacter(contents);
  }
}
