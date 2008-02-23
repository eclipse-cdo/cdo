/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentList;
import org.hibernate.engine.SessionFactoryImplementor;

/**
 * @author Martin Taal
 */
public class CDOManyReferenceSetter extends CDOPropertySetter
{
  private static final long serialVersionUID = 1L;

  public CDOManyReferenceSetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    // do some checking
    if (!(value instanceof PersistentList))
    {
      throw new IllegalArgumentException("Value is not a persistentlist but a " + value.getClass().getName());
    }

    // only set it in the listholder
    PersistableListHolder.getInstance().putListMapping(target, getCDOFeature(), (PersistentCollection)value);
  }
}
