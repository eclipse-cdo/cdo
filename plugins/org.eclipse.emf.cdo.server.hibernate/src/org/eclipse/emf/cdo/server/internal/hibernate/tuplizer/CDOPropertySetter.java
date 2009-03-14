/**
 * Copyright (c) 2004 - 2009 Martin Taal and others.
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

import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDORevisionPropertyAccessor.CDORevisionSetter;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.property.Setter;

import java.lang.reflect.Method;

/**
 * TODO How does this differ from {@link CDORevisionSetter}? Both needed?
 * 
 * @author Martin Taal
 */
public class CDOPropertySetter extends CDOPropertyHandler implements Setter
{
  private static final long serialVersionUID = 1L;

  public CDOPropertySetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  public Method getMethod()
  {
    return null;
  }

  public String getMethodName()
  {
    return null;
  }

  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    revision.setValue(getEStructuralFeature(), value);
  }
}
