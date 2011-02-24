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
package org.eclipse.emf.cdo.threedee.agent;

/**
 * @author Eike Stepper
 */
public aspect Aspect
{
  pointcut publicMethods() :
    //execution(public * *.*(..)) ||

    execution(public * org.eclipse.emf.cdo.internal.server.Repository.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.CommitManager.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.LockManager.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.QueryManager.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.SessionManager.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.Session.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.View.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.Transaction.*(..)) ||

    execution(public * org.eclipse.emf.cdo.internal.server.mem.MEMStore.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.mem.MEMStoreAccessor.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.server.mem.MEMStoreChunkReader.*(..)) ||

    execution(public * org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl.*(..)) ||

    execution(public * org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.revision.CDORevisionCacheImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.spi.common.revision.AbstractCDORevision.*(..)) ||
    execution(public * org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl.*(..)) ||

    execution(public * org.eclipse.emf.cdo.internal.common.model.CDOPackageRegistryImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.model.CDOPackageUnitImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.model.CDOPackageInfoImpl.*(..)) ||
    execution(public * org.eclipse.emf.cdo.internal.common.model.CDOClassInfoImpl.*(..));

  before(Object target) : publicMethods() && target(target)
  {
    Hook.before(target);
  }

  after(Object target) : publicMethods() && target(target)
  {
    Hook.after(target);
  }
}
