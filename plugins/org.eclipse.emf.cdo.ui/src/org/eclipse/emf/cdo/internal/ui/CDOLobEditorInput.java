/*
 * Copyright (c) 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;

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

  public CDOLobEditorInput(CDOResourceLeaf resource)
  {
    this(resource, false);
  }

  public CDOLobEditorInput(CDOResourceLeaf resource, boolean commitOnSave)
  {
    this.resource = resource;
    this.commitOnSave = commitOnSave;
  }

  public final CDOResourceLeaf getResource()
  {
    return resource;
  }

  public final boolean isCommitOnSave()
  {
    return commitOnSave;
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
    return resource.getName();
  }

  @Override
  public IPersistableElement getPersistable()
  {
    return null;
  }

  @Override
  public String getToolTipText()
  {
    return resource.getURI().toString();
  }
}
