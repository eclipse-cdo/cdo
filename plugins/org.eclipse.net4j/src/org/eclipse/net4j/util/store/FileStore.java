/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class FileStore<CONTENT> extends Store<CONTENT>
{
  private File file;

  public FileStore(File file)
  {
    this.file = file;
  }

  public File getFile()
  {
    return file;
  }

  @Override
  protected InputStream getInputStream() throws IOException
  {
    return new FileInputStream(file);
  }

  @Override
  protected OutputStream getOutputStream() throws IOException
  {
    return new FileOutputStream(file);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("FileStore[{0}]", file.getAbsolutePath());
  }
}
