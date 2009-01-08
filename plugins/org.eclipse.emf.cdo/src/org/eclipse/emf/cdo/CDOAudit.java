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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.revision.CDORevision;

/**
 * A read-only {@link CDOView view} to a <em>historical</em> state of the object graph in the repository specified by a
 * time stamp (i.e. an arbitrary point in the lifetime of the repository).
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAudit extends CDOView
{
  /**
   * Returns the point in (repository) time this audit view is currently referring to. {@link CDOObject Objects}
   * provided by this view are {@link CDORevision#isValid(long) valid} at this time.
   */
  public long getTimeStamp();

  /**
   * Sets the point in (repository) time this audit view should refer to. {@link CDOObject Objects} provided by this
   * view will be {@link CDORevision#isValid(long) valid} at this time.
   * 
   * @since 2.0
   */
  public void setTimeStamp(long timeStamp);
}
