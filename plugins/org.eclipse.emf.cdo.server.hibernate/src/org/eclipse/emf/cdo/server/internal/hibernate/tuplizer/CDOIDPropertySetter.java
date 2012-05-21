/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;

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
    final InternalCDORevision revision = (InternalCDORevision)target;
    if (value == null)
    {
      if (getEStructuralFeature().isUnsettable())
      {
        revision.unset(getEStructuralFeature());
      }

      return;
    }

    final CDOID revisionID = HibernateUtil.getInstance().getCDOID(revision);
    if (revisionID == null || revisionID instanceof CDOIDTemp)
    {
      final CDOID newCDOID = HibernateUtil.getInstance().createCDOID(new CDOClassifierRef(revision.getEClass()), value);
      revision.setID(newCDOID);
      if (HibernateThreadContext.isCommitContextSet())
      {
        final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();
        commitContext.setNewID(revisionID, newCDOID);
        if (revisionID instanceof CDOIDTemp)
        {
          commitContext.getCommitContext().addIDMapping(revisionID, newCDOID);
        }
      }
    }
    else
    {
      final Serializable idValue = HibernateUtil.getInstance().getIdValue(revisionID);
      if (idValue == null)
      {
        throw new IllegalStateException("ID value is null for revision " + revision); //$NON-NLS-1$
      }

      if (!idValue.equals(value))
      {
        throw new IllegalStateException("Current id and new id are different " + value + "/" + idValue); //$NON-NLS-1$ //$NON-NLS-2$
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
