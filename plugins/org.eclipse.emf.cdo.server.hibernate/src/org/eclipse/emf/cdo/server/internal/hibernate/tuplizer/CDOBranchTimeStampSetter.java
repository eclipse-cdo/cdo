/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateBranchPointImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;

/**
 * Sets the time stamp information in the revision.
 *
 * @see CDOHibernateBranchPointImpl
 * @author Martin Taal
 */
public class CDOBranchTimeStampSetter extends CDOPropertySetter
{
  private static final long serialVersionUID = 1L;

  public CDOBranchTimeStampSetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    final InternalCDORevision revision = (InternalCDORevision)target;
    final long timeStamp;
    if (value == null)
    {
      timeStamp = 0;
    }
    else
    {
      timeStamp = (Long)value;
    }
    final CDOHibernateBranchPointImpl branchPoint = new CDOHibernateBranchPointImpl(timeStamp);
    revision.setBranchPoint(branchPoint);
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
