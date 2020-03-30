/**
 * <copyright>
 *
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: NotationEObjectImpl.java,v 1.3 2010/03/06 00:04:14 aboyko Exp $
 */
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;

/**
 * An implementation of the model object '<em><b>EObject</b></em>'.
 * This implementation flattens the fields for storing
 * the {@link #eProxyURI}, the {@link #eContents}, and the {@link #eCrossReferences},
 * which in {@link EObjectImpl} are stored in the properties holder.
 * This reduces the likelihood of needing to allocate a properties holder and speeds up the access to these fields.
 * @deprecated use {@link org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container}
 */
@Deprecated
public class NotationEObjectImpl extends EObjectImpl
{
  protected URI eProxyURI;

  /**
   * Creates an EObject that is faster and more space efficient.
   */
  protected NotationEObjectImpl()
  {
    super();
  }

  @Override
  protected EPropertiesHolder eProperties()
  {
    if (eProperties == null)
    {
      eProperties = new EPropertiesHolderBaseImpl()
      {
      };
    }
    return eProperties;
  }

  @Override
  public boolean eIsProxy()
  {
    return eProxyURI != null;
  }

  @Override
  public URI eProxyURI()
  {
    return eProxyURI;
  }

  @Override
  public void eSetProxyURI(URI uri)
  {
    eProxyURI = uri;
  }

  @Override
  public EList eContents()
  {
    return EContentsEList.createEContentsEList(this);
  }

  @Override
  public EList eCrossReferences()
  {
    return ECrossReferenceEList.createECrossReferenceEList(this);
  }
}
