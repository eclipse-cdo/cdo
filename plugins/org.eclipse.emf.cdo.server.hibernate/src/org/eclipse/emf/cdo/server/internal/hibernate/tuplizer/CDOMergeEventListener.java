/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.hibernate.engine.SessionImplementor;
import org.hibernate.event.def.DefaultMergeEventListener;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Map;

/**
 * The CDOMergeEventListener prevents copying of values of an existing entity to itself.
 */
public class CDOMergeEventListener extends DefaultMergeEventListener
{
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("rawtypes")
  @Override
  protected void copyValues(final EntityPersister persister, final Object entity, final Object target,
      final SessionImplementor source, final Map copyCache)
  {
    if (entity != target)
    {
      super.copyValues(persister, entity, target, source, copyCache);
    }
  }
}
