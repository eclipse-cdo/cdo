/*
 * Copyright (c) 2013, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lob;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A {@link Writer} that produces a {@link #getClob() CDOClob}.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 4.3
 */
public class CDOClobWriter extends Writer
{
  private StringWriter buffer = new StringWriter();

  private CDOClob clob;

  public CDOClobWriter()
  {
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException
  {
    checkBuffer();
    buffer.write(cbuf, off, len);
  }

  @Override
  public void close() throws IOException
  {
    if (buffer != null)
    {
      clob = new CDOClob(new StringReader(buffer.toString()));
      buffer = null;
    }
  }

  @Override
  public void flush() throws IOException
  {
    checkBuffer();
    buffer.flush();
  }

  public CDOClob getClob()
  {
    return clob;
  }

  void checkBuffer() throws IOException
  {
    if (buffer == null)
    {
      throw new IOException("CDOClobWriter closed"); //$NON-NLS-1$
    }
  }
}
