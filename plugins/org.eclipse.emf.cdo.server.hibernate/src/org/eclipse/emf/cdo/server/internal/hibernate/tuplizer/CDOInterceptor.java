/***************************************************************************
 * Copyright (c) 2004 - 2008 Springsite B.V. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.EmptyInterceptor;

/**
 * Resolves entitynames, todo: use entityname strategy!
 * 
 * @author Martin Taal
 */
public class CDOInterceptor extends EmptyInterceptor
{
  private static final long serialVersionUID = 1L;

  public CDOInterceptor()
  {
  }

  @Override
  public String getEntityName(Object object)
  {
    InternalCDORevision revision = HibernateUtil.getInstance().getCDORevision(object);
    return revision.getCDOClass().getName();
  }
}
