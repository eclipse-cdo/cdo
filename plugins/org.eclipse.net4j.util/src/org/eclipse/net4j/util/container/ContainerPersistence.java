/*
 * Copyright (c) 2015, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.container.IContainer.Persistence;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 3.5
 */
public abstract class ContainerPersistence<E> implements Persistence<E>
{
  @Override
  public Collection<E> loadElements() throws IORuntimeException
  {
    InputStream out = null;

    try
    {
      out = openInputStream();

      ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(out));

      @SuppressWarnings("unchecked")
      Collection<E> elements = (Collection<E>)oos.readObject();
      return elements;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    catch (ClassNotFoundException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(out);
    }
  }

  @Override
  public void saveElements(Collection<E> elements) throws IORuntimeException
  {
    OutputStream out = null;

    try
    {
      out = openOutputStream();

      ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(out));
      oos.writeObject(elements);
      oos.flush();
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(out);
    }
  }

  protected abstract InputStream openInputStream() throws IOException;

  protected abstract OutputStream openOutputStream() throws IOException;
}
