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
package org.eclipse.net4j.internal.jms;

import javax.jms.TemporaryQueue;

public class TemporaryQueueImpl extends QueueImpl implements TemporaryQueue
{
  private static final long serialVersionUID = 1L;

  private static int counter = 0;

  public TemporaryQueueImpl()
  {
    super("TempQueue" + (++counter));
  }

  public void delete()
  {
    throw new NotYetImplementedException();
  }
}
