/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOBranchTimeStampGetter;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOBranchTimeStampSetter;

/**
 * Used to get the timestamp from the db into the revision branchpoint.
 *
 * @see CDOBranchTimeStampGetter
 * @see CDOBranchTimeStampSetter
 * @author Martin Taal
 */
public class CDOHibernateBranchPointImpl extends CDOBranchPointImpl
{

  public CDOHibernateBranchPointImpl(long timeStamp)
  {
    super(HibernateThreadContext.getCurrentStoreAccessor().getStore().getRepository().getBranchManager().getMainBranch(), timeStamp);
  }
}
