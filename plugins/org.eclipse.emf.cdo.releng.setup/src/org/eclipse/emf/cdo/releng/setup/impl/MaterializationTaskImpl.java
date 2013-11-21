/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.Component;
import org.eclipse.emf.cdo.releng.setup.ComponentType;
import org.eclipse.emf.cdo.releng.setup.ManualSourceLocator;
import org.eclipse.emf.cdo.releng.setup.MaterializationTask;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.SetupTaskContext;
import org.eclipse.emf.cdo.releng.setup.SourceLocator;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.buckminster.core.cspec.builder.CSpecBuilder;
import org.eclipse.buckminster.core.cspec.builder.ComponentRequestBuilder;
import org.eclipse.buckminster.core.cspec.model.CSpec;
import org.eclipse.buckminster.core.query.builder.ComponentQueryBuilder;
import org.eclipse.buckminster.core.query.model.ComponentQuery;
import org.eclipse.buckminster.model.common.CommonFactory;
import org.eclipse.buckminster.model.common.Format;
import org.eclipse.buckminster.model.common.PropertyRef;
import org.eclipse.buckminster.mspec.MaterializationNode;
import org.eclipse.buckminster.mspec.MaterializationSpec;
import org.eclipse.buckminster.mspec.MspecFactory;
import org.eclipse.buckminster.osgi.filter.FilterFactory;
import org.eclipse.buckminster.rmap.Locator;
import org.eclipse.buckminster.rmap.Matcher;
import org.eclipse.buckminster.rmap.Provider;
import org.eclipse.buckminster.rmap.ResourceMap;
import org.eclipse.buckminster.rmap.RmapFactory;
import org.eclipse.buckminster.rmap.SearchPath;
import org.eclipse.buckminster.sax.Utils;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.p2.metadata.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Materialization Task</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl#getRootComponents <em>Root Components</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl#getSourceLocators <em>Source Locators</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.MaterializationTaskImpl#getP2Repositories <em>P2 Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MaterializationTaskImpl extends BasicMaterializationTaskImpl implements MaterializationTask
{
  /**
   * The cached value of the '{@link #getRootComponents() <em>Root Components</em>}' containment reference list.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getRootComponents()
   * @generated
   * @ordered
   */
  protected EList<Component> rootComponents;

  /**
   * The cached value of the '{@link #getSourceLocators() <em>Source Locators</em>}' containment reference list.
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @see #getSourceLocators()
   * @generated
   * @ordered
   */
  protected EList<SourceLocator> sourceLocators;

  /**
   * The cached value of the '{@link #getP2Repositories() <em>P2 Repositories</em>}' containment reference list.
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @see #getP2Repositories()
   * @generated
   * @ordered
   */
  protected EList<P2Repository> p2Repositories;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MaterializationTaskImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.MATERIALIZATION_TASK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<SourceLocator> getSourceLocators()
  {
    if (sourceLocators == null)
    {
      sourceLocators = new EObjectContainmentEList.Resolving<SourceLocator>(SourceLocator.class, this,
          SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS);
    }
    return sourceLocators;
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  public EList<Component> getRootComponents()
  {
    if (rootComponents == null)
    {
      rootComponents = new EObjectContainmentEList.Resolving<Component>(Component.class, this,
          SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS);
    }
    return rootComponents;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<P2Repository> getP2Repositories()
  {
    if (p2Repositories == null)
    {
      p2Repositories = new EObjectContainmentEList.Resolving<P2Repository>(P2Repository.class, this,
          SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES);
    }
    return p2Repositories;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS:
      return ((InternalEList<?>)getRootComponents()).basicRemove(otherEnd, msgs);
    case SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS:
      return ((InternalEList<?>)getSourceLocators()).basicRemove(otherEnd, msgs);
    case SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES:
      return ((InternalEList<?>)getP2Repositories()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS:
      return getRootComponents();
    case SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS:
      return getSourceLocators();
    case SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES:
      return getP2Repositories();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS:
      getRootComponents().clear();
      getRootComponents().addAll((Collection<? extends Component>)newValue);
      return;
    case SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS:
      getSourceLocators().clear();
      getSourceLocators().addAll((Collection<? extends SourceLocator>)newValue);
      return;
    case SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      getP2Repositories().addAll((Collection<? extends P2Repository>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS:
      getRootComponents().clear();
      return;
    case SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS:
      getSourceLocators().clear();
      return;
    case SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES:
      getP2Repositories().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.MATERIALIZATION_TASK__ROOT_COMPONENTS:
      return rootComponents != null && !rootComponents.isEmpty();
    case SetupPackage.MATERIALIZATION_TASK__SOURCE_LOCATORS:
      return sourceLocators != null && !sourceLocators.isEmpty();
    case SetupPackage.MATERIALIZATION_TASK__P2_REPOSITORIES:
      return p2Repositories != null && !p2Repositories.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public Object getOverrideToken()
  {
    return getClass();
  }

  @Override
  public void overrideFor(SetupTask overriddenSetupTask)
  {
    super.overrideFor(overriddenSetupTask);

    MaterializationTask overriddenMaterializationTask = (MaterializationTask)overriddenSetupTask;
    getRootComponents().addAll(overriddenMaterializationTask.getRootComponents());
    getSourceLocators().addAll(overriddenMaterializationTask.getSourceLocators());
    getP2Repositories().addAll(overriddenMaterializationTask.getP2Repositories());
  }

  @Override
  protected String getMspec(SetupTaskContext context) throws Exception
  {
    return BuckminsterHelper.getMspec(context, getRootComponents(), getSourceLocators(), getP2Repositories());
  }

  private static class BuckminsterHelper
  {
    public static String getMspec(SetupTaskContext context, EList<Component> rootComponents,
        EList<SourceLocator> sourceLocators, EList<P2Repository> p2Repositories) throws Exception
    {
      File workspaceDir = context.getWorkspaceDir();
      File buckminsterFolder = new File(workspaceDir, ".buckminster");
      URI mspecURI = URI.createFileURI(new File(buckminsterFolder, "buckminster.mspec").toString());

      MaterializationSpec mspec = MspecFactory.eINSTANCE.createMaterializationSpec();

      mspec.setInstallLocation(new Path(""));
      mspec.setMaterializer("p2");
      mspec.setName("buckminster.mspec");
      mspec.setUrl("buckminster.cquery");

      Map<String, String> properties = mspec.getProperties();
      // properties.put("target.os", "*");
      // properties.put("target.ws", "*");
      // properties.put("target.arch", "*");
      properties.put("target.os", Platform.getOS());
      properties.put("target.ws", Platform.getWS());
      properties.put("target.arch", Platform.getOSArch());
      properties.put("buckminster.download.source", "true");

      MaterializationNode materializationNode = MspecFactory.eINSTANCE.createMaterializationNode();
      materializationNode.setMaterializer("workspace");
      materializationNode.setFilter(FilterFactory.newInstance("(buckminster.source=true)"));
      mspec.getMspecNodes().add(materializationNode);

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource mspecResource = resourceSet.createResource(mspecURI);
      mspecResource.getContents().add(mspec);
      mspecResource.save(null);

      ComponentRequestBuilder componentRequestBuilder = new ComponentRequestBuilder();
      componentRequestBuilder.setName(".buckminster");
      componentRequestBuilder.setComponentTypeID("buckminster");

      ComponentQueryBuilder componentQueryBuilder = new ComponentQueryBuilder();
      componentQueryBuilder.setResourceMapURL("buckminster.rmap");
      componentQueryBuilder.setRootRequest(componentRequestBuilder.createComponentRequest());

      Map<String, String> declaredProperties = componentQueryBuilder.getDeclaredProperties();
      declaredProperties.put("target.os", Platform.getOS());
      declaredProperties.put("target.ws", Platform.getWS());
      declaredProperties.put("target.arch", Platform.getOSArch());
      // declaredProperties.put("target.os", "*");
      // declaredProperties.put("target.ws", "*");
      // declaredProperties.put("target.arch", "*");

      ComponentQuery componentQuery = componentQueryBuilder.createComponentQuery();

      FileOutputStream cqueryOutputStream = null;
      try
      {
        cqueryOutputStream = new FileOutputStream(new File(buckminsterFolder, "buckminster.cquery"));
        Utils.serialize(componentQuery, cqueryOutputStream);
      }
      finally
      {
        IOUtil.close(cqueryOutputStream);
      }

      CSpecBuilder cspecBuilder = new CSpecBuilder();
      cspecBuilder.setComponentTypeID("buckminster");
      cspecBuilder.setName(".buckminster");
      cspecBuilder.setVersion(Version.create("1.0.0"));

      for (Component rootComponent : rootComponents)
      {
        ComponentRequestBuilder rootComponentRequestBuilder = new ComponentRequestBuilder();
        rootComponentRequestBuilder.setName(rootComponent.getName());
        rootComponentRequestBuilder.setComponentTypeID(rootComponent.getType().toString());
        cspecBuilder.addDependency(rootComponentRequestBuilder.createComponentRequest());
      }

      CSpec cspec = cspecBuilder.createCSpec();

      FileOutputStream cspecOutputStream = null;
      try
      {
        cspecOutputStream = new FileOutputStream(new File(buckminsterFolder, "buckminster.cspec"));
        Utils.serialize(cspec, cspecOutputStream);
      }
      finally
      {
        IOUtil.close(cspecOutputStream);
      }

      ResourceMap rmap = RmapFactory.eINSTANCE.createResourceMap();
      EList<Matcher> matchers = rmap.getMatchers();

      SearchPath buckminsterSearchPath = RmapFactory.eINSTANCE.createSearchPath();
      buckminsterSearchPath.setName("buckminster");
      EList<Provider> buckminsterProviders = buckminsterSearchPath.getProviders();
      Provider buckminsterProvider = RmapFactory.eINSTANCE.createProvider();
      buckminsterProvider.setComponentTypesAttr("buckminster");
      buckminsterProvider.setReaderType("local");
      buckminsterProvider.setSource(false);

      Format buckminsterFormat = CommonFactory.eINSTANCE.createFormat();
      buckminsterFormat.setFormat(buckminsterFolder.toString());
      buckminsterProvider.setURI(buckminsterFormat);
      buckminsterProviders.add(buckminsterProvider);

      Locator buckminsterLocator = RmapFactory.eINSTANCE.createLocator();
      buckminsterLocator.setSearchPath(buckminsterSearchPath);
      buckminsterLocator.setPattern(Pattern.compile("\\.buckminster"));
      buckminsterLocator.setFailOnError(true);
      matchers.add(buckminsterLocator);
      rmap.getSearchPaths().add(buckminsterSearchPath);

      if (!sourceLocators.isEmpty())
      {
        int sourceProviderIndex = 0;
        HashMap<String, List<ComponentLocation>> componentMap = new HashMap<String, List<ComponentLocation>>();
        for (SourceLocator sourceLocator : sourceLocators)
        {
          if (sourceLocator instanceof ManualSourceLocator)
          {
            SearchPath sourceSearchPath = RmapFactory.eINSTANCE.createSearchPath();
            sourceSearchPath.setName("sources_" + sourceProviderIndex++);
            EList<Provider> sourceProviders = sourceSearchPath.getProviders();

            ManualSourceLocator manualSourceLocator = (ManualSourceLocator)sourceLocator;
            String locationPattern = manualSourceLocator.getLocation();
            locationPattern = locationPattern.replace("*", "{0}");
            if (locationPattern.indexOf("{0}") == -1)
            {
              if (!locationPattern.endsWith("/") && !locationPattern.endsWith("\\"))
              {
                locationPattern += File.separator;
              }

              locationPattern += "{0}";
            }

            Provider provider = RmapFactory.eINSTANCE.createProvider();

            StringBuilder componentTypes = new StringBuilder();
            for (ComponentType componentType : manualSourceLocator.getComponentTypes())
            {
              if (componentTypes.length() != 0)
              {
                componentTypes.append(',');
              }

              componentTypes.append(componentType.toString());
            }

            provider.setComponentTypesAttr(componentTypes.toString());
            provider.setReaderType("local");
            provider.setSource(true);

            Format format = CommonFactory.eINSTANCE.createFormat();
            format.setFormat(locationPattern);
            PropertyRef propertyRef = CommonFactory.eINSTANCE.createPropertyRef();
            propertyRef.setKey("buckminster.component");
            format.getValues().add(propertyRef);
            provider.setURI(format);
            sourceProviders.add(provider);

            Locator locator = RmapFactory.eINSTANCE.createLocator();
            String componentNamePattern = manualSourceLocator.getComponentNamePattern();
            if (!StringUtil.isEmpty(componentNamePattern))
            {
              locator.setPattern(Pattern.compile(componentNamePattern));
            }

            locator.setSearchPath(sourceSearchPath);
            locator.setFailOnError(p2Repositories.isEmpty()); // TODO
            matchers.add(locator);

            rmap.getSearchPaths().add(sourceSearchPath);
          }
          else
          {
            AutomaticSourceLocator automaticSourceLocator = (AutomaticSourceLocator)sourceLocator;
            automaticSourceLocator.getRootFolder();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            analyze(componentMap, documentBuilder, new File(automaticSourceLocator.getRootFolder()));
          }
        }

        for (Map.Entry<String, List<ComponentLocation>> entry : componentMap.entrySet())
        {
          String componentName = entry.getKey();

          SearchPath sourceSearchPath = RmapFactory.eINSTANCE.createSearchPath();
          sourceSearchPath.setName("sources_" + componentName);
          EList<Provider> sourceProviders = sourceSearchPath.getProviders();

          for (ComponentLocation componentLocation : entry.getValue())
          {
            Provider provider = RmapFactory.eINSTANCE.createProvider();

            provider.setComponentTypesAttr(componentLocation.componentType.toString());
            provider.setReaderType("local");
            provider.setSource(true);

            Format format = CommonFactory.eINSTANCE.createFormat();
            format.setFormat(componentLocation.location);
            provider.setURI(format);

            sourceProviders.add(provider);
          }

          Locator locator = RmapFactory.eINSTANCE.createLocator();
          locator.setPattern(Pattern.compile("^" + Pattern.quote(componentName) + "$"));

          locator.setSearchPath(sourceSearchPath);
          locator.setFailOnError(true);
          matchers.add(locator);

          rmap.getSearchPaths().add(sourceSearchPath);
        }
      }

      if (!p2Repositories.isEmpty())
      {
        SearchPath p2SearchPath = RmapFactory.eINSTANCE.createSearchPath();
        p2SearchPath.setName("p2");
        EList<Provider> p2Providers = p2SearchPath.getProviders();
        for (P2Repository p2Repository : p2Repositories)
        {
          String url = context.redirect(URI.createURI(p2Repository.getURL())).toString();
          Provider provider = RmapFactory.eINSTANCE.createProvider();
          provider.setComponentTypesAttr("eclipse.feature,osgi.bundle");
          provider.setReaderType("p2");
          provider.setSource(false);
          provider.setMutable(false);

          Format format = CommonFactory.eINSTANCE.createFormat();
          format.setFormat(url);
          provider.setURI(format);
          p2Providers.add(provider);
        }

        Locator p2Locator = RmapFactory.eINSTANCE.createLocator();
        p2Locator.setSearchPath(p2SearchPath);
        matchers.add(p2Locator);
        rmap.getSearchPaths().add(p2SearchPath);
      }

      Resource rmapResource = resourceSet.createResource(mspecURI.trimSegments(1).appendSegment("buckminster.rmap"));
      rmapResource.getContents().add(rmap);
      rmapResource.save(null);

      return mspecURI.toString();
    }

    private static class ComponentLocation
    {
      public ComponentType componentType;

      public String location;

      public ComponentLocation(ComponentType componentType, String location)
      {
        this.componentType = componentType;
        this.location = location;
      }
    }

    private static void analyze(Map<String, List<ComponentLocation>> componentMap, DocumentBuilder documentBuilder,
        File folder)
    {
      File projectFile = new File(folder, ".project");
      if (projectFile.exists())
      {
        try
        {
          String componentName = null;
          ComponentType componentType = null;

          File manifestFile = new File(folder, "META-INF/MANIFEST.MF");
          if (manifestFile.exists())
          {
            FileInputStream manifestFileInputStream = null;

            try
            {
              manifestFileInputStream = new FileInputStream(manifestFile);
              Manifest manifest = new Manifest(manifestFileInputStream);
              String bundleSymbolicName = manifest.getMainAttributes().getValue("Bundle-SymbolicName").trim();
              int index = bundleSymbolicName.indexOf(';');
              if (index != -1)
              {
                bundleSymbolicName = bundleSymbolicName.substring(0, index).trim();
              }

              componentName = bundleSymbolicName;
              componentType = ComponentType.OSGI_BUNDLE;
            }
            catch (IOException ex)
            {
              Activator.log(ex);
            }
            finally
            {
              IOUtil.close(manifestFileInputStream);
            }
          }
          else
          {
            File featureXMLFile = new File(folder, "feature.xml");
            if (featureXMLFile.exists())
            {
              try
              {
                Element rootElement = load(documentBuilder, featureXMLFile);
                componentName = rootElement.getAttribute("id").trim();
                componentType = ComponentType.ECLIPSE_FEATURE;
              }
              catch (Exception ex)
              {
                Activator.log(ex);
              }
            }
            else
            {
              File cspecFile = new File(folder, "buckminster.cspec");
              if (cspecFile.exists())
              {
                Element rootElement = load(documentBuilder, cspecFile);
                componentName = rootElement.getAttribute("name").trim();
                componentType = ComponentType.BUCKMINSTER;
              }
            }
          }

          if (componentName != null)
          {
            List<ComponentLocation> locations = componentMap.get(componentName);
            if (locations == null)
            {
              locations = new ArrayList<ComponentLocation>();
              componentMap.put(componentName, locations);
            }

            locations.add(new ComponentLocation(componentType, folder.toString()));
          }
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }

      File[] listFiles = folder.listFiles();
      if (listFiles != null)
      {
        for (File file : listFiles)
        {
          if (file.isDirectory())
          {
            analyze(componentMap, documentBuilder, file);
          }
        }
      }
    }

    private static Element load(DocumentBuilder documentBuilder, File file) throws Exception
    {
      Document document = documentBuilder.parse(file);
      return document.getDocumentElement();
    }
  }
} // MaterializationTaskImpl
