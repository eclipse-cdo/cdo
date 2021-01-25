/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.ui.internal.compare.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.io.EncodingProvider;

import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.legacy.IStreamContentAccessor;
import org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.legacy.ITypedElement;
import org.eclipse.emf.compare.utils.ReferenceUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link ITypedElement typed element} that can be used as input for TextMergeViewer. The returned {@link #getContents() content}
 * is the value of the given {@link EAttribute attribute} of the given {@link EObject object}.
 *
 * @author Eike Stepper
 */
public class CDOLobAttributeChangeAccessor implements ITypedElement, IStreamContentAccessor
{
  private final EObject eObject;

  private final EAttribute eAttribute;

  public CDOLobAttributeChangeAccessor(EObject eObject, AttributeChange attributeChange)
  {
    this.eObject = eObject;
    eAttribute = attributeChange.getAttribute();
  }

  @Override
  public String getName()
  {
    return this.getClass().getName();
  }

  @Override
  public Image getImage()
  {
    return ExtendedImageRegistry.getInstance().getImage(EcoreEditPlugin.getPlugin().getImage("full/obj16/EAttribute")); //$NON-NLS-1$
  }

  @Override
  @SuppressWarnings("restriction")
  public String getType()
  {
    return org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.TypeConstants.TYPE_ETEXT_DIFF;
  }

  @Override
  public InputStream getContents() throws CoreException
  {
    CDOLob<?> lob = (CDOLob<?>)ReferenceUtil.safeEGet(eObject, eAttribute);
    String encoding = eObject instanceof EncodingProvider ? ((EncodingProvider)eObject).getEncoding() : null;

    try
    {
      return CDOUtil.openInputStream(lob, encoding);
    }
    catch (IOException ex)
    {
      OM.BUNDLE.coreException(ex);
      return null; // Can't happen.
    }
  }
}
