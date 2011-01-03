/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.concurrent.IRWLockManager;

/**
 * The type of the to-be-locked objects is either {@link CDOIDAndBranch} or {@link CDOID}, depending on whether
 * branching is supported by the repository or not.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalLockManager extends IRWLockManager<Object, IView>
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  /**
   * @since 4.0
   */
  public Object getLockEntryObject(Object key);
}
