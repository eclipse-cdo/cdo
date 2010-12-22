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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;
import org.eclipse.emf.cdo.workspace.efs.CDOFS;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class CDOWorkspaceStore extends AbstractResourceNodeStore
{
  private static final AbstractResourceNodeStore NO_PARENT = null;

  private String name;

  private File location;

  private CDOWorkspace workspace;

  private Map<String, Long> lastModifiedTimes = new HashMap<String, Long>();

  private CDOView view;

  private SaveContext saveContext;

  public CDOWorkspaceStore(String name, File location)
  {
    this.name = name;
    this.location = location;
  }

  public File getLocation()
  {
    return location;
  }

  public synchronized CDOWorkspace getWorkspace()
  {
    if (workspace == null)
    {
      workspace = openWorkspace();
    }

    return workspace;
  }

  public synchronized void setWorkspace(CDOWorkspace workspace)
  {
    this.workspace = workspace;
  }

  private CDOWorkspace openWorkspace()
  {
    try
    {
      return CDOFS.open(name, location);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public AbstractResourceNodeStore getParent()
  {
    return NO_PARENT;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public URI toURI()
  {
    try
    {
      return new URI(CDOWorkspaceFileSystem.SCHEME + "://" + name);
    }
    catch (URISyntaxException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  @Override
  public IFileStore getChild(String name)
  {
    if (CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME.equals(name))
    {
      return new CDOProjectDescriptionStore(this);
    }

    return super.getChild(name);
  }

  @Override
  public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException
  {
    return this;
  }

  @Override
  public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException
  {
    throw new UnsupportedOperationException();
  }

  public void dispose()
  {
    if (view != null)
    {
      IOUtil.close(view);
      view = null;
    }
  }

  @Override
  public CDOWorkspaceStore getWorkspaceStore()
  {
    return this;
  }

  public long getLastModified(String path)
  {
    Long time = lastModifiedTimes.get(path);
    return time == null ? EFS.NONE : time;
  }

  public void setLastModified(String path, long time)
  {
    lastModifiedTimes.put(path, time);
    // TODO Save lastModifiedTimes
  }

  public SaveContext getSaveContext()
  {
    if (saveContext == null)
    {
      saveContext = new SaveContext();
    }

    return saveContext;
  }

  @Override
  public String getPath()
  {
    return "";
  }

  @Override
  protected synchronized CDOView getView()
  {
    if (view == null)
    {
      view = workspace.openView();
    }

    return view;
  }

  @Override
  protected CDOResourceNode getResourceNode(CDOView view)
  {
    return view.getRootResource();
  }

  @Override
  protected boolean isDirectory(CDOResourceNode node)
  {
    return true;
  }

  @Override
  protected void collectChildNames(CDOResourceNode node, List<String> childNames)
  {
    childNames.add(CDOProjectDescriptionStore.DESCRIPTION_FILE_NAME);

    CDOResource rootResource = (CDOResource)node;
    for (EObject content : rootResource.getContents())
    {
      if (content instanceof CDOResourceNode)
      {
        CDOResourceNode child = (CDOResourceNode)content;
        childNames.add(child.getName());
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class SaveContext
  {
    private CDOTransaction transaction = workspace.openTransaction();

    private Map<String, InternalCDOObject> newObjects = new HashMap<String, InternalCDOObject>();

    private Map<String, List<ForwardReference>> forwardReferences = new HashMap<String, List<ForwardReference>>();

    private XMIResource xmiResource;

    public SaveContext()
    {
    }

    public void save(XMIResource xmiResource, String cdoPath)
    {
      this.xmiResource = xmiResource;

      try
      {
        CDOResource cdoResource = transaction.getOrCreateResource(cdoPath);
        saveContents(xmiResource.getContents(), cdoResource.getContents());
      }
      finally
      {
        this.xmiResource = null;
        done();
      }
    }

    private void saveContents(EList<EObject> xmiContents, EList<EObject> cdoContents)
    {
      int size = xmiContents.size();
      for (int i = 0; i < size; i++)
      {
        EObject xmiObject = xmiContents.get(i);
        CDOObject cdoObject = getCDOObjectByXMIID(xmiObject);
        if (cdoObject == null)
        {
          cdoObject = createNewCDOObject(xmiObject);
          cdoContents.add(i, cdoObject);
        }
        else
        {
          int index = cdoContents.indexOf(cdoObject);
          if (index != -1)
          {
            if (index != i)
            {
              cdoContents.move(i, index);
            }
          }
          else
          {
            cdoContents.add(i, cdoObject);
          }
        }

        saveObject((InternalEObject)xmiObject, (InternalCDOObject)cdoObject);
      }

      shortenList(cdoContents, size);
    }

    private void saveObject(InternalEObject xmiObject, InternalCDOObject cdoObject)
    {
      // CDOClassInfo classInfo = cdoObject.cdoRevision().getClassInfo();
      for (EStructuralFeature feature : xmiObject.eClass().getEAllStructuralFeatures())
      {
        Object xmiValue = xmiObject.eGet(feature);
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment())
          {
            if (reference.isMany())
            {
              // Many-valued containment reference
              @SuppressWarnings("unchecked")
              EList<EObject> xmiContents = (EList<EObject>)xmiValue;

              @SuppressWarnings("unchecked")
              EList<EObject> cdoContents = (EList<EObject>)cdoObject.eGet(reference);

              saveContents(xmiContents, cdoContents);
            }
            else
            {
              // Single-valued containment reference
              if (xmiValue != null)
              {
                InternalCDOObject cdoValue = getCDOObjectByXMIID((EObject)xmiValue);
                if (cdoValue == null)
                {
                  cdoValue = createNewCDOObject((EObject)xmiValue);
                }

                cdoObject.eSet(reference, cdoValue);
                saveObject((InternalEObject)xmiValue, cdoValue);
              }
              else
              {
                cdoObject.eSet(reference, null);
              }
            }
          }
          else
          {
            if (reference.isMany())
            {
              // Many-valued cross reference
              @SuppressWarnings("unchecked")
              EList<EObject> xmiElements = (EList<EObject>)xmiValue;

              @SuppressWarnings("unchecked")
              EList<EObject> cdoElements = (EList<EObject>)cdoObject.eGet(reference);

              int size = xmiElements.size();
              for (int i = 0; i < size; i++)
              {
                InternalEObject xmiElement = (InternalEObject)xmiElements.get(i);
                InternalCDOObject cdoElement;

                org.eclipse.emf.common.util.URI eProxyURI = xmiElement.eProxyURI();
                if (eProxyURI != null)
                {
                  String href = eProxyURI.fragment();
                  cdoElement = getCDOObjectByHREF(href);
                  if (cdoElement == null)
                  {
                    registerForwardReference(cdoObject, reference, i, href);

                    InternalCDOObject dummy = createNewCDOObject(xmiElement);
                    cdoElements.add(i, dummy);
                    continue;
                  }
                }
                else
                {
                  cdoElement = getCDOObjectByXMIID(xmiElement);
                }

                int index = cdoElements.indexOf(cdoElement);
                if (index != -1)
                {
                  cdoElements.move(i, index);
                }
                else
                {
                  cdoElements.add(i, cdoElement);
                }
              }

              shortenList(cdoElements, size);
            }
            else
            {
              // Single-valued cross reference
              CDOObject cdoValue = null;
              if (xmiValue != null)
              {
                org.eclipse.emf.common.util.URI eProxyURI = ((InternalEObject)xmiValue).eProxyURI();
                if (eProxyURI != null)
                {
                  String href = eProxyURI.fragment();
                  cdoValue = getCDOObjectByHREF(href);
                  if (cdoValue == null)
                  {
                    registerForwardReference(cdoObject, reference, -1, href);
                  }
                }
                else
                {
                  cdoValue = getCDOObjectByXMIID((EObject)xmiValue);
                }
              }

              cdoObject.eSet(reference, cdoValue);
            }
          }
        }
        else
        {
          EAttribute attribute = (EAttribute)feature;
          if (attribute.isMany())
          {
            // Many-valued attribute
            @SuppressWarnings("unchecked")
            EList<Object> xmiElements = (EList<Object>)xmiValue;

            @SuppressWarnings("unchecked")
            EList<Object> cdoElements = (EList<Object>)cdoObject.eGet(attribute);
            cdoElements.clear();

            int size = xmiElements.size();
            for (int i = 0; i < size; i++)
            {
              Object xmiElement = xmiElements.get(i);
              cdoElements.add(xmiElement);
            }
          }
          else
          {
            // Single-valued attribute
            cdoObject.eSet(attribute, xmiValue);
          }
        }
      }
    }

    private InternalCDOObject getCDOObjectByXMIID(EObject xmiObject)
    {
      String xmiID = xmiResource.getID(xmiObject);
      if (xmiID != null)
      {
        try
        {
          CDOID id = CDOIDUtil.read(xmiID);
          if (!CDOIDUtil.isNull(id))
          {
            return (InternalCDOObject)transaction.getObject(id);
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          //$FALL-THROUGH$
        }
      }

      return null;
    }

    private InternalCDOObject createNewCDOObject(EObject xmiObject)
    {
      // Create new object
      EObject newInstance = EcoreUtil.create(xmiObject.eClass());
      InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(newInstance);

      // Remember new object
      String fragment = xmiResource.getURIFragment(xmiObject);
      newObjects.put(fragment, cdoObject);

      return cdoObject;
    }

    private void shortenList(EList<EObject> list, int size)
    {
      int remove = list.size() - size;
      while (remove-- != 0)
      {
        list.remove(list.size() - 1);
      }
    }

    private InternalCDOObject getCDOObjectByHREF(String href)
    {
      InternalCDOObject cdoObject = null;

      try
      {
        CDOID id = CDOIDUtil.read(href);
        cdoObject = (InternalCDOObject)transaction.getObject(id);
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }

      if (cdoObject == null)
      {
        cdoObject = newObjects.get(href);
      }

      return cdoObject;
    }

    private void done()
    {
      if (resolveForwardReferences())
      {
        commit();
      }
    }

    private void registerForwardReference(InternalCDOObject cdoObject, EReference reference, int index, String href)
    {
      List<ForwardReference> list = forwardReferences.get(href);
      if (list == null)
      {
        list = new ArrayList<ForwardReference>();
        forwardReferences.put(href, list);
      }

      list.add(new ForwardReference(cdoObject, reference, index));
    }

    private boolean resolveForwardReferences()
    {
      Set<Entry<String, List<ForwardReference>>> entrySet = forwardReferences.entrySet();
      for (Iterator<Entry<String, List<ForwardReference>>> it = entrySet.iterator(); it.hasNext();)
      {
        Entry<String, List<ForwardReference>> entry = it.next();
        String href = entry.getKey();

        InternalCDOObject target = getCDOObjectByHREF(href);
        if (target != null)
        {
          List<ForwardReference> list = entry.getValue();
          for (ForwardReference forwardReference : list)
          {
            forwardReference.resolve(target);
          }

          it.remove();
        }
      }

      return forwardReferences.isEmpty();
    }

    private void commit()
    {
      try
      {
        transaction.commit();
      }
      catch (CommitException ex)
      {
        throw WrappedException.wrap(ex);
      }
      finally
      {
        IOUtil.closeSilent(transaction);
        transaction = null;
        forwardReferences = null;
        newObjects = null;
        saveContext = null;
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ForwardReference
    {
      private InternalCDOObject source;

      private EReference reference;

      private int index;

      public ForwardReference(InternalCDOObject source, EReference reference, int index)
      {
        this.source = source;
        this.reference = reference;
        this.index = index;
      }

      public void resolve(InternalCDOObject target)
      {
        if (reference.isMany())
        {
          @SuppressWarnings("unchecked")
          EList<EObject> list = (EList<EObject>)source.eGet(reference);
          list.set(index, target);
        }
        else
        {
          source.eSet(reference, target);
        }
      }
    }
  }
}
