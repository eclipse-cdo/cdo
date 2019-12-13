/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
public class ExtendedIOAdapter implements ExtendedIOHandler
{
  public ExtendedIOAdapter()
  {
  }

  @Override
  public void handleIn(ExtendedDataInputStream in) throws IOException
  {
  }

  @Override
  public void handleOut(ExtendedDataOutputStream out) throws IOException
  {
  }
}
