/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 3.4
 */
public abstract class FileLock
{
  private final File lockFile;

  private FileLock(File file)
  {
    lockFile = new File(file.getParentFile(), file.getName() + ".lock");
  }

  public final File getLockFile()
  {
    return lockFile;
  }

  public void release()
  {
    lockFile.delete();
  }

  public static FileLock forRead(File file)
  {
    return new FileLock.ForRead(file);
  }

  public static FileLock forWrite(File file)
  {
    return new FileLock.ForWrite(file);
  }

  /**
   * @author Eike Stepper
   */
  private static final class ForRead extends FileLock
  {
    private FileInputStream stream;

    public ForRead(File file)
    {
      super(file);

      File lockFile = getLockFile();

      for (;;)
      {
        try
        {
          stream = new FileInputStream(lockFile);
          return;
        }
        catch (FileNotFoundException ex)
        {
          try
          {
            new FileOutputStream(lockFile).close();
          }
          catch (IOException ex1)
          {
            throw new IORuntimeException(ex1);
          }
        }
      }
    }

    @Override
    public void release()
    {
      try
      {
        stream.close();
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }

      super.release();
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ForWrite extends FileLock
  {
    private FileOutputStream stream;

    public ForWrite(File file)
    {
      super(file);

      File lockFile = getLockFile();
      try
      {
        stream = new FileOutputStream(lockFile);
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    @Override
    public void release()
    {
      try
      {
        stream.close();
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }

      super.release();
    }
  }
}
