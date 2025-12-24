/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.util;

import org.eclipse.emf.cdo.ecore.dependencies.DependenciesFactory;
import org.eclipse.emf.cdo.ecore.dependencies.Element;
import org.eclipse.emf.cdo.ecore.dependencies.Link;
import org.eclipse.emf.cdo.ecore.dependencies.Model;
import org.eclipse.emf.cdo.ecore.dependencies.ModelContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class WorkspaceScanner
{
  private WorkspaceScanner()
  {
  }

  public static ModelContainer scanWorkspace()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(true));

    try
    {
      ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceProxyVisitor()
      {
        @Override
        public boolean visit(IResourceProxy proxy) throws CoreException
        {
          if (proxy.getType() == IResource.FILE && proxy.getName().endsWith(".ecore"))
          {
            String fullPath = proxy.requestFullPath().toString();
            resourceSet.getResource(URI.createPlatformResourceURI(fullPath, true), true);
            return false;
          }

          return true;
        }
      }, IContainer.DO_NOT_CHECK_EXISTENCE);
    }
    catch (CoreException ex)
    {
      ex.printStackTrace();
    }

    return resolveAll(resourceSet);
  }

  private static ModelContainer resolveAll(ResourceSet resourceSet)
  {
    ModelContainer container = DependenciesFactory.eINSTANCE.createModelContainer();

    List<Resource> resources = resourceSet.getResources();
    int start = 0;
    int lastStart = -1;

    while (start != lastStart)
    {
      int size = resources.size();
      for (int i = start; i < size; ++i)
      {
        resolveAll(container, resources.get(i));
      }

      lastStart = start;
      start = size;
    }

    return container;
  }

  private static void resolveAll(ModelContainer container, Resource source)
  {
    for (EObject eObject : source.getContents())
    {
      resolveAll(container, source, eObject);
    }
  }

  private static void resolveAll(ModelContainer container, Resource source, EObject eObject)
  {
    eObject.eContainer();
    resolveCrossReferences(container, source, eObject);

    for (Iterator<EObject> i = eObject.eAllContents(); i.hasNext();)
    {
      EObject childEObject = i.next();
      resolveCrossReferences(container, source, childEObject);
    }
  }

  private static void resolveCrossReferences(ModelContainer container, Resource source, EObject eObject)
  {
    URI sourceURI = EcoreUtil.getURI(eObject);
    Model sourceModel = getModel(container, null, source);
    Element sourceElement = getElement(sourceModel, sourceURI);

    for (FeatureIterator<EObject> it = (FeatureIterator<EObject>)eObject.eCrossReferences().iterator(); it.hasNext();)
    {
      EObject targetObject = it.next();
      EReference reference = (EReference)it.feature();
      if (reference.isDerived())
      {
        continue;
      }

      Model targetModel = null;
      boolean proxy = false;

      URI targetURI = ((InternalEObject)targetObject).eProxyURI();
      if (targetURI != null)
      {
        targetModel = getModel(container, targetURI, null);
        proxy = true;
      }
      else
      {
        Resource target = targetObject.eResource();
        if (target != null && target != source)
        {
          targetURI = EcoreUtil.getURI(targetObject);
          targetModel = getModel(container, targetURI, target);
        }
      }

      if (targetURI != null)
      {
        Element targetElement = getElement(targetModel, targetURI);
        if (proxy)
        {
          targetElement.setExists(false);
        }

        Link link = DependenciesFactory.eINSTANCE.createLink();
        link.setUri(sourceURI);
        link.setSource(sourceElement);
        link.setTarget(targetElement);
        link.setReference(reference);
      }
    }
  }

  private static Model getModel(ModelContainer container, URI uri, Resource resource)
  {
    if (uri == null)
    {
      uri = resource.getURI();
    }

    uri = uri.trimFragment();

    Model model = container.getModel(uri);
    if (model == null)
    {
      model = DependenciesFactory.eINSTANCE.createModel();
      model.setUri(uri);
      model.setContainer(container);
    }

    if (resource != null)
    {
      EPackage ePackage = (EPackage)resource.getContents().get(0);
      model.setNsURI(ePackage.getNsURI());
      model.setName(ePackage.getName());
      model.setExists(true);
    }

    return model;
  }

  private static Element getElement(Model model, URI uri)
  {
    Element element = model.getElement(uri);
    if (element == null)
    {
      element = DependenciesFactory.eINSTANCE.createElement();
      element.setUri(uri);
      element.setModel(model);
      element.setExists(true);
    }

    return element;
  }
}
