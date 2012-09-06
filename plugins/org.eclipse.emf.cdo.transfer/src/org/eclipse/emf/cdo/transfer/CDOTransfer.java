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

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
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

  private CDOTransferType defaultTransferType = CDOTransferType.UNKNOWN;

  private ResourceSet sourceResourceSet;

  private ResourceSet targetResourceSet;

  private Set<Resource> resourcesToSave = new HashSet<Resource>();

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

  public final ResourceSet getSourceResourceSet()
  {
    if (sourceResourceSet == null)
    {
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
      targetResourceSet = targetSystem.provideResourceSet();
      if (targetResourceSet == null)
      {
        targetResourceSet = createResourceSet(targetSystem);
      }
    }

    return targetResourceSet;
  }

  protected ResourceSet createResourceSet(CDOTransferSystem system)
  {
    Resource.Factory.Registry registry = new ResourceFactoryRegistryImpl()
    {
      {
        getProtocolToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap());
        getExtensionToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap());
        getContentTypeToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap());

        getExtensionToFactoryMap().remove(Resource.Factory.Registry.DEFAULT_EXTENSION);
        getContentTypeToFactoryMap().remove(Resource.Factory.Registry.DEFAULT_CONTENT_TYPE_IDENTIFIER);
      }

      @Override
      protected Factory delegatedGetFactory(URI uri, String contentTypeIdentifier)
      {
        return null;
      }
    };

    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.setResourceFactoryRegistry(registry);
    return resourceSet;
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

  public CDOTransferMapping map(IPath sourcePath) throws IOException
  {
    CDOTransferElement source = sourceSystem.getElement(sourcePath);
    return map(source);
  }

  public CDOTransferMapping map(String sourcePath) throws IOException
  {
    return map(new Path(sourcePath));
  }

  public CDOTransferMapping map(CDOTransferElement source) throws IOException
  {
    return map(source, rootMapping);
  }

  protected CDOTransferMapping map(CDOTransferElement source, CDOTransferMapping parent) throws IOException
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

  protected CDOTransferMapping createMapping(CDOTransferElement source, CDOTransferMapping parent) throws IOException
  {
    return new CDOTransferMappingImpl(this, source, parent);
  }

  protected void unmap(CDOTransferMapping mapping)
  {
    mappings.remove(mapping.getSource());
    mapping.getChildren();
  }

  protected boolean hasResourceFactory(CDOTransferElement source)
  {
    URI uri = source.getURI();
    // TODO Derive resourceSet from element.getSystem()?
    Registry registry = getSourceResourceSet().getResourceFactoryRegistry();
    return registry.getFactory(uri) != null;
  }

  protected CDOTransferType getTransferType(CDOTransferElement source)
  {
    CDOTransferType type = source.getSystem().getDefaultTransferType(source);
    if (type == CDOTransferType.UNKNOWN)
    {
      if (hasResourceFactory(source))
      {
        return CDOTransferType.MODEL;
      }
    }

    if (type == CDOTransferType.UNKNOWN)
    {
      type = getDefaultTransferType();
    }

    return type;
  }

  protected Resource getSourceResource(CDOTransferMapping mapping)
  {
    URI uri = mapping.getSource().getURI();
    ResourceSet sourceResourceSet = getSourceResourceSet();
    return sourceResourceSet.getResource(uri, true);
  }

  protected Resource getTargetResource(CDOTransferMapping mapping) throws IOException
  {
    IPath path = mapping.getFullPath();
    ResourceSet targetResourceSet = getTargetResourceSet();
    return targetSystem.createModel(targetResourceSet, path);
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

  public void perform() throws IOException
  {
    validate(rootMapping);
    perform(rootMapping);

    for (Resource resource : resourcesToSave)
    {
      resource.save(null);
    }

    resourcesToSave.clear();
  }

  protected void perform(CDOTransferMapping mapping) throws IOException
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

  protected void performFolder(CDOTransferMapping mapping) throws IOException
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

  protected void performModel(CDOTransferMapping mapping) throws IOException
  {
    Resource sourceResource = getSourceResource(mapping);
    Resource targetResource = getTargetResource(mapping);

    EList<EObject> sourceContents = sourceResource.getContents();
    Collection<EObject> targetContents = EcoreUtil.copyAll(sourceContents);

    EList<EObject> contents = targetResource.getContents();
    contents.addAll(targetContents);
    resourcesToSave.add(targetResource);
  }

  protected void performBinary(CDOTransferMapping mapping) throws IOException
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

  protected void performText(CDOTransferMapping mapping, String encoding) throws IOException
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
}
