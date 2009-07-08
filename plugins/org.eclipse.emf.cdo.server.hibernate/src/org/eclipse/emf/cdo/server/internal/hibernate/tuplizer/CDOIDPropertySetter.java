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
import org.eclipse.emf.cdo.server.hibernate.id.CDOIDHibernate;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateStoreAccessor;
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
    InternalCDORevision revision = (InternalCDORevision)target;
    if (value == null)
    {
      if (getEStructuralFeature().isUnsettable())
      {
        revision.unset(getEStructuralFeature());
      }
      return;
    }

    HibernateCommitContext commitContext = null;
    if (HibernateThreadContext.isCommitContextSet())
    {
      commitContext = HibernateThreadContext.getCommitContext();
    }

    final HibernateStoreAccessor storeAccessor = HibernateThreadContext.getCurrentStoreAccessor();
    final String entityName = storeAccessor.getStore().getEntityName(revision.getEClass());

    CDOID revisionID = revision.getID();
    if (revisionID == null)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value, entityName);
      revision.setID(newCDOID);
      if (commitContext != null)
      {
        commitContext.setNewID(revisionID, newCDOID);
      }
    }
    else if (revisionID instanceof CDOIDTemp)
    {
      CDOIDHibernate newCDOID = CDOIDHibernateFactoryImpl.getInstance().createCDOID((Serializable)value, entityName);
      revision.setID(newCDOID);
      if (commitContext != null)
      {
        commitContext.getCommitContext().addIDMapping((CDOIDTemp)revisionID, newCDOID);
        commitContext.setNewID(revisionID, newCDOID);
      }
    }
    else
    {
      CDOIDHibernate hbCDOID = (CDOIDHibernate)revision.getID();
      if (hbCDOID.getId() == null)
      {
        // TODO: how can this happen?
        hbCDOID.setId((Serializable)value);
      }
      else if (!hbCDOID.getId().equals(value))
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
