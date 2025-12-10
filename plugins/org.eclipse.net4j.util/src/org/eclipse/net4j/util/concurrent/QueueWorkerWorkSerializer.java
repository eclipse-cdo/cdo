/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

/**
 * @author Eike Stepper
 * @deprecated As of 3.6 use {@link ExecutorWorkSerializer}.
 */
@Deprecated
public class QueueWorkerWorkSerializer extends QueueRunner implements IWorkSerializer
{
  @Deprecated
  public QueueWorkerWorkSerializer()
  {
    setDaemon(true);
    activate();
  }

  @Deprecated
  @Override
  public void dispose()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }
}
