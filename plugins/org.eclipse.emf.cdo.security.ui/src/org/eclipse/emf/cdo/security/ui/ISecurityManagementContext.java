/*
 * Copyright (c) 2013, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.ui;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IAdaptable;

/**
 * An optional security-management context that may be provided as an {@linkplain IAdaptable adapter}
 * by the view part to which the "Manage Security" command is contributed.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public interface ISecurityManagementContext
{
  public static final ISecurityManagementContext DEFAULT = new Default();

  /**
   * Obtains a view in which to open the security resource for editing.  If at all possible, this
   * should be a writable {@linkplain CDOTransaction transaction}.  If necessary, implementors are
   * welcome to open a new session logged in as the Administrator for this purpose.
   *
   * @see #getSecurityResource(CDOView)
   * @see #disconnect(CDOView)
   */
  public CDOView connect(CDOSession session);

  /**
   * Releases a {@code view} previously {@linkplain #connect(CDOSession) obtained} from this context.
   * The caller must not attempt to use the {@code view} after this point because in all likelihood
   * it will be closed.
   *
   * @see #connect(CDOSession)
   */
  public void disconnect(CDOView view);

  /**
   * Obtains the resource containing the security model for presentation in the Security Management
   * editor.
   */
  public CDOResource getSecurityResource(CDOView view);

  /**
   * A default implementation of a {@link ISecurityManagementContext security management context}.
   *
   * @author Christian W. Damus (CEA LIST)
   */
  public static class Default implements ISecurityManagementContext
  {
    @Override
    public CDOView connect(CDOSession session)
    {
      if (session.isClosed())
      {
        return null;
      }

      if (User.ADMINISTRATOR.equals(session.getUserID()))
      {
        return session.openTransaction();
      }

      return session.openView();
    }

    @Override
    public void disconnect(CDOView view)
    {
      view.close();
    }

    @Override
    public CDOResource getSecurityResource(CDOView view)
    {
      CDOResource result = null;

      try
      {
        result = view.getResource("/security"); //$NON-NLS-1$
      }
      catch (Exception e)
      {
        // OK, so it's not in the default location. Work a little harder to find it
        CloseableIterator<EObject> realms = null;
        try
        {
          realms = view.queryInstancesAsync(SecurityPackage.Literals.REALM);
          if (realms.hasNext())
          {
            result = (CDOResource)realms.next().eResource();
          }
        }
        catch (Exception e2)
        {
          OM.LOG.error(e2);
        }
        finally
        {
          IOUtil.closeSilent(realms);
        }
      }

      if (result == null)
      {
        OM.LOG.warn("Security model resource not available."); //$NON-NLS-1$
      }

      return result;
    }
  }
}
