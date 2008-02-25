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

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;
import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;
import org.eclipse.emf.cdo.server.hibernate.CDOIDHibernate;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.id.CDOIDHibernateImpl;

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
    final InternalCDORevision revision = (InternalCDORevision)target;
    final CDOID cdoID = revision.getID();
    if (cdoID == null)
    {
      final CDOIDHibernate newCDOID = new CDOIDHibernateImpl();
      newCDOID.setId((Serializable)value);
      newCDOID.setEntityName(revision.getCDOClass().getName());
      revision.setID(newCDOID);
    }
    else if (cdoID instanceof CDOIDTemp)
    {
      final CommitContext commitContext = HibernateThreadContext.getCommitContext();
      final CDOIDHibernate newCDOID = new CDOIDHibernateImpl();
      newCDOID.setId((Serializable)value);
      newCDOID.setEntityName(revision.getCDOClass().getName());
      revision.setID(newCDOID);
      commitContext.addIDMapping((CDOIDTemp)cdoID, newCDOID);
    }
    else
    {
      final CDOIDHibernate hbCDOID = (CDOIDHibernate)revision.getID();
      if (!hbCDOID.getId().equals(value))
      {
        throw new IllegalStateException("Current id and new id are different " + value + "/" + hbCDOID.getId());
      }
    }
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
