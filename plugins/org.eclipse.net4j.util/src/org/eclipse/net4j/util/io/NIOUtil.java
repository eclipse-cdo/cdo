/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.ImplementationError;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author Eike Stepper
 */
public final class NIOUtil
{
  private NIOUtil()
  {
  }

  public static void copyFile(File source, File target)
  {
    FileChannel sourceChannel = null;
    FileChannel targetChannel = null;

    try
    {
      if (!target.exists())
      {
        target.createNewFile();
      }

      sourceChannel = new FileInputStream(source).getChannel();
      targetChannel = new FileOutputStream(target).getChannel();

      long size = sourceChannel.size();
      long transfered = sourceChannel.transferTo(0, size, targetChannel);
      if (transfered != size)
      {
        throw new ImplementationError("Seems as if a loop must be implemented here");
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(sourceChannel);
      IOUtil.closeSilent(targetChannel);
    }
  }
}
