/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * A text editor will consult {@link CDOLobStorage} for this input.
 *
 * @author Eike Stepper
 */
public class CDOLobEditorInput extends PlatformObject implements IEditorInput
{
  private final CDOResourceLeaf resource;

  private final boolean commitOnSave;

  private final String name;

  private URI uri;

  public CDOLobEditorInput(CDOResourceLeaf resource)
  {
    this(resource, false);
  }

  public CDOLobEditorInput(CDOResourceLeaf resource, boolean commitOnSave)
  {
    this.resource = resource;
    this.commitOnSave = commitOnSave;
    name = resource.getName();
  }

  public final CDOResourceLeaf getResource()
  {
    return resource;
  }

  public final boolean isCommitOnSave()
  {
    return commitOnSave;
  }

  public final URI getURI()
  {
    if (uri != null)
    {
      return uri;
    }

    return resource.getURI();
  }

  public final void setURI(URI uri)
  {
    this.uri = uri;
  }

  @Override
  public boolean exists()
  {
    return true;
  }

  @Override
  public ImageDescriptor getImageDescriptor()
  {
    return null;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public IPersistableElement getPersistable()
  {
    return null;
  }

  @Override
  public String getToolTipText()
  {
    return getURI().toString();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getAdapter(Class<T> type)
  {
    T adapter = super.getAdapter(type);

    if (adapter == null && type == URI.class)
    {
      adapter = (T)getURI();
    }

    return adapter;
  }
}
