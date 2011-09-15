/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.ContainerInfoConverter;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

/**
 * gets the container information as a String (to-be-stored in the db) from the CDORevision.
 * 
 * @see ContainerInfoConverter
 * @author Martin Taal
 */
public class CDOContainerGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOContainerGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    final InternalCDORevision revision = (InternalCDORevision)target;
    return ContainerInfoConverter.getInstance().convertContainerRelationToString(revision);
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
