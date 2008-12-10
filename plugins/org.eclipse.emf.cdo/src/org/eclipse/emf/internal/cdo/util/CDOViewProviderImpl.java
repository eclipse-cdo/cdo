/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.util.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.common.util.URI;

/**
 * Emulates the same behaviour as before the CDOViewProvider concept was introduced. Will provide a view from an already
 * populated <code>CDOViewSet</code>
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public class CDOViewProviderImpl extends AbstractCDOViewProvider
{
  public CDOViewProviderImpl()
  {
    super("cdo:.*");
  }

  public CDOView getView(URI uri, CDOViewSet viewSet)
  {
    if (viewSet == null)
    {
      throw new IllegalArgumentException("viewSet == null");
    }
    if (uri == null)
    {
      throw new IllegalArgumentException("uri == null");
    }

    String repositoryUUID = CDOURIUtil.extractRepositoryUUID(uri);
    return viewSet.resolveView(repositoryUUID);
  }
}
