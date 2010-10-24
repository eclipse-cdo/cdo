/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace.internal.efs;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.internal.efs.CDOWorkspaceStore.SaveContext;
import org.eclipse.emf.cdo.workspace.internal.efs.bundle.OM;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class CDOResourceNodeStore extends AbstractResourceNodeStore
{
  private CDOWorkspaceStore workspaceStore;

  private AbstractResourceNodeStore parent;

  private String name;

  public CDOResourceNodeStore(CDOWorkspaceStore workspaceStore, AbstractResourceNodeStore parent, String name)
  {
    this.workspaceStore = workspaceStore;
    this.parent = parent;
    this.name = name;
  }

  @Override
  public AbstractResourceNodeStore getParent()
  {
    return parent;
  }

  @Override
  public String getName()
  {
    return name;
  }

  public String getPath()
  {
    if (parent instanceof CDOResourceNodeStore)
    {
      return ((CDOResourceNodeStore)parent).getPath() + "/" + name;
    }

    return name;
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ResourceNodeRunnable<InputStream>()
    {
      @Override
      protected InputStream run(CDOResourceNode node)
      {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CDOResource resource = (CDOResource)node;

        try
        {
          resource.save(baos, null);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
          throw WrappedException.wrap(ex);
        }

        return new ByteArrayInputStream(baos.toByteArray());
      }
    }.run();
  }

  @Override
  public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    return new ByteArrayOutputStream()
    {
      @Override
      public void close() throws IOException
      {
        byte[] bytes = toByteArray();
        InputStream in = new ByteArrayInputStream(bytes);

        XMIResource xmiResource = new XMIResourceImpl();
        xmiResource.load(in, null);

        EList<EObject> contents = xmiResource.getContents();
        SaveContext saveContext = getWorkspaceStore().getSaveContext();
        handleSavedObjects(contents, xmiResource, saveContext);

        saveContext.save();
      }
    };
  }

  protected void handleSavedObjects(EList<EObject> contents, XMIResource xmiResource, SaveContext saveContext)
  {
    for (EObject eObject : contents)
    {
      handleSavedObject((InternalEObject)eObject, xmiResource, saveContext);
      handleSavedObjects(eObject.eContents(), xmiResource, saveContext);
    }
  }

  protected void handleSavedObject(InternalEObject eObject, XMIResource xmiResource, SaveContext saveContext)
  {
    InternalCDOObject cdoObject = getCDOObject(eObject, xmiResource, saveContext, true);

    CDOClassInfo classInfo = cdoObject.cdoRevision().getClassInfo();
    for (EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {
      boolean reference = feature instanceof EReference;
      Object value = eObject.eGet(feature);
      if (feature.isMany())
      {
        @SuppressWarnings("unchecked")
        List<Object> source = (List<Object>)value;

        @SuppressWarnings("unchecked")
        List<Object> target = (List<Object>)cdoObject.eGet(feature);
        target.clear();

        for (Object element : source)
        {
          if (reference)
          {
            element = getCDOObject((InternalEObject)element, xmiResource, saveContext, false);
          }

          target.add(element);
        }
      }
      else
      {
        if (reference)
        {
          value = getCDOObject((InternalEObject)value, xmiResource, saveContext, false);
        }

        cdoObject.eSet(feature, value);
      }
    }
  }

  private InternalCDOObject getCDOObject(InternalEObject eObject, XMIResource xmiResource, SaveContext saveContext,
      boolean createOnDemand)
  {
    if (eObject == null)
    {
      return null;
    }

    String fragment = xmiResource.getID(eObject);
    InternalCDOObject cdoObject = null;

    try
    {
      CDOID id = CDOIDUtil.read(fragment);
      cdoObject = (InternalCDOObject)saveContext.getTransaction().getObject(id);
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    if (cdoObject == null)
    {
      cdoObject = saveContext.getNewObject(fragment);
    }

    if (cdoObject == null)
    {
      if (createOnDemand)
      {
        EObject eContainer = eObject.eContainer();
        if (eContainer == null)
        {
          // TODO: implement CDOResourceNodeStore.getCDOObject(eObject, xmiResource, saveContext, createOnDemand)
          throw new UnsupportedOperationException();
        }

        InternalCDOObject cdoContainer = getCDOObject((InternalEObject)eContainer, xmiResource, saveContext, false);
        EObject newInstance = EcoreUtil.create(eObject.eClass());

        EReference containmentFeature = eObject.eContainmentFeature();
        if (containmentFeature.isMany())
        {
          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>)cdoContainer.eGet(containmentFeature);
          list.add(newInstance);
        }
        else
        {
          cdoContainer.eSet(containmentFeature, newInstance);
        }

        cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(newInstance);
        saveContext.registerNewObject(fragment, cdoObject);
      }
      else
      {
        // saveContext.registerForwardReference(fragment, cdoObject);

        // TODO: implement CDOResourceNodeStore.getCDOObject(eObject, xmiResource, saveContext, createOnDemand)
        throw new UnsupportedOperationException();
      }
    }

    return cdoObject;
  }

  @Override
  public void delete(int options, IProgressMonitor monitor) throws CoreException
  {
    // Options can only contain EFS.NONE
    new ResourceNodeRunnable<Boolean>()
    {
      @Override
      protected Boolean run(CDOResourceNode node)
      {
        try
        {
          node.delete(null);
          return true;
        }
        catch (IOException ex)
        {
          throw WrappedException.wrap(ex);
        }
      }
    }.run(true);
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    // TODO Respect the SHALLOW option
    new ResourceNodeRunnable<CDOResourceFolder>()
    {
      @Override
      protected CDOResourceFolder run(CDOView view)
      {
        return ((CDOTransaction)view).createResourceFolder(getPath());
      }
    }.run(true);

    return this;
  }

  @Override
  public CDOWorkspaceStore getWorkspaceStore()
  {
    return workspaceStore;
  }

  @Override
  protected CDOResourceNode getResourceNode(CDOView view)
  {
    return view.getResourceNode(getPath());
  }

  @Override
  protected boolean isDirectory(CDOResourceNode node)
  {
    return node instanceof CDOResourceFolder;
  }

  @Override
  protected void collectChildNames(CDOResourceNode node, List<String> childNames)
  {
    if (node instanceof CDOResourceFolder)
    {
      CDOResourceFolder folder = (CDOResourceFolder)node;

      for (CDOResourceNode child : folder.getNodes())
      {
        childNames.add(child.getName());
      }
    }
  }
}
