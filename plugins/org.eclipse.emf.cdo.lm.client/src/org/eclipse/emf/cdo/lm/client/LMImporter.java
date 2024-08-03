/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.lm.Drop;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.StreamSpec;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportElement.ImportBinary;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportElement.ImportFolder;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportElement.ImportResource;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportElement.ImportText;
import org.eclipse.emf.cdo.lm.client.LMImporter.ImportElement.Type;
import org.eclipse.emf.cdo.lm.internal.client.SystemDescriptor;
import org.eclipse.emf.cdo.lm.modules.DependencyDefinition;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.lm.modules.ModulesFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CollectionUtil;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Eike Stepper
 * @since 1.4
 */
public final class LMImporter
{
  public static final UnaryOperator<URI> EXTERNAL_REFERENCE_PRESERVER = uri -> uri;

  public static final UnaryOperator<URI> EXTERNAL_REFERENCE_REJECTER = uri -> {
    throw new IllegalStateException("External reference: " + uri);
  };

  public static final UnaryOperator<URI> EXTERNAL_REFERENCE_UNSETTER = uri -> null;

  private static final URI EXTERNAL_REFERENCE_UNSET_MARKER = URI.createURI("UNSET_EXTERNAL_REFERENCE://cdo.lm");

  private final Map<String, ImportModule> modules = new HashMap<>();

  private final Map<URI, ImportElement> elements = new HashMap<>();

  public LMImporter()
  {
  }

  public Map<String, ImportModule> getModules()
  {
    return Collections.unmodifiableMap(modules);
  }

  public ImportModule getModule(String name)
  {
    return modules.get(name);
  }

  public ImportModule addModule(String moduleName, URI rootURI)
  {
    if (modules.containsKey(moduleName))
    {
      throw new IllegalStateException("Module import already exists: " + moduleName);
    }

    ImportModule module = new ImportModule(moduleName, rootURI);
    modules.put(moduleName, module);
    return module;
  }

  public ImportResolution resolve(ResourceSet resourceSet)
  {
    return resolve(resourceSet, false);
  }

  public ImportResolution resolve(ResourceSet resourceSet, boolean rejectExternalReferences)
  {
    UnaryOperator<URI> externalReferenceHandler = rejectExternalReferences ? EXTERNAL_REFERENCE_REJECTER : null;
    return resolve(resourceSet, externalReferenceHandler);
  }

  public ImportResolution resolve(ResourceSet resourceSet, UnaryOperator<URI> externalReferenceHandler)
  {
    if (externalReferenceHandler == null)
    {
      externalReferenceHandler = EXTERNAL_REFERENCE_PRESERVER;
    }

    for (ImportModule module : modules.values())
    {
      module.accept(element -> {
        if (element instanceof ImportResource)
        {
          ImportResource importResource = (ImportResource)element;
          importResource.resource = resourceSet.getResource(element.getURI(), true);
        }
      });
    }

    EcoreUtil.resolveAll(resourceSet);
    Map<EObject, URI> externalReferences = new HashMap<>(); // targetObject -> handledURI

    for (ImportElement element : elements.values())
    {
      if (element instanceof ImportResource)
      {
        Resource resource = ((ImportResource)element).getResource();
        if (resource != null)
        {
          ImportModule sourceModule = element.getModule();

          for (TreeIterator<EObject> it = resource.getAllContents(); it.hasNext();)
          {
            EObject source = it.next();

            for (Iterator<EObject> it2 = ((InternalEList<EObject>)source.eCrossReferences()).basicListIterator(); it2.hasNext();)
            {
              EObject target = it2.next();
              if (target.eIsProxy())
              {
                throw new IllegalStateException("Unresolved proxy: " + ((InternalEObject)target).eProxyURI());
              }

              URI targetURI = EcoreUtil.getURI(target);
              URI targetElementURI = targetURI.trimFragment();

              ImportElement targetElement = elements.get(targetElementURI);
              if (targetElement == null)
              {
                URI handledURI = externalReferenceHandler.apply(targetURI);
                if (handledURI == null)
                {
                  handledURI = EXTERNAL_REFERENCE_UNSET_MARKER;
                }

                externalReferences.put(target, handledURI);
                continue;
              }

              ImportModule targetModule = targetElement.getModule();
              if (targetModule != sourceModule)
              {
                sourceModule.registerDependency(targetModule);
              }
            }
          }
        }
      }
    }

    return new ImportResolution(resourceSet, externalReferences);
  }

  private void registerElement(ImportElement element)
  {
    URI uri = element.getURI();
    if (elements.containsKey(uri))
    {
      throw new IllegalArgumentException("Duplicate element in importer: " + element);
    }

    elements.put(uri, element);
  }

  /**
   * @author Eike Stepper
   */
  public final class ImportModule implements Consumer<Consumer<ImportElement>>
  {
    private final String name;

    private final ImportFolder root;

    private final Map<URI, ImportElement> elements = new HashMap<>();

    private final Set<ImportModule> resolvedDependencies = new HashSet<>();

    private final Set<ImportModule> extraDependencies = new HashSet<>();

    private ImportModule(String name, URI rootURI)
    {
      this.name = name;
      root = new ImportFolder(this, rootURI);
    }

    public Set<ImportModule> getDependencies()
    {
      Set<ImportModule> dependencies = new HashSet<>(resolvedDependencies);
      dependencies.addAll(extraDependencies);
      return dependencies;
    }

    public Set<ImportModule> getResolvedDependencies()
    {
      return Collections.unmodifiableSet(resolvedDependencies);
    }

    public Set<ImportModule> getExtraDependencies()
    {
      return Collections.unmodifiableSet(extraDependencies);
    }

    public void addExtraDependency(ImportModule extraDependency)
    {
      if (!modules.containsValue(extraDependency))
      {
        throw new IllegalArgumentException("Extra dependency is not an importer module: " + extraDependency);
      }

      extraDependencies.add(extraDependency);
    }

    public String getName()
    {
      return name;
    }

    public ImportFolder getRoot()
    {
      return root;
    }

    public URI getRootURI()
    {
      return root.getURI();
    }

    @Override
    public void accept(Consumer<ImportElement> visitor)
    {
      root.accept(visitor);
    }

    public ImportElement getChild(String name)
    {
      return root.getChild(name);
    }

    public List<ImportElement> getChildren()
    {
      return root.getChildren();
    }

    public ImportFolder getOrAddFolderPath(String path)
    {
      return root.getOrAddFolderPath(path);
    }

    public ImportFolder getOrAddFolder(String name)
    {
      return root.getOrAddFolder(name);
    }

    public ImportResource addResource(String name)
    {
      return root.addResource(name);
    }

    public ImportBinary addBinary(String name)
    {
      return root.addBinary(name);
    }

    public ImportText addText(String name, String encoding)
    {
      return root.addText(name, encoding);
    }

    @Override
    public String toString()
    {
      return name;
    }

    private void registerElement(ImportElement element)
    {
      URI uri = element.getURI();
      if (elements.containsKey(uri))
      {
        throw new IllegalArgumentException("Duplicate element in module: " + element);
      }

      elements.put(uri, element);
      LMImporter.this.registerElement(element);
    }

    private void registerDependency(ImportModule dependency)
    {
      resolvedDependencies.add(dependency);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class ImportElement implements Consumer<Consumer<ImportElement>>
  {
    private static final List<ImportElement> NO_CHILDREN = Collections.unmodifiableList(Collections.emptyList());

    private final ImportModule module;

    private final ImportFolder parent;

    private final String name;

    private final String[] segments;

    private final URI uri;

    /**
     * Creates a root element.
     */
    private ImportElement(ImportModule module, URI rootURI)
    {
      this.module = Objects.requireNonNull(module);
      parent = null;
      name = null;
      segments = new String[0];
      uri = rootURI;
      module.registerElement(this);
    }

    private ImportElement(ImportFolder parent, String name)
    {
      module = Objects.requireNonNull(parent).getModule();
      this.parent = parent;
      this.name = requireValidName(name);

      List<String> list = new ArrayList<>(Arrays.asList(parent.getSegments()));
      list.add(name);
      segments = list.toArray(new String[list.size()]);

      uri = parent.getURI().appendSegment(name);
      module.registerElement(this);
    }

    public ImportModule getModule()
    {
      return module;
    }

    public ImportFolder getParent()
    {
      return parent;
    }

    public String getName()
    {
      return name;
    }

    public abstract Type getType();

    public String[] getSegments()
    {
      return segments;
    }

    public URI getURI()
    {
      return uri;
    }

    public String getPath()
    {
      return String.join(CDOURIUtil.SEGMENT_SEPARATOR, segments);
    }

    public boolean isRoot()
    {
      return parent == null;
    }

    public abstract boolean isFolder();

    public boolean isLeaf()
    {
      return !isFolder();
    }

    @Override
    public void accept(Consumer<ImportElement> visitor)
    {
      visitor.accept(this);
    }

    @Override
    public String toString()
    {
      return getPath();
    }

    public static String requireValidName(String name)
    {
      if (!URI.validSegment(name))
      {
        throw new IllegalArgumentException("Name is not a valid URI path segment: " + name);
      }

      return name;
    }

    /**
     * @author Eike Stepper
     */
    public enum Type
    {
      FOLDER, RESOURCE, BINARY, TEXT;
    }

    /**
     * @author Eike Stepper
     */
    public static final class ImportFolder extends ImportElement
    {
      private List<ImportElement> children;

      /**
       * Creates a root element.
       */
      private ImportFolder(ImportModule module, URI rootURI)
      {
        super(module, rootURI);
      }

      private ImportFolder(ImportFolder parent, String name)
      {
        super(parent, name);
      }

      @Override
      public Type getType()
      {
        return Type.FOLDER;
      }

      @Override
      public boolean isFolder()
      {
        return true;
      }

      @Override
      public void accept(Consumer<ImportElement> visitor)
      {
        super.accept(visitor);

        if (children != null)
        {
          for (ImportElement child : children)
          {
            child.accept(visitor);
          }
        }
      }

      public List<ImportElement> getChildren()
      {
        return children == null ? NO_CHILDREN : Collections.unmodifiableList(children);
      }

      public ImportElement getChild(String name)
      {
        if (children != null)
        {
          for (ImportElement child : children)
          {
            if (child.getName().equals(name))
            {
              return child;
            }
          }
        }

        return null;
      }

      public ImportFolder getOrAddFolderPath(String path)
      {
        ImportFolder folder = this;

        StringTokenizer tokenizer = new StringTokenizer(path, CDOURIUtil.SEGMENT_SEPARATOR);
        while (tokenizer.hasMoreTokens())
        {
          String name = tokenizer.nextToken().trim();
          if (!StringUtil.isEmpty(name))
          {
            folder = getOrAddFolder(name);
          }
        }

        return folder;
      }

      public ImportFolder getOrAddFolder(String name)
      {
        return addChild(new ImportFolder(this, name));
      }

      public ImportResource addResource(String name)
      {
        return addChild(new ImportResource(this, name));
      }

      public ImportBinary addBinary(String name)
      {
        return addChild(new ImportBinary(this, name));
      }

      public ImportText addText(String name, String encoding)
      {
        return addChild(new ImportText(this, name, encoding));
      }

      private <E extends ImportElement> E addChild(E child)
      {
        if (isLeaf())
        {
          throw new IllegalStateException("Children not supported by " + getType());
        }

        if (children == null)
        {
          children = new ArrayList<>(1);
        }
        else if (child.getType() != Type.FOLDER)
        {
          String name = child.getName();
          for (ImportElement c : children)
          {
            if (c.getName().equals(name))
            {
              throw new IllegalStateException("A child with the name '" + name + "' already exists: " + c);
            }
          }
        }

        children.add(child);
        return child;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static abstract class ImportLeaf<CONTENTS> extends ImportElement
    {
      private UnaryOperator<CONTENTS> contentsModifier;

      private String copyPath;

      private ImportLeaf(ImportFolder parent, String name)
      {
        super(parent, name);
        copyPath = getPath();
      }

      @Override
      public boolean isFolder()
      {
        return false;
      }

      public UnaryOperator<CONTENTS> getContentsModifier()
      {
        return contentsModifier;
      }

      public ImportLeaf<CONTENTS> setContentsModifier(UnaryOperator<CONTENTS> contentsModifier)
      {
        this.contentsModifier = contentsModifier;
        return this;
      }

      public String getCopyPath()
      {
        return copyPath;
      }

      public ImportLeaf<CONTENTS> setCopyPath(String copyPath)
      {
        this.copyPath = copyPath;
        return this;
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class ImportResource extends ImportLeaf<EList<EObject>>
    {
      private Resource resource;

      private ImportResource(ImportFolder parent, String name)
      {
        super(parent, name);
      }

      @Override
      public Type getType()
      {
        return Type.RESOURCE;
      }

      public Resource getResource()
      {
        return resource;
      }

      @Override
      public ImportResource setContentsModifier(UnaryOperator<EList<EObject>> contentsModifier)
      {
        return (ImportResource)super.setContentsModifier(contentsModifier);
      }

      @Override
      public ImportResource setCopyPath(String copyPath)
      {
        return (ImportResource)super.setCopyPath(copyPath);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class ImportBinary extends ImportLeaf<InputStream>
    {
      private ImportBinary(ImportFolder parent, String name)
      {
        super(parent, name);
      }

      @Override
      public Type getType()
      {
        return Type.BINARY;
      }

      @Override
      public ImportBinary setContentsModifier(UnaryOperator<InputStream> contentsModifier)
      {
        return (ImportBinary)super.setContentsModifier(contentsModifier);
      }

      @Override
      public ImportBinary setCopyPath(String copyPath)
      {
        return (ImportBinary)super.setCopyPath(copyPath);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class ImportText extends ImportLeaf<Reader>
    {
      private final String encoding;

      private ImportText(ImportFolder parent, String name, String encoding)
      {
        super(parent, name);
        this.encoding = encoding;
      }

      @Override
      public Type getType()
      {
        return Type.TEXT;
      }

      public String getEncoding()
      {
        return encoding;
      }

      @Override
      public ImportText setContentsModifier(UnaryOperator<Reader> contentsModifier)
      {
        return (ImportText)super.setContentsModifier(contentsModifier);
      }

      public ImportText setStringContentsModifier(UnaryOperator<String> stringModifier)
      {
        return (ImportText)super.setContentsModifier(new StringContentsModifier(stringModifier));
      }

      @Override
      public ImportText setCopyPath(String copyPath)
      {
        return (ImportText)super.setCopyPath(copyPath);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class StringContentsModifier implements UnaryOperator<Reader>
    {
      private final UnaryOperator<String> stringModifier;

      public StringContentsModifier(UnaryOperator<String> stringModifier)
      {
        this.stringModifier = stringModifier;
      }

      @Override
      public Reader apply(Reader in)
      {
        String text = IOUtil.readText(in);
        text = stringModifier.apply(text);
        return new StringReader(text);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public final class ImportResolution
  {
    private final ResourceSet resourceSet;

    private final Map<EObject, URI> externalReferences;

    private final List<ModuleInfo> moduleInfos = new ArrayList<>();

    private ImportResolution(ResourceSet resourceSet, Map<EObject, URI> externalReferences)
    {
      this.resourceSet = resourceSet;
      this.externalReferences = externalReferences;

      for (ImportModule module : CollectionUtil.topologicalSort(modules.values(), ImportModule::getDependencies, true))
      {
        ModuleDefinition moduleDefinition = ModulesFactory.eINSTANCE.createModuleDefinition();
        moduleDefinition.setName(module.getName());
        moduleDefinition.setVersion(Version.createOSGi(0, 1, 0));

        for (ImportModule dependency : module.getDependencies())
        {
          DependencyDefinition dependencyDefinition = ModulesFactory.eINSTANCE.createDependencyDefinition();
          dependencyDefinition.setTargetName(dependency.getName());
          dependencyDefinition.setVersionRange(VersionRange.emptyRange);
          moduleDefinition.getDependencies().add(dependencyDefinition);
        }

        StreamSpec streamSpec = new StreamSpec(0, 1, "Initial");
        moduleInfos.add(new ModuleInfo(module, moduleDefinition, streamSpec));
      }
    }

    public ResourceSet getResourceSet()
    {
      return resourceSet;
    }

    public Map<EObject, URI> getExternalReferences()
    {
      return Collections.unmodifiableMap(externalReferences);
    }

    public List<ModuleInfo> getModuleInfos()
    {
      return Collections.unmodifiableList(moduleInfos);
    }

    public ModuleInfo getModuleInfo(ImportModule module)
    {
      for (ModuleInfo moduleInfo : moduleInfos)
      {
        if (moduleInfo.getModule() == module)
        {
          return moduleInfo;
        }
      }

      return null;
    }

    public List<Module> importModules(ISystemDescriptor systemDescriptor) throws ConcurrentAccessException, CommitException, ResolutionException, IOException
    {
      Set<String> moduleNames = new HashSet<>(Arrays.asList(systemDescriptor.getModuleNames()));

      for (ModuleInfo moduleInfo : moduleInfos)
      {
        String moduleName = moduleInfo.getModule().getName();
        if (moduleNames.contains(moduleName))
        {
          throw new IllegalStateException("Module already exists: " + moduleName);
        }
      }

      List<Module> result = new ArrayList<>();
      Map<EObject, Pair<ModuleInfo, CDOID>> objectMappings = new HashMap<>();

      for (ModuleInfo moduleInfo : moduleInfos)
      {
        Module module = importModule(systemDescriptor, objectMappings, moduleInfo);
        result.add(module);
      }

      return result;
    }

    private Module importModule(ISystemDescriptor systemDescriptor, Map<EObject, Pair<ModuleInfo, CDOID>> objectMappings, ModuleInfo moduleInfo)
        throws ConcurrentAccessException, CommitException, ResolutionException
    {
      ModuleDefinition moduleDefinition = moduleInfo.getModuleDefinition();
      String moduleName = moduleDefinition.getName();
      ModuleType moduleType = moduleInfo.getModuleType();
      StreamSpec streamSpec = moduleInfo.getStreamSpec();

      Module module = systemDescriptor.createModule(moduleName, moduleType, streamSpec, new NullProgressMonitor());
      Stream stream = module.getStreams().get(0);

      CDOTransaction transaction = null;

      try
      {
        ResourceSet moduleResourceSet = systemDescriptor.createModuleResourceSet(stream);
        transaction = (CDOTransaction)CDOUtil.getView(moduleResourceSet);

        String moduleDefinitionPath = stream.getSystem().getProcess().getModuleDefinitionPath();
        CDOResource moduleDefinitionResource = transaction.getOrCreateResource(moduleDefinitionPath);

        EList<EObject> contents = moduleDefinitionResource.getContents();
        contents.clear();
        contents.add(moduleDefinition);

        transaction.commit();
      }
      finally
      {
        LifecycleUtil.deactivate(transaction);
        transaction = null;
      }

      try
      {
        ResourceSet moduleResourceSet = systemDescriptor.createModuleResourceSet(stream);
        CDOViewSet viewSet = CDOUtil.getViewSet(moduleResourceSet);
        transaction = (CDOTransaction)viewSet.getViews()[0];

        ModuleCopier copier = new ModuleCopier(objectMappings, transaction);

        moduleInfo.getModule().accept(element -> {
          Type type = element.getType();

          switch (type)
          {
          case FOLDER:
            // Do nothing.
            break;

          case RESOURCE:
            importResource(copier, (ImportResource)element);
            break;

          case BINARY:
            importBinary(copier, (ImportBinary)element);
            break;

          case TEXT:
            importText(copier, (ImportText)element);
            break;

          default:
            throw new IllegalStateException("Illegal element type: " + type);
          }
        });

        copier.copyReferences();
        CDOCommitInfo commitInfo = transaction.commit(new NullProgressMonitor());
        copier.recordObjectMappings(moduleInfo);

        long timeStamp = commitInfo.getTimeStamp();
        if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
        {
          CDOCommitInfoManager commitInfoManager = transaction.getSession().getCommitInfoManager();
          CDOBranch branch = transaction.getBranch();
          timeStamp = commitInfoManager.getLastCommitOfBranch(branch, true);
        }

        DropCreator dropCreator = moduleInfo.getDropCreator();
        if (dropCreator != null)
        {
          dropCreator.createDrop(systemDescriptor, stream, timeStamp, new NullProgressMonitor());
        }
      }
      finally
      {
        LifecycleUtil.deactivate(transaction);
      }

      return module;
    }

    private CDOResource importResource(ModuleCopier copier, ImportResource element)
    {
      EList<EObject> contents = element.getResource().getContents();

      UnaryOperator<EList<EObject>> contentsModifier = element.getContentsModifier();
      if (contentsModifier != null)
      {
        EList<EObject> modifiedContents = contentsModifier.apply(contents);
        if (modifiedContents != null)
        {
          contents = modifiedContents;
        }
      }

      Collection<EObject> copyContents = copier.copyAll(contents);

      String path = element.getCopyPath();
      CDOResource result = copier.getTransaction().createResource(path);
      result.getContents().addAll(copyContents);
      return result;

    }

    private CDOBinaryResource importBinary(ModuleCopier copier, ImportBinary element)
    {
      try
      {
        URIConverter uriConverter = resourceSet.getURIConverter();
        URI uri = element.getURI();
        InputStream inputStream = new BufferedInputStream(uriConverter.createInputStream(uri));

        UnaryOperator<InputStream> contentsModifier = element.getContentsModifier();
        if (contentsModifier != null)
        {
          InputStream modifiedInputStream = contentsModifier.apply(inputStream);
          if (modifiedInputStream != null)
          {
            inputStream = modifiedInputStream;
          }
        }

        String path = element.getCopyPath();
        CDOBinaryResource result = copier.getTransaction().createBinaryResource(path);
        result.setContents(new CDOBlob(inputStream));
        return result;
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    private CDOTextResource importText(ModuleCopier copier, ImportText element)
    {
      try
      {
        URIConverter uriConverter = resourceSet.getURIConverter();
        URI uri = element.getURI();
        String encoding = element.getEncoding();
        Reader reader = new BufferedReader(new InputStreamReader(uriConverter.createInputStream(uri), encoding));

        UnaryOperator<Reader> contentsModifier = element.getContentsModifier();
        if (contentsModifier != null)
        {
          Reader modifiedReader = contentsModifier.apply(reader);
          if (modifiedReader != null)
          {
            reader = modifiedReader;
          }
        }

        String path = element.getCopyPath();
        CDOTextResource result = copier.getTransaction().createTextResource(path);
        result.setEncoding(encoding);
        result.setContents(new CDOClob(reader));
        return result;
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }
    }

    /**
     * @author Eike Stepper
     */
    public final class ModuleInfo
    {
      private final ImportModule module;

      private ModuleType moduleType;

      private ModuleDefinition moduleDefinition;

      private StreamSpec streamSpec;

      private DropCreator dropCreator = DropCreator.DEFAULT;

      private ModuleInfo(ImportModule module, ModuleDefinition moduleDefinition, StreamSpec streamSpec)
      {
        this.module = module;
        this.moduleDefinition = moduleDefinition;
        this.streamSpec = streamSpec;
      }

      public ImportModule getModule()
      {
        return module;
      }

      public ModuleType getModuleType()
      {
        return moduleType;
      }

      public void setModuleType(ModuleType moduleType)
      {
        this.moduleType = moduleType;
      }

      public ModuleDefinition getModuleDefinition()
      {
        return moduleDefinition;
      }

      public void setModuleDefinition(ModuleDefinition moduleDefinition)
      {
        this.moduleDefinition = moduleDefinition;
      }

      public StreamSpec getStreamSpec()
      {
        return streamSpec;
      }

      public void setStreamSpec(StreamSpec streamSpec)
      {
        this.streamSpec = streamSpec;
      }

      public DropCreator getDropCreator()
      {
        return dropCreator;
      }

      public void setDropCreator(DropCreator dropCreator)
      {
        this.dropCreator = dropCreator;
      }

      @Override
      public String toString()
      {
        return module.getName();
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class ModuleCopier extends Copier
    {
      private static final long serialVersionUID = 1L;

      private final Map<EObject, Pair<ModuleInfo, CDOID>> objectMappings;

      private final CDOTransaction transaction;

      public ModuleCopier(Map<EObject, Pair<ModuleInfo, CDOID>> objectMappings, CDOTransaction transaction)
      {
        super(true, false);
        this.objectMappings = objectMappings;
        this.transaction = transaction;
      }

      public CDOTransaction getTransaction()
      {
        return transaction;
      }

      /**
       * Record the object mappings for the import of dependent modules.
       */
      public void recordObjectMappings(ModuleInfo moduleInfo)
      {
        for (Map.Entry<EObject, EObject> entry : entrySet())
        {
          EObject eObject = entry.getKey();
          CDOObject copyObject = (CDOObject)entry.getValue();

          objectMappings.put(eObject, Pair.create(moduleInfo, copyObject.cdoID()));
        }
      }

      /**
       * This method is only called during {@link #copyReferences()}.
       */
      @Override
      public EObject get(Object key)
      {
        Pair<ModuleInfo, CDOID> mapping = objectMappings.get(key);
        if (mapping != null)
        {
          ModuleInfo elementModuleInfo = mapping.getElement1();
          CDOID elementID = mapping.getElement2();

          CDOView view = getViewSafe(elementModuleInfo);
          return view.getObject(elementID);
        }

        URI handledURI = externalReferences.get(key);
        if (handledURI != null)
        {
          if (handledURI == EXTERNAL_REFERENCE_UNSET_MARKER)
          {
            return null;
          }

          ResourceSet targetResourceSet = transaction.getResourceSet();
          EObject targetObject = targetResourceSet.getEObject(handledURI, true);
          if (targetObject != null)
          {
            return targetObject;
          }
        }

        return super.get(key);
      }

      private CDOView getViewSafe(ModuleInfo moduleInfo)
      {
        String name = moduleInfo.getModule().getName();
        CDOView[] views = transaction.getViewSet().getViews();

        for (int i = 1; i < views.length; i++) // Skip primary view.
        {
          CDOView view = views[i];

          Object moduleName = view.getSession().properties().get(SystemDescriptor.KEY_MODULE_NAME);
          if (name.equals(moduleName))
          {
            return view;
          }
        }

        throw new IllegalStateException("View not found for module " + name);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  @FunctionalInterface
  public interface DropCreator
  {
    public static final DropCreator DEFAULT = (systemDescriptor, stream, timeStamp, monitor) -> {
      DropType dropType = getDefaultDropType(systemDescriptor);
      return systemDescriptor.createDrop(stream, dropType, timeStamp, "Initial Module Import", monitor);
    };

    public static DropType getDefaultDropType(ISystemDescriptor systemDescriptor)
    {
      DropType firstRelease = null;

      for (DropType dropType : systemDescriptor.getSystem().getProcess().getDropTypes())
      {
        if (dropType.isRelease())
        {
          if (firstRelease == null)
          {
            firstRelease = dropType;
          }
        }
        else
        {
          return dropType;
        }
      }

      return firstRelease;
    }

    public Drop createDrop(ISystemDescriptor systemDescriptor, Stream stream, long timeStamp, IProgressMonitor monitor)
        throws ConcurrentAccessException, CommitException;
  }
}
