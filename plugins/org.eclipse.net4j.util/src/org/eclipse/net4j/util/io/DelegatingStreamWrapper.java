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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingStreamWrapper<IN extends InputStream, OUT extends OutputStream> implements
    IStreamWrapper<IN, OUT>
{
  private IStreamWrapper delegate;

  public DelegatingStreamWrapper(IStreamWrapper delegate)
  {
    this.delegate = delegate;
  }

  public IStreamWrapper getDelegate()
  {
    return delegate;
  }

  public IN wrapInputStream(InputStream in) throws IOException
  {
    return doWrapInputStream(delegate.wrapInputStream(in));
  }

  public OUT wrapOutputStream(OutputStream out) throws IOException
  {
    return doWrapOutputStream(delegate.wrapOutputStream(out));
  }

  public void finishInputStream(IN in) throws IOException
  {
    delegate.finishInputStream(in);
    doFinishInputStream(in);
  }

  public void finishOutputStream(OUT out) throws IOException
  {
    delegate.finishOutputStream(out);
    doFinishOutputStream(out);
  }

  protected abstract IN doWrapInputStream(InputStream in) throws IOException;

  protected abstract OUT doWrapOutputStream(OutputStream out) throws IOException;

  protected abstract void doFinishInputStream(IN in) throws IOException;

  protected abstract void doFinishOutputStream(OUT out) throws IOException;
}
