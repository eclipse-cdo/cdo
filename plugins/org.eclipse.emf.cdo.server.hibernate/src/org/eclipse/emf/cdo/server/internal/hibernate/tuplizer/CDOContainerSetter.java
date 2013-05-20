/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.hibernate.engine.spi.SessionFactoryImplementor;

/**
 * Sets the container information from the String (stored in the db) back into the CDORevision.
 * 
 * @see ContainerInfoConverter
 * @author Martin Taal
 */
public class CDOContainerSetter extends CDOPropertySetter
{
  private static final long serialVersionUID = 1L;

  public CDOContainerSetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    final InternalCDORevision revision = (InternalCDORevision)target;
    ContainerInfoConverter.getInstance().setContainerRelationFromString(revision, (String)value);
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
