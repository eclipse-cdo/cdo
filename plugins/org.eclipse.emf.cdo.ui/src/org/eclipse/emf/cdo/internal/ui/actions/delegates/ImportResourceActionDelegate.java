/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.dialogs.ImportResourceDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
@Deprecated
public class ImportResourceActionDelegate extends NewResourceActionDelegate
{
  private URI sourceURI;

  @Deprecated
  public ImportResourceActionDelegate()
  {
  }

  @Deprecated
  @Override
  protected final CDOObject preRun(CDOObject object)
  {
    ImportResourceDialog dialog = new ImportResourceDialog(getShell(), Messages.getString("ImportResourceToFolderAction_0"), SWT.OPEN); //$NON-NLS-1$
    if (dialog.open() == ImportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        sourceURI = uris.get(0);
        setNewResourceNode(createNewResourceNode());
        getNewResourceNode().setName(dialog.getTargetPath());

        CDOTransaction transaction = object.cdoView().getSession().openTransaction();
        new CDOTransactionCommentator(transaction);

        CDOObject transactionalObject = transaction.getObject(object);
        return transactionalObject;
      }

      MessageDialog.openError(getShell(), Messages.getString("ImportResourceActionDelegate.0"), //$NON-NLS-1$
          Messages.getString("ImportResourceToFolderAction_1")); //$NON-NLS-1$
      cancel();
    }
    else
    {
      cancel();
    }

    return null;
  }

  @Deprecated
  @Override
  protected CDOResourceNode createNewResourceNode()
  {
    CDOResource resource = (CDOResource)super.createNewResourceNode();

    // Source ResourceSet
    ResourceSet sourceSet = new ResourceSetImpl();
    // sourceSet.setPackageRegistry(transaction.getSession().getPackageRegistry());

    // Source Resource
    Resource source = sourceSet.getResource(sourceURI, true);
    List<EObject> sourceContents = new ArrayList<>(source.getContents());

    // Target Resource
    EList<EObject> targetContents = resource.getContents();

    // Move contents over
    for (EObject root : sourceContents)
    {
      targetContents.add(root);
    }

    return resource;
  }
}
