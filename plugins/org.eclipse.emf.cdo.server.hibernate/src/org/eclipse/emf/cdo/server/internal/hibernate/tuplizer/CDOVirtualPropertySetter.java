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
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.Setter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Gets resourceId, containedId and containingFeatureId
 * 
 * @author Martin Taal
 */
public class CDOVirtualPropertySetter implements Setter
{
  private static final long serialVersionUID = 1L;
  private final boolean isResourceIdProperty;
  private final boolean isContainerIdProperty;
  private final boolean isContainingFeatureIdProperty;
  private final String propertyName;
  
  public CDOVirtualPropertySetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    isResourceIdProperty = propertyName.compareTo("resourceID") == 0; 
    isContainerIdProperty = propertyName.compareTo("containerID") == 0; 
    isContainingFeatureIdProperty = propertyName.compareTo("containingFeatureID") == 0;
    this.propertyName = propertyName;
    if (!isResourceIdProperty && !isContainerIdProperty && !isContainingFeatureIdProperty) {
      throw new IllegalArgumentException("Propertyname " + propertyName + " not supported must be one of resourceID, containerID, containingFeatureID");
    }
  }

  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    if (isResourceIdProperty) {
      revision.setResourceID((CDOID)value);
      return;
    } else if (isContainerIdProperty) {
      revision.setContainerID((CDOID)value);
      return;
    } else if (isContainingFeatureIdProperty) {
      revision.setContainingFeatureID((Integer)value);
      return;
    }
    throw new IllegalArgumentException("Propertyname " + propertyName + " not supported must be one of resourceID, containerID, containingFeatureID");
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }
}
