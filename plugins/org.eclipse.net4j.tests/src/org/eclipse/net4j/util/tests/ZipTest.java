/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.io.ZIPUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class ZipTest extends AbstractOMTest
{
  public void testZip() throws Exception
  {
    File zipFile = newFile("src.zip"); //$NON-NLS-1$

    try
    {
      File sourceFolder = newFile("src"); //$NON-NLS-1$
      ZIPUtil.zip(sourceFolder, false, zipFile);
    }
    finally
    {
      zipFile.delete();
    }
  }

  private static File newFile(String path) throws IOException
  {
    return new File(path).getCanonicalFile();
  }
}
