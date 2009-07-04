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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

import java.io.Serializable;

/**
 * @author Martin Taal
 */
public class CDOIDPropertyGetter extends CDOPropertyGetter
{
  private static final long serialVersionUID = 1L;

  public CDOIDPropertyGetter(CDORevisionTuplizer tuplizer, String propertyName)
  {
    super(tuplizer, propertyName);
  }

  @Override
  public Object get(Object target) throws HibernateException
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    if (!(revision.getID() instanceof CDOIDHibernate))
    {
      if (!isVirtualProperty())
      {
        final Object value = super.get(target);
        setCDOID(revision, value);
        return value;
      }

      return null;
    }

    CDOIDHibernate cdoID = (CDOIDHibernate)revision.getID();
    if (isVirtualProperty())
    {
      return cdoID.getId();
    }
    else
    {
      Object id = super.get(target);
      setCDOID(revision, id);

      // TODO: does this make sense?
      // if (cdoID.getId() == null)
      // {
      // cdoID.setId((Serializable)id);
      // }
      return id;
    }
  }

  private void setCDOID(CDORevision target, Object value)
  {
    InternalCDORevision revision = (InternalCDORevision)target;
    HibernateCommitContext hcc = null;
    if (HibernateThreadContext.isCommitContextSet())
    {
      hcc = HibernateThreadContext.getCommitContext();
    }

    CDOID cdoID = revision.getID();
    if (cdoID == null)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getEClass().getName());
      revision.setID(newCDOID);
      if (hcc != null)
      {
        hcc.setNewID(cdoID, newCDOID);
      }
    }
    else if (cdoID instanceof CDOIDTemp)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value,
          revision.getEClass().getName());
      revision.setID(newCDOID);
      if (hcc != null)
      {
        hcc.getCommitContext().addIDMapping((CDOIDTemp)cdoID, newCDOID);
        hcc.setNewID(cdoID, newCDOID);
      }
    }
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
