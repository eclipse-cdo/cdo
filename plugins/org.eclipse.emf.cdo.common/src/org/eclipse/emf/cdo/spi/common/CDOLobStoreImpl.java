/*
 * Copyright (c) 2011, 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.emf.cdo.common.lob.CDOLobInfo;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.DigestWriter;
import org.eclipse.net4j.util.io.ExpectedFileInputStream;
import org.eclipse.net4j.util.io.ExpectedFileReader;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class CDOLobStoreImpl implements CDOLobStore
{
  public static final CDOLobStoreImpl INSTANCE = new CDOLobStoreImpl();

  private long timeout = IOUtil.DEFAULT_TIMEOUT;

  private File folder;

  private int tempID;

  public CDOLobStoreImpl(File folder)
  {
    this.folder = folder;
    if (folder.exists())
    {
      checkDirectory();
    }
  }

  public CDOLobStoreImpl()
  {
    this(getDefaultFolder());
  }

  /**
   * @since 4.8
   */
  public long getTimeout()
  {
    return timeout;
  }

  /**
   * @since 4.8
   */
  public void setTimeout(long timeout)
  {
    this.timeout = timeout;
  }

  public File getFolder()
  {
    if (!folder.exists())
    {
      folder.mkdirs();
    }

    checkDirectory();
    return folder;
  }

  @Override
  public File getBinaryFile(byte[] id)
  {
    return new File(getFolder(), HexUtil.bytesToHex(id) + ".blob");
  }

  @Override
  public InputStream getBinary(CDOLobInfo info) throws IOException
  {
    File file = getBinaryFile(info.getID());
    long expectedSize = info.getSize();
    ExpectedFileInputStream inputStream = new ExpectedFileInputStream(file, expectedSize);
    inputStream.setTimeout(timeout);
    return inputStream;
  }

  @Override
  public CDOLobInfo putBinary(InputStream contents) throws IOException
  {
    File tempFile = getTempFile();
    MessageDigest digest = createDigest();
    digest.update("BINARY".getBytes());

    FileOutputStream fos = null;
    long size;

    try
    {
      fos = new FileOutputStream(tempFile);
      DigestOutputStream dos = new DigestOutputStream(fos, digest);
      size = IOUtil.copyBinary(contents, dos);
    }
    finally
    {
      IOUtil.close(fos);
    }

    byte[] id = digest.digest();
    makePermanent(tempFile, getBinaryFile(id));
    return new CDOLobInfo(id, size);
  }

  @Override
  public File getCharacterFile(byte[] id)
  {
    return new File(getFolder(), HexUtil.bytesToHex(id) + ".clob");
  }

  @Override
  public Reader getCharacter(CDOLobInfo info) throws IOException
  {
    File file = getCharacterFile(info.getID());
    long expectedSize = info.getSize();
    ExpectedFileReader reader = new ExpectedFileReader(file, expectedSize);
    reader.setTimeout(timeout);
    return reader;

  }

  @Override
  public CDOLobInfo putCharacter(Reader contents) throws IOException
  {
    File tempFile = getTempFile();
    MessageDigest digest = createDigest();
    digest.update("CHARACTER".getBytes());

    FileWriter fw = null;
    long size;

    try
    {
      fw = new FileWriter(tempFile);
      DigestWriter dw = new DigestWriter(fw, digest);
      size = IOUtil.copyCharacter(contents, dw);
    }
    finally
    {
      IOUtil.close(fw);
    }

    byte[] id = digest.digest();
    makePermanent(tempFile, getCharacterFile(id));
    return new CDOLobInfo(id, size);
  }

  @Override
  public String toString()
  {
    return "CDOLobStore[" + folder + "]";
  }

  protected MessageDigest createDigest()
  {
    try
    {
      return MessageDigest.getInstance("SHA-1");
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  protected synchronized File getTempFile()
  {
    for (;;)
    {
      ++tempID;
      File file = new File(getFolder(), "contents" + tempID + ".tmp");
      if (!file.exists())
      {
        return file;
      }
    }
  }

  private void makePermanent(File tempFile, File file)
  {
    if (file.exists())
    {
      tempFile.delete();
    }
    else
    {
      tempFile.renameTo(file);
    }
  }

  private void checkDirectory()
  {
    if (!folder.isDirectory())
    {
      throw new IORuntimeException("Not a folder: " + folder.getAbsolutePath());
    }
  }

  private static File getDefaultFolder()
  {
    String path = OMPlatform.INSTANCE.getProperty("java.io.tmpdir");
    return new File(new File(path), "cdo_lobs");
  }
}
