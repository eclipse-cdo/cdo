/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.om;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Eike Stepper
 */
public abstract class InternalOMJob
{
  private String name;

  private boolean system;

  public InternalOMJob(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
    ((AbstractPlatform)OMPlatform.INSTANCE).renameJob(this, name);
  }

  public boolean isSystem()
  {
    return system;
  }

  public void setSystem(boolean system)
  {
    this.system = system;
  }

  protected final void internalSchedule()
  {
    ((AbstractPlatform)OMPlatform.INSTANCE).scheduleJob(this);
  }

  protected final void internalCancel()
  {
    ((AbstractPlatform)OMPlatform.INSTANCE).cancelJob(this);
  }

  protected abstract IStatus run(IProgressMonitor monitor);
}
