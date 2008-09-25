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

/**
 * A read-only view to a <em>historical</em> state of the object graph in the repository specified by a time stamp (i.e.
 * an arbitrary point in the lifetime of the repository).
 * 
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOAudit extends CDOView
{
  public long getTimeStamp();

  /**
   * @since 2.0
   */
  public void setTimeStamp(long timeStamp);
}
