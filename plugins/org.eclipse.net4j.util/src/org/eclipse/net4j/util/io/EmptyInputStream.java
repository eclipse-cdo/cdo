/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
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
import java.io.InputStream;

/**
 * @author Eike Stepper
 * @since 3.14
 */
public class EmptyInputStream extends InputStream
{
  public EmptyInputStream()
  {
  }

  @Override
  public int read() throws IOException
  {
    return IOUtil.EOF;
  }
}
