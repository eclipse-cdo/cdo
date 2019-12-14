/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.dialogs.ImportResourceDialog;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionCommentator;

import org.eclipse.net4j.util.io.IORuntimeException;

import org.eclipse.emf.common.ui.dialogs.ResourceDialog;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Victor Roldan Betancort
 */
@Deprecated
public class ExportResourceActionDelegate extends TransactionalBackgroundActionDelegate
{
  private URI targetURI;

  public ExportResourceActionDelegate()
  {
    super(Messages.getString("ExportSelectedResourceAction_0")); //$NON-NLS-1$
  }

  @Override
  protected final CDOObject preRun(CDOObject object)
  {
    ResourceDialog dialog = new ResourceDialog(getShell(), Messages.getString("ExportSelectedResourceAction_1"), //$NON-NLS-1$
        SWT.SAVE);
    if (dialog.open() == ImportResourceDialog.OK)
    {
      List<URI> uris = dialog.getURIs();
      if (uris.size() == 1)
      {
        targetURI = uris.get(0);
        CDOTransaction transaction = object.cdoView().getSession().openTransaction();
        new CDOTransactionCommentator(transaction);

        CDOObject transactionalObject = transaction.getObject(object);
        return transactionalObject;
      }

      MessageDialog.openError(getShell(), Messages.getString("ExportResourceActionDelegate.0"), //$NON-NLS-1$
          Messages.getString("ExportSelectedResourceAction_2")); //$NON-NLS-1$
      cancel();
    }
    else
    {
      cancel();
    }

    return null;
  }

  @Override
  protected void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception
  {
    // Source Resource
    Resource source = object instanceof CDOResource ? (CDOResource)object : object.cdoResource();
    List<EObject> sourceContents = new ArrayList<>(source.getContents());
    exportObjects(sourceContents);
  }

  private void exportObjects(List<EObject> sourceContents)
  {
    // Target Resource
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    Resource resource = resourceSet.createResource(targetURI);

    Collection<EObject> copiedRoots = EcoreUtil.copyAll(sourceContents);
    resource.getContents().addAll(copiedRoots);

    try
    {
      resource.save(null);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }
}
