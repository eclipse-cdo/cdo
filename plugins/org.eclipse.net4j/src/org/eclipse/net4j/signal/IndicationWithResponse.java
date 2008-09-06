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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class IndicationWithResponse extends Indication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, IndicationWithResponse.class);

  protected IndicationWithResponse()
  {
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    super.execute(in, out);
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Responding " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    OutputStream wrappedOutputStream = wrapOutputStream(out);

    try
    {
      responding(ExtendedDataOutputStream.wrap(wrappedOutputStream));
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      sendExceptionMessage(ex);
      throw ex;
    }
    finally
    {
      finishOutputStream(wrappedOutputStream);
    }

    // End response
    out.flushWithEOS();
  }

  /**
   * <b>Important Note:</b> The response must not be empty, i.e. the stream must be used at least to write a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract void responding(ExtendedDataOutputStream out) throws IOException;
}
