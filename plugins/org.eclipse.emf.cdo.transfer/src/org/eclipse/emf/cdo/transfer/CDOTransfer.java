/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.emf.cdo.spi.transfer.ResourceFactoryRegistryWithoutDefaults;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOTransfer implements INotifier
{
  protected final Notifier notifier = new Notifier();

  private final CDOTransferSystem sourceSystem;

  private final CDOTransferSystem targetSystem;

  private final CDOTransferMapping rootMapping = new CDOTransferMappingImpl(this);

  private final Map<CDOTransferElement, CDOTransferMapping> mappings = new HashMap<CDOTransferElement, CDOTransferMapping>();

  private CDOTransferType defaultTransferType = CDOTransferType.BINARY;

  private ModelTransferContext modelTransferContext = createModelTransferContext();

  public CDOTransfer(CDOTransferSystem sourceSystem, CDOTransferSystem targetSystem)
  {
    this.sourceSystem = sourceSystem;
    this.targetSystem = targetSystem;
  }

  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  public final CDOTransferSystem getSourceSystem()
  {
    return sourceSystem;
  }

  public final CDOTransferSystem getTargetSystem()
  {
    return targetSystem;
  }

  public ModelTransferContext getModelTransferContext()
  {
    return modelTransferContext;
  }

  public final CDOTransferMapping getRootMapping()
  {
    return rootMapping;
  }

  public final CDOTransferType getDefaultTransferType()
  {
    return defaultTransferType;
  }

  public final void setDefaultTransferType(CDOTransferType defaultTransferType)
  {
    if (defaultTransferType == CDOTransferType.FOLDER)
    {
      throw new IllegalArgumentException();
    }

    this.defaultTransferType = defaultTransferType;
  }

  public Set<CDOTransferType> getUsedTransferTypes()
  {
    final Set<CDOTransferType> result = new HashSet<CDOTransferType>();
    rootMapping.accept(new CDOTransferMapping.Visitor()
    {
      public boolean visit(CDOTransferMapping mapping)
      {
        result.add(mapping.getTransferType());
        return true;
      }
    });

    return result;
  }

  public CDOTransferMapping map(IPath sourcePath)
  {
    CDOTransferElement source = sourceSystem.getElement(sourcePath);
    return map(source);
  }

  public CDOTransferMapping map(String sourcePath)
  {
    return map(new Path(sourcePath));
  }

  public CDOTransferMapping map(CDOTransferElement source)
  {
    return map(source, rootMapping);
  }

  protected CDOTransferMapping map(CDOTransferElement source, CDOTransferMapping parent)
  {
    CDOTransferMapping mapping = mappings.get(source);
    if (mapping == null)
    {
      mapping = createMapping(source, parent);
      mappings.put(source, mapping);
    }
    else
    {
      if (mapping.getParent() != parent)
      {
        throw new IllegalStateException();
      }
    }

    return mapping;
  }

  protected void unmap(CDOTransferMapping mapping)
  {
    mappings.remove(mapping.getSource());
    mapping.getChildren();
  }

  protected CDOTransferMapping createMapping(CDOTransferElement source, CDOTransferMapping parent)
  {
    return new CDOTransferMappingImpl(this, source, parent);
  }

  protected ModelTransferContext createModelTransferContext()
  {
    return new ModelTransferContext(this);
  }

  protected CDOTransferType getTransferType(CDOTransferElement source)
  {
    if (source.isDirectory())
    {
      return CDOTransferType.FOLDER;
    }

    if (modelTransferContext.hasResourceFactory(source))
    {
      return CDOTransferType.MODEL;
    }

    CDOTransferType type = sourceSystem.getDefaultTransferType(source);
    if (type == null)
    {
      type = getDefaultTransferType();
    }

    return type;
  }

  protected void validate(CDOTransferMapping mapping)
  {
    if (mapping.getStatus() == CDOTransferMapping.Status.CONFLICT)
    {
      throw new IllegalStateException("Conflict: " + mapping);
    }

    for (CDOTransferMapping child : mapping.getChildren())
    {
      validate(child);
    }
  }

  public void perform()
  {
    validate(rootMapping);
    perform(rootMapping);
    modelTransferContext.save();
  }

  protected void perform(CDOTransferMapping mapping)
  {
    CDOTransferType transferType = mapping.getTransferType();
    if (transferType == CDOTransferType.FOLDER)
    {
      performFolder(mapping);
    }
    else if (transferType == CDOTransferType.MODEL)
    {
      performModel(mapping);
    }
    else if (transferType == CDOTransferType.BINARY)
    {
      performBinary(mapping);
    }
    else if (transferType instanceof CDOTransferType.Text)
    {
      String encoding = ((CDOTransferType.Text)transferType).getEncoding();
      performText(mapping, encoding);
    }
  }

  protected void performFolder(CDOTransferMapping mapping)
  {
    if (mapping.getStatus() == CDOTransferMapping.Status.NEW)
    {
      targetSystem.createFolder(mapping.getFullPath());
    }

    for (CDOTransferMapping child : mapping.getChildren())
    {
      perform(child);
    }
  }

  protected void performModel(CDOTransferMapping mapping)
  {
    modelTransferContext.perform(mapping);
  }

  protected void performBinary(CDOTransferMapping mapping)
  {
    IPath path = mapping.getFullPath();
    InputStream source = mapping.getSource().openInputStream();

    try
    {
      targetSystem.createBinary(path, source);
    }
    finally
    {
      IOUtil.close(source);
    }
  }

  protected void performText(CDOTransferMapping mapping, String encoding)
  {
    IPath path = mapping.getFullPath();
    InputStream source = mapping.getSource().openInputStream();

    try
    {
      targetSystem.createText(path, source, encoding);
    }
    finally
    {
      IOUtil.close(source);
    }
  }

  protected void childrenChanged(CDOTransferMapping mapping, CDOTransferMapping child, ChildrenChangedEvent.Kind kind)
  {
    if (child.getTransferType() == CDOTransferType.MODEL)
    {
      if (kind == ChildrenChangedEvent.Kind.MAPPED)
      {
        modelTransferContext.addModelMapping(child);
      }
      else
      {
        modelTransferContext.removeModelMapping(child);
      }
    }

    IListener[] listeners = notifier.getListeners();
    if (listeners != null)
    {
      notifier.fireEvent(new ChildrenChangedEvent(mapping, child, kind), listeners);
    }
  }

  protected void relativePathChanged(CDOTransferMapping mapping, IPath oldPath, IPath newPath)
  {
    IListener[] listeners = notifier.getListeners();
    if (listeners != null)
    {
      notifier.fireEvent(new RelativePathChangedEvent(mapping, oldPath, newPath), listeners);
    }
  }

  protected void transferTypeChanged(CDOTransferMapping mapping, CDOTransferType oldType, CDOTransferType newType)
  {
    if (oldType == CDOTransferType.MODEL)
    {
      modelTransferContext.removeModelMapping(mapping);
    }

    if (newType == CDOTransferType.MODEL)
    {
      modelTransferContext.addModelMapping(mapping);
    }

    IListener[] listeners = notifier.getListeners();
    if (listeners != null)
    {
      notifier.fireEvent(new TransferTypeChangedEvent(mapping, oldType, newType), listeners);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class MappingEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private CDOTransferMapping mapping;

    private MappingEvent(CDOTransferMapping mapping)
    {
      super(mapping.getTransfer());
      this.mapping = mapping;
    }

    public CDOTransferMapping getMapping()
    {
      return mapping;
    }

    public abstract boolean hasTreeImpact();
  }

  /**
   * @author Eike Stepper
   */
  public static class ChildrenChangedEvent extends MappingEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOTransferMapping child;

    private Kind kind;

    ChildrenChangedEvent(CDOTransferMapping mapping, CDOTransferMapping child, Kind kind)
    {
      super(mapping);
      this.child = child;
      this.kind = kind;
    }

    @Override
    public boolean hasTreeImpact()
    {
      return true;
    }

    public CDOTransferMapping getChild()
    {
      return child;
    }

    public Kind getKind()
    {
      return kind;
    }

    /**
     * @author Eike Stepper
     */
    public enum Kind
    {
      MAPPED, UNMAPPED
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class RelativePathChangedEvent extends MappingEvent
  {
    private static final long serialVersionUID = 1L;

    private IPath oldPath;

    private IPath newPath;

    RelativePathChangedEvent(CDOTransferMapping mapping, IPath oldPath, IPath newPath)
    {
      super(mapping);
      this.oldPath = oldPath;
      this.newPath = newPath;
    }

    @Override
    public boolean hasTreeImpact()
    {
      return true;
    }

    public IPath getOldPath()
    {
      return oldPath;
    }

    public IPath getNewPath()
    {
      return newPath;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class TransferTypeChangedEvent extends MappingEvent
  {
    private static final long serialVersionUID = 1L;

    private CDOTransferType oldType;

    private CDOTransferType newType;

    TransferTypeChangedEvent(CDOTransferMapping mapping, CDOTransferType oldType, CDOTransferType newType)
    {
      super(mapping);
      this.oldType = oldType;
      this.newType = newType;
    }

    @Override
    public boolean hasTreeImpact()
    {
      return false;
    }

    public CDOTransferType getOldType()
    {
      return oldType;
    }

    public CDOTransferType getNewType()
    {
      return newType;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class UnmappedModelsEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private UnmappedModelsEvent(CDOTransfer transfer)
    {
      super(transfer);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ModelTransferContext
  {
    private final CDOTransfer transfer;

    private ResourceSet sourceResourceSet;

    private ResourceSet targetResourceSet;

    private Map<CDOTransferElement, Resource> elementResources = new HashMap<CDOTransferElement, Resource>();

    private Map<Resource, CDOTransferElement> resourceElements = new HashMap<Resource, CDOTransferElement>();

    private Set<Resource> unmappedModels;

    private Map<URI, ModelTransferResolution> resolutions = new HashMap<URI, ModelTransferResolution>();

    protected ModelTransferContext(CDOTransfer transfer)
    {
      this.transfer = transfer;
    }

    public final CDOTransfer getTransfer()
    {
      return transfer;
    }

    public final ResourceSet getSourceResourceSet()
    {
      if (sourceResourceSet == null)
      {
        CDOTransferSystem sourceSystem = transfer.getSourceSystem();
        sourceResourceSet = sourceSystem.provideResourceSet();
        if (sourceResourceSet == null)
        {
          sourceResourceSet = createResourceSet(sourceSystem);
        }
      }

      return sourceResourceSet;
    }

    public final ResourceSet getTargetResourceSet()
    {
      if (targetResourceSet == null)
      {
        CDOTransferSystem targetSystem = transfer.getTargetSystem();
        targetResourceSet = targetSystem.provideResourceSet();
        if (targetResourceSet == null)
        {
          targetResourceSet = createResourceSet(targetSystem);
        }
      }

      return targetResourceSet;
    }

    public void registerSourceExtension(String extension, Resource.Factory factory)
    {
      Map<String, Object> map = getSourceResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
      map.put(extension, factory);
    }

    public void registerSourceProtocol(String protocol, Resource.Factory factory)
    {
      Map<String, Object> map = getSourceResourceSet().getResourceFactoryRegistry().getProtocolToFactoryMap();
      map.put(protocol, factory);
    }

    public void registerSourceContentType(String contentType, Resource.Factory factory)
    {
      Map<String, Object> map = getSourceResourceSet().getResourceFactoryRegistry().getContentTypeToFactoryMap();
      map.put(contentType, factory);
    }

    public void registerTargetExtension(String extension, Resource.Factory factory)
    {
      Map<String, Object> map = getTargetResourceSet().getResourceFactoryRegistry().getExtensionToFactoryMap();
      map.put(extension, factory);
    }

    public void registerTargetProtocol(String protocol, Resource.Factory factory)
    {
      Map<String, Object> map = getTargetResourceSet().getResourceFactoryRegistry().getProtocolToFactoryMap();
      map.put(protocol, factory);
    }

    public void registerTargetContentType(String contentType, Resource.Factory factory)
    {
      Map<String, Object> map = getTargetResourceSet().getResourceFactoryRegistry().getContentTypeToFactoryMap();
      map.put(contentType, factory);
    }

    public Set<Resource> resolve()
    {
      if (unmappedModels == null)
      {
        unmappedModels = new HashSet<Resource>();

        ResourceSet resourceSet = getSourceResourceSet();
        EList<Resource> resources = resourceSet.getResources();
        resources.clear();

        Set<Resource> mappedModels = resourceElements.keySet();
        resources.addAll(mappedModels);
        EcoreUtil.resolveAll(resourceSet);

        for (Resource resource : resources)
        {
          if (!mappedModels.contains(resource))
          {
            unmappedModels.add(resource);
          }
        }

        fireUnmappedModelsEvent();
      }

      return unmappedModels;
    }

    public ModelTransferResolution getResolution(URI uri)
    {
      return resolutions.get(uri);
    }

    public ModelTransferResolution setResolution(URI uri, ModelTransferResolution resolution)
    {
      ModelTransferResolution old = resolutions.put(uri, resolution);
      if (resolution != old)
      {
        fireUnmappedModelsEvent();
      }

      return old;
    }

    protected void fireUnmappedModelsEvent()
    {
      transfer.notifier.fireEvent(new UnmappedModelsEvent(transfer));
    }

    protected void addModelMapping(CDOTransferMapping mapping)
    {
      CDOTransferElement element = mapping.getSource();
      URI uri = element.getURI();

      ResourceSet resourceSet = getSourceResourceSet();
      Resource resource = resourceSet.getResource(uri, true);
      elementResources.put(element, resource);
      resourceElements.put(resource, element);
      unmappedModels = null;
      fireUnmappedModelsEvent();
    }

    protected void removeModelMapping(CDOTransferMapping mapping)
    {
      CDOTransferElement element = mapping.getSource();
      Resource resource = elementResources.remove(element);
      if (resource != null)
      {
        resourceElements.remove(resource);
        resource.unload();

        ResourceSet resourceSet = getSourceResourceSet();
        resourceSet.getResources().remove(resource);
      }

      unmappedModels = null;
      fireUnmappedModelsEvent();
    }

    protected Resource getSourceResource(CDOTransferMapping mapping)
    {
      URI uri = mapping.getSource().getURI();
      ResourceSet sourceResourceSet = getSourceResourceSet();
      return sourceResourceSet.getResource(uri, true);
    }

    protected Resource getTargetResource(CDOTransferMapping mapping)
    {
      IPath path = mapping.getFullPath();
      ResourceSet targetResourceSet = getTargetResourceSet();
      CDOTransferSystem targetSystem = transfer.getTargetSystem();
      return targetSystem.createModel(targetResourceSet, path);
    }

    protected ResourceSet createResourceSet(CDOTransferSystem system)
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.setResourceFactoryRegistry(new ResourceFactoryRegistryWithoutDefaults());
      return resourceSet;
    }

    protected boolean hasResourceFactory(CDOTransferElement source)
    {
      URI uri = source.getURI();

      // TODO Derive resourceSet from element.getSystem()?
      Resource.Factory.Registry registry = getSourceResourceSet().getResourceFactoryRegistry();
      return registry.getFactory(uri) != null;
    }

    protected void perform(CDOTransferMapping mapping)
    {
      Resource sourceResource = getSourceResource(mapping);
      Resource targetResource = getTargetResource(mapping);

      EList<EObject> sourceContents = sourceResource.getContents();
      Collection<EObject> targetContents = EcoreUtil.copyAll(sourceContents);

      EList<EObject> contents = targetResource.getContents();
      contents.addAll(targetContents);
    }

    protected void save()
    {
      try
      {
        for (Resource resource : elementResources.values())
        {
          resource.save(null);
        }
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface ModelTransferResolution
  {

  }
}
