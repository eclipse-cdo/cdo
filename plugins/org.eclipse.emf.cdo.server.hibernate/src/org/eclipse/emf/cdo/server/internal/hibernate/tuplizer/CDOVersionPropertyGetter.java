/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

/**
 * @author Martin Taal
 */
public class CDOVersionPropertyGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOVersionPropertyGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    if (isVirtualProperty())
    {
      return revision.getVersion();
    }

    Object version = super.get(target);
    // TODO: does this make sense?
    // revision.setVersion(((Number)value).intValue());
    return version;
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
