/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - copied from CDORevisionPropertyHandler and adapted
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.SessionFactoryImplementor;

import java.util.List;

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
    // Do some checking
    if (value instanceof HibernateMoveableListWrapper)
    {
      super.set(target, ((HibernateMoveableListWrapper)value).getDelegate(), factory);
      return;
    }

    if (!(value instanceof PersistentCollection))
    {
      throw new IllegalArgumentException("Value is not a PersistentCollection but a " + value.getClass().getName()); //$NON-NLS-1$
    }

    if (!(value instanceof List<?>))
    {
      throw new IllegalArgumentException("Value is not a list but a " + value.getClass().getName()); //$NON-NLS-1$
    }

    // Only set it in the listholder
    PersistableListHolder.getInstance().putListMapping(target, getEStructuralFeature(), (PersistentCollection)value);

    // check if deep inside the persistentlist there is not already a delegate which is a hibernatemoveable list
    // which contains the list which should really be set in the cdorevision
    // persistentlist, hibernatemoveablelistwrapper, real list, if so then the real list should be set
    final InternalCDORevision revision = (InternalCDORevision)target;
    final Object currentValue = revision.getValue(getEStructuralFeature());
    if (currentValue == null)
    {
      @SuppressWarnings("unchecked")
      List<Object> valueList = (List<Object>)value;

      final WrappedHibernateList whl = new WrappedHibernateList();
      whl.setDelegate(valueList);
      super.set(target, whl, factory);
    }
  }
}
