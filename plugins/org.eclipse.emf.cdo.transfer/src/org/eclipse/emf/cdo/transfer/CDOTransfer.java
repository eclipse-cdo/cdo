/*
 * Copyright (c) 2012, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.om.monitor.SubProgressMonitor;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstracts the transfer of a tree of {@link CDOTransferElement elements} for a
 * {@link #getSourceSystem() source} to a {@link #getTargetSystem() target} {@link CDOTransferSystem system}.
 * <p>
 * The mappings of the source elements to their target elements is represented as a tree of {@link CDOTransferMapping transfer mappings}.
 * <p>
 * A transfer fires the following {@link IEvent events}:
 * <ul>
 * <li>{@link ChildrenChangedEvent} when the {@link CDOTransferMapping#getChildren() children} of a mapping have changed.
 * <li>{@link RelativePathChangedEvent} when the {@link CDOTransferMapping#getRelativePath() relative path} of a mapping has changed.
 * <li>{@link TransferTypeChangedEvent} when the {@link CDOTransferMapping#getTransferType() transfer type} of a mapping has changed.
 * <li>{@link UnmappedModelsEvent} when the set of {@link CDOTransfer.ModelTransferContext#getUnmappedModels() unmapped models} has changed.
 * </ul>
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOTransfer implements INotifier
{
  protected final Notifier notifier = new Notifier();

  private final CDOTransferSystem sourceSystem;

  private final CDOTransferSystem targetSystem;

  private final CDOTransferMapping rootMapping = new CDOTransferMappingImpl(this);

  private final Map<CDOTransferElement, CDOTransferMapping> mappings = new HashMap<>();

  private CDOTransferType defaultTransferType = CDOTransferType.BINARY;

  private ModelTransferContext modelTransferContext = createModelTransferContext();

  private PathProvider pathProvider = PathProvider.DEFAULT;

  public CDOTransfer(CDOTransferSystem sourceSystem, CDOTransferSystem targetSystem)
  {
    this.sourceSystem = sourceSystem;
    this.targetSystem = targetSystem;
  }

  @Override
  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  @Override
  public boolean hasListeners()
  {
    return notifier.hasListeners();
  }

  @Override
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

  /**
   * @since 4.3
   */
  public final PathProvider getPathProvider()
  {
    return pathProvider;
  }

  /**
   * @since 4.3
   */
  public final void setPathProvider(PathProvider pathProvider)
  {
    this.pathProvider = pathProvider;
  }

  public ModelTransferContext getModelTransferContext()
  {
    return modelTransferContext;
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
    final Set<CDOTransferType> result = new HashSet<>();
    rootMapping.accept(new CDOTransferMapping.Visitor()
    {
      @Override
      public boolean visit(CDOTransferMapping mapping)
      {
        result.add(mapping.getTransferType());
        return true;
      }
    });

    return result;
  }

  public final CDOTransferMapping getRootMapping()
  {
    return rootMapping;
  }

  public IPath getTargetPath()
  {
    return rootMapping.getRelativePath();
  }

  public void setTargetPath(IPath targetPath)
  {
    rootMapping.setRelativePath(targetPath);
  }

  public void setTargetPath(String path)
  {
    rootMapping.setRelativePath(path);
  }

  public int getMappingCount()
  {
    return mappings.size();
  }

  public CDOTransferMapping map(IPath sourcePath, IProgressMonitor monitor)
  {
    CDOTransferElement source = sourceSystem.getElement(sourcePath);
    return map(source, monitor);
  }

  public CDOTransferMapping map(String sourcePath, IProgressMonitor monitor)
  {
    return map(new Path(sourcePath), monitor);
  }

  public CDOTransferMapping map(CDOTransferElement source, IProgressMonitor monitor)
  {
    return map(source, rootMapping, monitor);
  }

  protected CDOTransferMapping map(CDOTransferElement source, CDOTransferMapping parent, IProgressMonitor monitor)
  {
    CDOTransferMapping mapping = mappings.get(source);
    if (mapping == null)
    {
      mapping = createMapping(source, parent, monitor);
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

  protected CDOTransferMapping createMapping(CDOTransferElement source, CDOTransferMapping parent, IProgressMonitor monitor)
  {
    return new CDOTransferMappingImpl(this, source, parent, monitor);
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

    CDOTransferType type = sourceSystem.getDefaultTransferType(source);
    if (type != null)
    {
      return type;
    }

    if (modelTransferContext.hasResourceFactory(source))
    {
      return CDOTransferType.MODEL;
    }

    return getDefaultTransferType();
  }

  protected void validate(CDOTransferMapping mapping, IProgressMonitor monitor)
  {
    ConcurrencyUtil.checkCancelation(monitor);
    if (mapping.getStatus() == CDOTransferMapping.Status.CONFLICT)
    {
      throw new IllegalStateException("Conflict: " + mapping);
    }

    monitor.worked(1);
    for (CDOTransferMapping child : mapping.getChildren())
    {
      ConcurrencyUtil.checkCancelation(monitor);
      validate(child, monitor);
    }
  }

  public void perform()
  {
    perform(new NullProgressMonitor());
  }

  public void perform(IProgressMonitor monitor)
  {
    try
    {
      int mappingCount = getMappingCount();
      monitor.beginTask("Perform transfer from " + getSourceSystem() + " to " + getTargetSystem(), 5 * mappingCount);

      monitor.subTask("Validating transfer");
      validate(rootMapping, new SubProgressMonitor(monitor, mappingCount));

      perform(rootMapping, monitor);
      modelTransferContext.performFinish(new SubProgressMonitor(monitor, 2 * mappingCount));
    }
    finally
    {
      monitor.done();
    }
  }

  protected void perform(CDOTransferMapping mapping, IProgressMonitor monitor)
  {
    monitor.subTask("Transferring " + mapping);
    ConcurrencyUtil.checkCancelation(monitor);

    CDOTransferType transferType = mapping.getTransferType();
    if (transferType == CDOTransferType.FOLDER)
    {
      performFolder(mapping, monitor);
    }
    else if (transferType == CDOTransferType.MODEL)
    {
      performModel(mapping, monitor);
    }
    else if (transferType == CDOTransferType.BINARY)
    {
      performBinary(mapping, monitor);
    }
    else if (transferType instanceof CDOTransferType.Text)
    {
      String encoding = ((CDOTransferType.Text)transferType).getEncoding();
      performText(mapping, encoding, monitor);
    }
  }

  protected void performFolder(CDOTransferMapping mapping, IProgressMonitor monitor)
  {
    if (mapping.getStatus() == CDOTransferMapping.Status.NEW)
    {
      targetSystem.createFolder(mapping.getFullPath());
    }

    monitor.worked(2);
    for (CDOTransferMapping child : mapping.getChildren())
    {
      perform(child, monitor);
    }
  }

  protected void performModel(CDOTransferMapping mapping, IProgressMonitor monitor)
  {
    modelTransferContext.perform(mapping, new SubProgressMonitor(monitor, 2));
  }

  protected void performBinary(CDOTransferMapping mapping, IProgressMonitor monitor)
  {
    IPath path = mapping.getFullPath();
    InputStream source = mapping.getSource().openInputStream();

    try
    {
      targetSystem.createBinary(path, source, new SubProgressMonitor(monitor, 2));
    }
    finally
    {
      IOUtil.close(source);
    }
  }

  protected void performText(CDOTransferMapping mapping, String encoding, IProgressMonitor monitor)
  {
    IPath path = mapping.getFullPath();
    InputStream source = mapping.getSource().openInputStream();

    try
    {
      targetSystem.createText(path, source, encoding, new SubProgressMonitor(monitor, 2));
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
   * Provides the path of a transfer element relative to its parent.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public interface PathProvider
  {
    public static final PathProvider DEFAULT = new PathProvider()
    {
      @Override
      public IPath getPath(CDOTransferElement element)
      {
        return new Path(element.getName());
      }
    };

    public IPath getPath(CDOTransferElement element);
  }

  /**
   * An abstract base implementation of a {@link CDOTransferMapping mapping} {@link ILifecycleEvent event}.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
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
   * A {@link MappingEvent mapping event} fired from a {@link CDOTransfer transfer} when
   * the {@link CDOTransferMapping#getChildren() children} of a mapping have changed.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
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
     * Enumerates the possible values of {@link ChildrenChangedEvent#getKind()}.
     *
     * @author Eike Stepper
     */
    public enum Kind
    {
      MAPPED, UNMAPPED
    }
  }

  /**
   * A {@link MappingEvent mapping event} fired from a {@link CDOTransfer transfer} when
   * the {@link CDOTransferMapping#getRelativePath() relative path} of a mapping has changed.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
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
   * A {@link MappingEvent mapping event} fired from a {@link CDOTransfer transfer} when
   * the {@link CDOTransferMapping#getTransferType() transfer type} of a mapping has changed.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
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
   * An {@link IEvent event} fired from a {@link CDOTransfer transfer} when
   * the set of {@link CDOTransfer.ModelTransferContext#getUnmappedModels() unmapped models} has changed.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
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
   * Encapsulates the model-specific aspects of a {@link CDOTransfer transfer}.
   *
   * @author Eike Stepper
   */
  public static class ModelTransferContext
  {
    private final CDOTransfer transfer;

    private ResourceSet sourceResourceSet;

    private ResourceSet targetResourceSet;

    private Map<CDOTransferElement, Resource> elementResources = new HashMap<>();

    private Map<Resource, CDOTransferElement> resourceElements = new HashMap<>();

    private Set<Resource> unmappedModels;

    private Map<URI, ModelTransferResolution> resolutions = new HashMap<>();

    private ResolveProxyAdapter resolveProxyAdapter;

    private Copier copier;

    protected ModelTransferContext(CDOTransfer transfer)
    {
      this.transfer = transfer;
      resolveProxyAdapter = new ResolveProxyAdapter();
      copier = createCopier();
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

    public Set<Resource> getUnmappedModels()
    {
      if (unmappedModels == null)
      {
        unmappedModels = resolve();
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

    protected Set<Resource> resolve()
    {
      Set<Resource> mappedModels = resourceElements.keySet();

      ResourceSet resourceSet = getSourceResourceSet();
      EList<Resource> resources = resourceSet.getResources();

      Set<Resource> unmappedModels = new HashSet<>(resources);
      unmappedModels.removeAll(mappedModels);
      return unmappedModels;
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

    protected Copier createCopier()
    {
      return new Copier();
    }

    protected ResourceSet createResourceSet(CDOTransferSystem system)
    {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.setResourceFactoryRegistry(new ResourceFactoryRegistryWithoutDefaults());
      resourceSet.eAdapters().add(resolveProxyAdapter);

      return resourceSet;
    }

    protected boolean hasResourceFactory(CDOTransferElement source)
    {
      URI uri = source.getURI();

      // TODO Derive resourceSet from element.getSystem()?
      Resource.Factory.Registry registry = getSourceResourceSet().getResourceFactoryRegistry();
      return registry.getFactory(uri) != null;
    }

    protected void perform(CDOTransferMapping mapping, IProgressMonitor monitor)
    {
      try
      {
        monitor.beginTask("", 2);

        Resource sourceResource = getSourceResource(mapping);
        Resource targetResource = getTargetResource(mapping); // Create target resource

        EList<EObject> sourceContents = sourceResource.getContents();
        Collection<EObject> targetContents = copier.copyAll(sourceContents);
        monitor.worked(1);

        EList<EObject> contents = targetResource.getContents();
        contents.addAll(targetContents);
        monitor.worked(1);
      }
      finally
      {
        monitor.done();
      }
    }

    protected void performFinish(IProgressMonitor monitor)
    {
      monitor.subTask("Copying model references");
      copier.copyReferences();

      CDOTransferSystem targetSystem = getTransfer().getTargetSystem();
      targetSystem.saveModels(getTargetResourceSet().getResources(), monitor);
    }

    /**
     * An {@link AdapterImpl adapter} for a {@link ResourceSet resource set} that resolves all proxies in all resources when they are loaded.
     *
     * @author Eike Stepper
     */
    public static class ResolveProxyAdapter extends AdapterImpl
    {
      private LoadResourceAdapter loadResourceAdapter;

      public ResolveProxyAdapter()
      {
        loadResourceAdapter = new LoadResourceAdapter();
      }

      @Override
      public void notifyChanged(Notification msg)
      {
        int eventType = msg.getEventType();
        switch (eventType)
        {
        case Notification.SET:
        {
          Resource oldValue = (Resource)msg.getOldValue();
          if (oldValue != null)
          {
            removeResource(oldValue);
          }

          Resource newValue = (Resource)msg.getNewValue();
          if (newValue != null)
          {
            addResource(newValue);
          }

          break;
        }
        case Notification.ADD:
        {
          Resource newValue = (Resource)msg.getNewValue();
          if (newValue != null)
          {
            addResource(newValue);
          }
          break;
        }
        case Notification.ADD_MANY:
        {
          @SuppressWarnings("unchecked")
          Collection<Resource> newValues = (Collection<Resource>)msg.getNewValue();
          for (Resource newValue : newValues)
          {
            addResource(newValue);
          }
          break;
        }
        case Notification.REMOVE:
        {
          Resource oldValue = (Resource)msg.getOldValue();
          if (oldValue != null)
          {
            removeResource(oldValue);
          }
          break;
        }
        case Notification.REMOVE_MANY:
        {
          @SuppressWarnings("unchecked")
          Collection<Resource> oldValues = (Collection<Resource>)msg.getOldValue();
          for (Resource oldContentValue : oldValues)
          {
            removeResource(oldContentValue);
          }
          break;
        }
        }
      }

      private void addResource(Resource resource)
      {
        EcoreUtil.resolveAll(resource);
        resource.eAdapters().add(loadResourceAdapter);
      }

      private void removeResource(Resource resource)
      {
      }

      /**
       * An {@link AdapterImpl adapter} for a {@link Resource resource} that resolves all proxies in that resource when it's demand loaded.
       *
       * @author Eike Stepper
       */
      public static class LoadResourceAdapter extends AdapterImpl
      {
        @Override
        public void notifyChanged(Notification msg)
        {
          if (msg.getFeatureID(Resource.class) == Resource.RESOURCE__IS_LOADED)
          {
            int eventType = msg.getEventType();
            switch (eventType)
            {
            case Notification.SET:
            {
              boolean isLoaded = msg.getNewBooleanValue();
              if (isLoaded)
              {
                Resource resource = (Resource)msg.getNotifier();
                EcoreUtil.resolveAll(resource);
              }
              break;
            }
            }
          }
        }
      }
    }
  }

  /**
   * Reserved for future use.
   *
   * @author Eike Stepper
   * @noextend This class is not intended to be subclassed by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ModelTransferResolution
  {
  }
}
