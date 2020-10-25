/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 * @since 4.12
 */
public interface CDOViewCommitInfoListener extends CDOCommitInfoHandler, IListener
{
  public void notifyCommitInfo(CDOCommitInfo commitInfo);

  @Override
  default void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    notifyCommitInfo(commitInfo);
  }

  @Override
  default void notifyEvent(IEvent event)
  {
    // Do nothing.
  }
}
