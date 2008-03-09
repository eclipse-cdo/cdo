/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.io;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class TMPUtil
{
  private TMPUtil()
  {
  }

  public static File createTempFolder() throws IORuntimeException
  {
    return createTempFolder("tmp");
  }

  public static File createTempFolder(String prefix) throws IORuntimeException
  {
    return createTempFolder(prefix, "");
  }

  public static File createTempFolder(String prefix, String suffix) throws IORuntimeException
  {
    return createTempFolder(prefix, suffix, null);
  }

  public static File createTempFolder(String prefix, String suffix, File directory) throws IORuntimeException
  {
    // TODO Consider servlets
    // ServletContext context = session.getServletContext();
    // File tempDir = (File)context.getAttribute("javax.servlet.context.tempdir");

    try
    {
      File tmp = File.createTempFile(prefix, suffix, directory);
      String tmpPath = tmp.getAbsolutePath();
      tmp.delete();
      tmp = new File(tmpPath);
      tmp.mkdirs();
      return tmp;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
