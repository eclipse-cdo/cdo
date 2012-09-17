/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * @author Victor Roldan Betancort
 */
public class ResourceNodeNameInputValidator implements IInputValidator
{
  private CDOResourceNode node;

  private boolean isFolder;

  public ResourceNodeNameInputValidator(CDOResourceNode node)
  {
    this.node = node;
    isFolder = node instanceof CDOResourceFolder;
  }

  public String isValid(String newText)
  {
    // Do not allow empty names
    if (StringUtil.isEmpty(newText))
    {
      return isFolder ? Messages.getString("CreateResourceNodeAction.3") : Messages.getString("CreateResourceNodeAction.4"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // Do not allow "/"
    if (newText.contains("/") || newText.contains("\\")) //$NON-NLS-1$ //$NON-NLS-2$
    {
      return Messages.getString("CreateResourceNodeAction.2"); //$NON-NLS-1$
    }

    for (EObject nodeObject : node.eContents())
    {
      CDOResourceNode node = (CDOResourceNode)nodeObject;
      if (node.getName().equals(newText))
      {
        return isFolder ? Messages.getString("CreateResourceNodeAction.5") + " " + newText : Messages.getString("CreateResourceNodeAction.6") //$NON-NLS-1$ //$NON-NLS-2$
                + " " + newText;
      }
    }

    return null;
  }
}
