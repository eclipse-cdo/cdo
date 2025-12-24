/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface StringIO
{
  public static final StringIO DIRECT = new StringIO()
  {
    @Override
    public void write(ExtendedDataOutput out, String string) throws IOException
    {
      out.writeString(string);
    }

    @Override
    public String read(ExtendedDataInput in) throws IOException
    {
      return in.readString();
    }

    @Override
    public String toString()
    {
      return "DIRECT"; //$NON-NLS-1$
    }
  };

  public void write(ExtendedDataOutput out, String string) throws IOException;

  public String read(ExtendedDataInput in) throws IOException;
}
