/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.AbstractLazyInitializer;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public class CDORevisionLazyInitializer extends AbstractLazyInitializer implements Serializable
{
  private static final long serialVersionUID = 1L;

  CDORevisionLazyInitializer(String entityName, Serializable id, SessionImplementor session)
  {
    super(entityName, id, session);
  }

  public InternalCDORevision getRevision()
  {
    return (InternalCDORevision)getImplementation();
  }

  @SuppressWarnings("rawtypes")
  public Class getPersistentClass()
  {
    throw new UnsupportedOperationException();
  }
}
