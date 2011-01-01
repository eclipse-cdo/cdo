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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDObject;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateCommitContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateThreadContext;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.hibernate.HibernateException;

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
    if (!(HibernateUtil.getInstance().getCDOID(revision) instanceof CDOIDObject))
    {
      if (!isVirtualProperty())
      {
        final Object value = super.get(target);
        // todo: value can be null for generated values?
        if (value != null)
        {
          setCDOID(revision, value);
        }

        return value;
      }

      return null;
    }

    if (isVirtualProperty())
    {
      return HibernateUtil.getInstance().getIdValue(revision.getID());
    }

    Object id = super.get(target);
    setCDOID(revision, id);

    // TODO: does this make sense?
    // if (cdoID.getId() == null)
    // {
    // cdoID.setId((Serializable)id);
    // }
    return id;
  }

  private void setCDOID(CDORevision target, Object value)
  {
    final InternalCDORevision revision = (InternalCDORevision)target;
    final CDOID cdoID = HibernateUtil.getInstance().getCDOID(revision);
    if (cdoID == null || cdoID instanceof CDOIDTemp)
    {
      final CDOID newCDOID = HibernateUtil.getInstance().createCDOID(new CDOClassifierRef(revision.getEClass()), value);
      revision.setID(newCDOID);
      if (HibernateThreadContext.isCommitContextSet())
      {
        final HibernateCommitContext commitContext = HibernateThreadContext.getCommitContext();
        commitContext.setNewID(cdoID, newCDOID);
        if (cdoID instanceof CDOIDTemp)
        {
          commitContext.getCommitContext().addIDMapping(cdoID, newCDOID);
        }
      }
    }
  }

  @Override
  protected boolean isVirtualPropertyAllowed()
  {
    return true;
  }
}
