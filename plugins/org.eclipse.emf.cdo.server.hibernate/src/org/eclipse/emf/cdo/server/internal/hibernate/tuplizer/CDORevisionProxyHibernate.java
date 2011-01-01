/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.spi.common.revision.DelegatingCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * @author Eike Stepper
 */
public class CDORevisionProxyHibernate extends DelegatingCDORevision implements HibernateProxy
{
  private static final long serialVersionUID = 1L;

  private CDORevisionLazyInitializer li;

  CDORevisionProxyHibernate(CDORevisionLazyInitializer li)
  {
    this.li = li;
  }

  public InternalCDORevision copy()
  {
    return new CDORevisionProxyHibernate(li);
  }

  @Override
  public InternalCDORevision getDelegate()
  {
    return li.getRevision();
  }

  public LazyInitializer getHibernateLazyInitializer()
  {
    return li;
  }

  public Object writeReplace()
  {
    return this;
  }
}
