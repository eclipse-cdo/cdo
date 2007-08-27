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
public class StreamWrapperChain<IN extends InputStream, OUT extends OutputStream> extends
    DelegatingStreamWrapper<IN, OUT>
{
  private IStreamWrapper<IN, OUT> head;

  public StreamWrapperChain(IStreamWrapper<IN, OUT> head, IStreamWrapper delegate)
  {
    super(delegate);
    this.head = head;
  }

  public IStreamWrapper<IN, OUT> getHead()
  {
    return head;
  }

  @Override
  protected IN doWrapInputStream(InputStream in) throws IOException
  {
    return head.wrapInputStream(in);
  }

  @Override
  protected OUT doWrapOutputStream(OutputStream out) throws IOException
  {
    return head.wrapOutputStream(out);
  }

  @Override
  protected void doFinishInputStream(IN in) throws IOException
  {
    head.finishInputStream(in);
  }

  @Override
  protected void doFinishOutputStream(OUT out) throws IOException
  {
    head.finishOutputStream(out);
  }
}
