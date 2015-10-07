/*
 * Copyright (c) 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

/**
 * A strategy used to customize the default conflict resolution behavior of {@link CDOTransaction transactions}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDOConflictResolver3 extends CDOConflictResolver
{
  /**
   * @return <code>false</code> to abort the commit operation, <code>true</code> otherwise.
   */
  public boolean preCommit();
}
