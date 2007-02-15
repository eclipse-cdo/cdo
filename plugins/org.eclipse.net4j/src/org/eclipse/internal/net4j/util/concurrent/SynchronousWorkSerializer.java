/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.concurrent;

import org.eclipse.net4j.util.concurrent.IWorkSerializer;

/**
 * @author Eike Stepper
 */
public class SynchronousWorkSerializer implements IWorkSerializer
{
  public SynchronousWorkSerializer()
  {
  }

  public void addWork(Runnable work)
  {
    work.run();
  }

  public void dispose()
  {
  }

  @Override
  public String toString()
  {
    return "SynchronousWorkSerializer"; //$NON-NLS-1$
  }
}
