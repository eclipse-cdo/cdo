/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public class FileContainerPersistence<E> extends ContainerPersistence<E>
{
  private final File file;

  public FileContainerPersistence(File file)
  {
    this.file = file;
  }

  public final File getFile()
  {
    return file;
  }

  @Override
  protected InputStream openInputStream() throws IOException
  {
    return new FileInputStream(file);
  }

  @Override
  protected OutputStream openOutputStream() throws IOException
  {
    return new FileOutputStream(file);
  }
}
