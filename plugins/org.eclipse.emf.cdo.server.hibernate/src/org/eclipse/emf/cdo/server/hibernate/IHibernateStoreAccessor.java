/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate;

import org.eclipse.emf.cdo.common.model.CDOFeature;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreAccessor;

import org.hibernate.Session;

/**
 * @author Eike Stepper
 */
public interface IHibernateStoreAccessor extends IStoreAccessor
{
  public IHibernateStore getStore();

  /**
   * @since 2.0
   */
  public IHibernateStoreChunkReader createChunkReader(CDORevision revision, CDOFeature feature);

  public Session getHibernateSession();
}
