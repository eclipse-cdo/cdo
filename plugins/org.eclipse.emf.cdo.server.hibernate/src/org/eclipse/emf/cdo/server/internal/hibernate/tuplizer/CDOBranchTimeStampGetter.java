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

/**
 * Gets the time stamp value to store in the database.
 *
 * @see CDOHibernateBranchPointImpl
 * @author Martin Taal
 */
public class CDOBranchTimeStampGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOBranchTimeStampGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    final InternalCDORevision revision = (InternalCDORevision)target;
    final Object value = revision.getTimeStamp();
    return value;
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
