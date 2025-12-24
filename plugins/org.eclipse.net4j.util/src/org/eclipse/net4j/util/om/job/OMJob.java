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
package org.eclipse.net4j.util.om.job;

import org.eclipse.net4j.internal.util.om.InternalOMJob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * @author Eike Stepper
 * @since 3.13
 */
public abstract class OMJob extends InternalOMJob
{
  public OMJob(String name)
  {
    super(name);
  }

  @Override
  public final String getName()
  {
    return super.getName();
  }

  @Override
  public final void setName(String name)
  {
    super.setName(name);
  }

  @Override
  public final boolean isSystem()
  {
    return super.isSystem();
  }

  @Override
  public final void setSystem(boolean system)
  {
    super.setSystem(system);
  }

  public final void schedule()
  {
    internalSchedule();
  }

  public final void cancel()
  {
    internalCancel();
  }

  @Override
  protected abstract IStatus run(IProgressMonitor monitor);
}
