/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.CDOInvalidationNotification;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IListener;

/**
 * An event that is emitted to registered {@link IListener listeners} of a {@link CDOSession} if
 * {@link CDOSession.Options#setPassiveUpdateEnabled(boolean) passive update} is enabled for the session.
 * 
 * @author Eike Stepper
 * @see CDOInvalidationNotification
 * @see CDOAdapterPolicy
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOSessionInvalidationEvent extends CDOSessionEvent, CDOCommitInfo
{
  public static final long LOCAL_ROLLBACK = CDORevision.UNSPECIFIED_DATE;

  /**
   * Returns the transaction that was committed and thereby caused this event to be emitted if this transaction is
   * local, or <code>null</code> if the transaction was remote.
   * 
   * @since 4.0
   */
  public CDOTransaction getLocalTransaction();

  /**
   * @deprecated Use {@link #getLocalTransaction()}.
   */
  @Deprecated
  public CDOView getView();

  /**
   * @since 3.0
   */
  public boolean isRemote();
}
