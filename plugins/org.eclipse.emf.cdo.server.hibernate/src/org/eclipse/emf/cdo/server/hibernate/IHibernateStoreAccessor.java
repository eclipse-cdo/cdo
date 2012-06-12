/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.hibernate;

import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.Session;

/**
 * The Hibernate store accessor works with Hibernate {@link Session} instances
 * to persist changes in the database, it obtains the {@link Session} from the 
 * {@link IHibernateStore}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IHibernateStoreAccessor extends IStoreAccessor.Raw
{
  public IHibernateStore getStore();

  /**
   * @since 2.0
   */
  public IHibernateStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature);

  public Session getHibernateSession();
}
