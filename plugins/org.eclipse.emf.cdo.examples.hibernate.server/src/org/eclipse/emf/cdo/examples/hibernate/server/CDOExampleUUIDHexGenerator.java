/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */

package org.eclipse.emf.cdo.examples.hibernate.server;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.UUIDHexGenerator;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;

/**
 * An example of overriding the standard UUID generator of Hibernate to prevent it overwriting an already existing id in
 * an object.
 * 
 * @author mtaal
 */

public class CDOExampleUUIDHexGenerator extends UUIDHexGenerator
{
  @Override
  public Serializable generate(SessionImplementor session, Object obj)
  {
    final EntityPersister entityPersister = session.getEntityPersister(null, obj);
    final Serializable id = entityPersister.getIdentifier(obj, session);
    if (id != null)
    {
      return id;
    }
    
    return super.generate(session, obj);
  }
}
