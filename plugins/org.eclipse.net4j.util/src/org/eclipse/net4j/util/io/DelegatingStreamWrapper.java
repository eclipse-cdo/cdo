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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingStreamWrapper implements IStreamWrapper
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

  public InputStream wrapInputStream(InputStream in)
  {
    return doWrapInputStream(delegate.wrapInputStream(in));
  }

  public OutputStream wrapOutputStream(OutputStream out)
  {
    return doWrapOutputStream(delegate.wrapOutputStream(out));
  }

  protected abstract InputStream doWrapInputStream(InputStream in);

  protected abstract OutputStream doWrapOutputStream(OutputStream out);
}
