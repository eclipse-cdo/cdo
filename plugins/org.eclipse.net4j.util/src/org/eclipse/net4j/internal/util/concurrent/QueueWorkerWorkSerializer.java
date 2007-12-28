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
package org.eclipse.net4j.internal.util.concurrent;

import org.eclipse.net4j.internal.util.lifecycle.QueueWorker;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class QueueWorkerWorkSerializer extends QueueWorker<Runnable> implements IWorkSerializer
{
  public QueueWorkerWorkSerializer()
  {
    LifecycleUtil.activate(this);
  }

  public void dispose()
  {
    deactivate();
  }

  @Override
  protected void work(WorkContext context, Runnable element)
  {
    element.run();
  }
}
