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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Gets resourceId, containedId and containingFeatureId
 * 
 * @author Martin Taal
 */
public class CDOVirtualPropertyGetter implements Getter
{
  private static final long serialVersionUID = 1L;
  private final boolean isResourceIdProperty;
  private final boolean isContainerIdProperty;
  private final boolean isContainingFeatureIdProperty;
  private final String propertyName;
  
  public CDOVirtualPropertyGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    isResourceIdProperty = propertyName.compareTo("resourceID") == 0; 
    isContainerIdProperty = propertyName.compareTo("containerID") == 0; 
    isContainingFeatureIdProperty = propertyName.compareTo("containingFeatureID") == 0;
    this.propertyName = propertyName;
    if (!isResourceIdProperty && !isContainerIdProperty && !isContainingFeatureIdProperty) {
      throw new IllegalArgumentException("Propertyname " + propertyName + " not supported must be one of resourceID, containerID, containingFeatureID");
    }
  }

  public Object get(Object target) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    if (isResourceIdProperty) {
      final CDOID cdoID = revision.getResourceID();
      if (cdoID instanceof CDOIDTemp) {
        // TODO: externalize entityname
        final CDOIDHibernate hibernateID = CDOIDHibernateFactoryImpl.getInstance().createCDOID(((CDOIDTemp)cdoID).getIntValue(), "CDOResource");
        revision.setResourceID(hibernateID);
        if (HibernateThreadContext.isHibernateCommitContextSet()) {
          HibernateThreadContext.getHibernateCommitContext().setNewID(cdoID, hibernateID);
        }
      }
      
      return revision.getResourceID();
    } else if (isContainerIdProperty) {
      return revision.getContainerID();
    } else if (isContainingFeatureIdProperty) {
      return revision.getContainingFeatureID();
    }
    throw new IllegalArgumentException("Propertyname " + propertyName + " not supported must be one of resourceID, containerID, containingFeatureID");
  }

  @SuppressWarnings("unchecked")
  public Object getForInsert(Object target, Map mergeMap, SessionImplementor session) throws HibernateException
  {
    return get(target);
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }

  @SuppressWarnings("unchecked")
  public Class getReturnType()
  {
    return Object.class;
  }
}
