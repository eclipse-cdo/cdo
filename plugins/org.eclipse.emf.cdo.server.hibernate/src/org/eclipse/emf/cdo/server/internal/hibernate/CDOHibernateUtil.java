/***************************************************************************
 * Copyright (c) 2004 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.id.CDOIDTemp;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStoreWriter.CommitContext;
import org.eclipse.emf.cdo.server.hibernate.CDOIDHibernate;

import org.hibernate.Session;

/**
 * @author Martin Taal
 */
public class CDOHibernateUtil
{
  private static CDOHibernateUtil instance = new CDOHibernateUtil();

  /**
   * @return the instance
   */
  public static CDOHibernateUtil getInstance()
  {
    return instance;
  }

  /**
   * @param instance
   *          the instance to set
   */
  public static void setInstance(CDOHibernateUtil instance)
  {
    CDOHibernateUtil.instance = instance;
  }

  public String getEntityName(CDORevision cdoRevision)
  {
    return cdoRevision.getCDOClass().getName();
  }

  /**
   * Translates a temporary cdoID into a hibernate ID, by finding the object it refers to in the CommitContext and then
   * returning or by persisting the object. Note assumes that the hibernate session and CommitContext are set in
   * CDOHibernateThreadContext.
   */
  public CDOIDHibernate getCDOIDHibernate(CDOID cdoID)
  {
    final CDORevision cdoRevision = getCDORevision(cdoID);
    if (cdoRevision.getID() instanceof CDOIDHibernate)
    {
      return (CDOIDHibernate)cdoRevision.getID();
    }
    final Session session = CDOHibernateThreadContext.getSession();
    session.saveOrUpdate(cdoRevision);
    if (!(cdoRevision.getID() instanceof CDOIDHibernate))
    {
      throw new IllegalStateException("CDORevision " + cdoRevision.getCDOClass().getName() + " " + cdoRevision.getID()
          + " does not have a hibernate cdoid after saving/updating it");
    }
    return (CDOIDHibernate)cdoRevision.getID();
  }

  /**
   * Gets a current object, first checks the new and dirty objects from the commitcontent. Otherwise reads it from the
   * session.
   */
  public CDORevision getCDORevision(CDOID id)
  {
    final CommitContext commitContext = CDOHibernateThreadContext.getCommitContext();
    for (CDORevision revision : commitContext.getNewObjects())
    {
      if (revision.getID().equals(id))
      {
        return revision;
      }
    }
    for (CDORevision revision : commitContext.getDirtyObjects())
    {
      if (revision.getID().equals(id))
      {
        return revision;
      }
    }

    // maybe the temp was already translated
    if (id instanceof CDOIDTemp)
    {
      final CDOID newID = commitContext.getIDMappings().get(id);
      if (newID != null)
      {
        return getCDORevision(newID);
      }
    }

    if (!(id instanceof CDOIDHibernate))
    {
      throw new IllegalArgumentException("Passed cdoid is not an instance of CDOIDHibernate but a "
          + id.getClass().getName() + ": " + id);
    }
    final CDOIDHibernate cdoIDHibernate = (CDOIDHibernate)id;
    final Session session = CDOHibernateThreadContext.getSession();
    return (CDORevision)session.get(cdoIDHibernate.getEntityName(), cdoIDHibernate.getId());
  }
}
