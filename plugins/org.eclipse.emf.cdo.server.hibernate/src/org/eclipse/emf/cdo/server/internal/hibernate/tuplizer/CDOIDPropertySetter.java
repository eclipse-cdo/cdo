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
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;

import java.io.Serializable;

/**
 * @author Martin Taal
 */
public class CDOIDPropertySetter extends CDOPropertySetter
{
  private static final long serialVersionUID = 1L;

  public CDOIDPropertySetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public void set(Object target, Object value, SessionFactoryImplementor factory) throws HibernateException
  {
    if (value == null)
    {
      return;
    }

    HibernateCommitContext hcc = null;
    if (HibernateThreadContext.isHibernateCommitContextSet())
    {
      hcc = HibernateThreadContext.getHibernateCommitContext();
    }
    InternalCDORevision revision = (InternalCDORevision)target;
    CDOID cdoID = revision.getID();
    if (cdoID == null)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getCDOClass().getName());
      revision.setID(newCDOID);
      if (hcc != null)
      {
        hcc.setNewID(cdoID, newCDOID);
      }
    }
    else if (cdoID instanceof CDOIDTemp)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getCDOClass().getName());
      revision.setID(newCDOID);
      if (hcc != null)
      {
        hcc.getCommitContext().addIDMapping((CDOIDTemp)cdoID, newCDOID);
        hcc.setNewID(cdoID, newCDOID);
      }
    }
    else
    {
      CDOIDHibernate hbCDOID = (CDOIDHibernate)revision.getID();
      if (!hbCDOID.getId().equals(value))
      {
        throw new IllegalStateException("Current id and new id are different " + value + "/" + hbCDOID.getId());
      }

    }
    if (!isVirtualProperty())
    {
      super.set(target, value, factory);
    }
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
