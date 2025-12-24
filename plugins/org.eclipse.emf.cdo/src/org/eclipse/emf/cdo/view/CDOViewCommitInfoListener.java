/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
 * A {@link IListener listener} that, when added to a {@link CDOView view}, {@link #notifyCommitInfo(CDOCommitInfo) notifies}
 * about {@link CDOCommitInfo commits} to the view's {@link CDOView#getBranch() branch}.
 * <p>
 * This is an alternative approach to listening for {@link CDOViewInvalidationEvent view invalidation events},
 * which impose a few usage challenges:
 * <ol>
 * <li> They're fired while the view is holding the view lock, which can lead to deadlock in a listener.
 * <li> They report only changes about objects that are actually loaded in the view, not all objects in the original commit.
 * </ol>
 *
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
