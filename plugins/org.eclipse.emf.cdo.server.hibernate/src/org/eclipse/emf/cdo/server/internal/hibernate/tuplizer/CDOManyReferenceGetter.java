/*
 * Copyright (c) 2008-2013, 2015 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.type.CollectionType;

/**
 * @author Martin Taal
 */
// Howto handle hibernate lists:
// - a new owner: the owner is persisted and its lists are replaced with hibernate
// persistentlist, the hibernate persitentlist will have a delegate (internally) which is the list which was previously
// present in the owner.
// - an existing owner: the owner is read from the db and hibernate will set a persistentlist
// directly
//
// The solution also needs to handle the following:
// - cdo does not have direct java references but stores cdoids in the list while hibernate expects real java object
// references.
// - cdo uses a moveablearraylist and not the standard arraylist
//
// The solution:
// - never return null when hibernate asks for the current value of the manyreference, always
// return a MoveableArrayList so that hibernate uses that as the delegate, set the MoveableArrayList
public class CDOManyReferenceGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOManyReferenceGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    // Check if there is already a persistentcollection
    PersistentCollection collection = PersistableListHolder.getInstance().getListMapping(target, getEStructuralFeature());
    if (collection != null)
    {
      return collection;
    }

    InternalCDORevision revision = (InternalCDORevision)target;
    CDOList list = revision.getList(getEStructuralFeature(), 10);

    if (list instanceof WrappedHibernateList)
    {
      final WrappedHibernateList wrappedHibernateList = (WrappedHibernateList)list;
      if (wrappedHibernateList.isUninitializedCollection())
      {
        return CollectionType.UNFETCHED_COLLECTION;
      }
      final Object delegate = wrappedHibernateList.getDelegate();
      if (delegate instanceof PersistentCollection)
      {
        return delegate;
      }
    }

    // Wrap the moveablearraylist
    HibernateMoveableListWrapper wrapper = new HibernateMoveableListWrapper();
    wrapper.setEFeature(getEStructuralFeature());
    wrapper.setDelegate(list);

    // And return it
    return wrapper;
  }
}
