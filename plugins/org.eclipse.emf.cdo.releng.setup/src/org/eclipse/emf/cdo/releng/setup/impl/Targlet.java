/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.releng.internal.setup.ui.ResourceManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.artifact.repository.ArtifactRepositoryManager;
import org.eclipse.equinox.internal.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.p2.director.app.DirectorApplication.AvoidTrustPromptService;
import org.eclipse.equinox.internal.p2.metadata.repository.MetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.director.PlanExecutionHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.core.UIServices;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IVersionedId;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionedId;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetLocationFactory;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.target.AbstractBundleContainer;
import org.eclipse.pde.internal.core.target.Messages;
import org.eclipse.pde.internal.core.target.TargetPlatformService;
import org.eclipse.pde.internal.ui.SWTFactory;
import org.eclipse.pde.internal.ui.shared.target.EditBundleContainerWizard;
import org.eclipse.pde.internal.ui.shared.target.IEditBundleContainerPage;
import org.eclipse.pde.internal.ui.shared.target.StyledBundleLabelProvider;
import org.eclipse.pde.ui.target.ITargetLocationEditor;
import org.eclipse.pde.ui.target.ITargetLocationUpdater;
import org.eclipse.pde.ui.target.ITargetLocationWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class Targlet extends AbstractBundleContainer
{
  public static final String TYPE = "Targlet"; //$NON-NLS-1$

  private static final String PROP_P2_PROFILE = "eclipse.p2.profile";

  private static final String FOLLOW_ARTIFACT_REPOSITORY_REFERENCES = "org.eclipse.equinox.p2.director.followArtifactRepositoryReferences"; //$NON-NLS-1$

  private static final String TRUE = Boolean.TRUE.toString();

  private static final String FALSE = Boolean.FALSE.toString();

  private File bundlePoolDir;

  private File agentDir;

  private String profileID;

  private transient IProfile profile;

  private transient IProvisioningAgent agent;

  private transient IInstallableUnit[] units;

  public Targlet(File bundlePoolDir, File agentDir, IProfile profile)
  {
    this(bundlePoolDir, agentDir, profile.getProfileId());
    this.profile = profile;
  }

  /*
   * Must be quick; is called multiple times during working copy creation.
   */
  public Targlet(File bundlePoolDir, File agentDir, String profileID)
  {
    this.bundlePoolDir = bundlePoolDir;
    this.agentDir = agentDir;
    this.profileID = profileID;
  }

  @Override
  public String getType()
  {
    return TYPE;
  }

  @Override
  public String getLocation(boolean resolve) throws CoreException
  {
    return new File(agentDir, "org.eclipse.equinox.p2.engine/profileRegistry/" + profileID + ".profile")
        .getAbsolutePath();
  }

  public File getBundlePoolDir()
  {
    return bundlePoolDir;
  }

  public File getAgentDir()
  {
    return agentDir;
  }

  public String getProfileID()
  {
    return profileID;
  }

  @Override
  public String serialize()
  {
    Element containerElement;
    Document document;

    try
    {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      document = docBuilder.newDocument();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      return null;
    }

    containerElement = document.createElement("location");
    containerElement.setAttribute("type", TYPE);
    containerElement.setAttribute("bundlePool", bundlePoolDir.getAbsolutePath());
    containerElement.setAttribute("agent", agentDir.getAbsolutePath());
    containerElement.setAttribute("profile", profileID);

    try
    {
      document.appendChild(containerElement);
      StreamResult result = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$
      transformer.transform(new DOMSource(document), result);
      return result.getWriter().toString();
    }
    catch (TransformerException ex)
    {
      return null;
    }
  }

  @Override
  public String toString()
  {
    return profileID;
  }

  /*
   * Is only called by content provider and only if this targlet is resolved.
   */
  public IInstallableUnit[] getInstallableUnits() throws CoreException
  {
    return units;
  }

  @Override
  protected TargetBundle[] resolveBundles(ITargetDefinition definition, IProgressMonitor monitor) throws CoreException
  {
    resolveUnits(definition, monitor);
    return fBundles;
  }

  @Override
  protected TargetFeature[] resolveFeatures(ITargetDefinition definition, IProgressMonitor monitor)
      throws CoreException
  {
    return fFeatures;
  }

  private void resolveUnits(ITargetDefinition definition, IProgressMonitor monitor) throws CoreException
  {
    try
    {
      IFileArtifactRepository bundlePool = getBundlePool();
      IProfile profile = getProfile();

      List<IInstallableUnit> units = new ArrayList<IInstallableUnit>();
      List<TargetBundle> bundles = new ArrayList<TargetBundle>();
      List<TargetFeature> features = new ArrayList<TargetFeature>();

      IQueryResult<IInstallableUnit> result = profile.query(QueryUtil.createIUAnyQuery(), null);
      for (Iterator<IInstallableUnit> i = result.iterator(); i.hasNext();)
      {
        IInstallableUnit unit = i.next();
        units.add(unit);

        if (isOSGiBundle(unit))
        {
          generateBundle(unit, bundlePool, bundles);
          // if (getIncludeSource())
          // {
          // // bit of a hack using the bundle naming convention for finding source bundles
          // // but this matches what we do when adding source to the profile so...
          //          IQuery<IInstallableUnit> sourceQuery = QueryUtil.createIUQuery(unit.getId() + ".source", unit.getVersion()); //$NON-NLS-1$
          // IQueryResult<IInstallableUnit> result = metadata.query(sourceQuery, null);
          // if (!result.isEmpty())
          // {
          // generateBundle(result.iterator().next(), artifacts, bundles);
          // }
          // }
        }
        else if (isFeatureJar(unit))
        {
          generateFeature(unit, bundlePool, features);
        }
      }

      this.units = units.toArray(new IInstallableUnit[units.size()]);
      fBundles = bundles.toArray(new TargetBundle[bundles.size()]);
      fFeatures = features.toArray(new TargetFeature[features.size()]);
    }
    catch (RuntimeException ex)
    {
      Activator.log(ex);
      throw ex;
    }
  }

  private void generateBundle(IInstallableUnit unit, IFileArtifactRepository repo, List<TargetBundle> bundles)
      throws CoreException
  {
    Collection<IArtifactKey> artifacts = unit.getArtifacts();
    for (Iterator<IArtifactKey> iterator2 = artifacts.iterator(); iterator2.hasNext();)
    {
      File file = repo.getArtifactFile(iterator2.next());
      if (file != null)
      {
        TargetBundle bundle = new TargetBundle(file);
        bundles.add(bundle);
      }
    }
  }

  private void generateFeature(IInstallableUnit unit, IFileArtifactRepository repo, List<TargetFeature> features)
      throws CoreException
  {
    Collection<IArtifactKey> artifacts = unit.getArtifacts();
    for (Iterator<IArtifactKey> iterator2 = artifacts.iterator(); iterator2.hasNext();)
    {
      File file = repo.getArtifactFile(iterator2.next());
      if (file != null)
      {
        TargetFeature feature = new TargetFeature(file);
        features.add(feature);
      }
    }
  }

  private boolean isOSGiBundle(IInstallableUnit unit)
  {
    return providesNamespace(unit, "osgi.bundle");
  }

  private boolean isFeatureJar(IInstallableUnit unit)
  {
    return providesNamespace(unit, "org.eclipse.update.feature");
  }

  private boolean providesNamespace(IInstallableUnit unit, String namespace)
  {
    for (IProvidedCapability providedCapability : unit.getProvidedCapabilities())
    {
      if (namespace.equals(providedCapability.getNamespace()))
      {
        return true;
      }
    }

    return false;
  }

  private IFileArtifactRepository getBundlePool() throws CoreException
  {
    URI uri = bundlePoolDir.toURI();
    IArtifactRepositoryManager manager = getArtifactRepositoryManager();

    try
    {
      if (manager.contains(uri))
      {
        return (IFileArtifactRepository)manager.loadRepository(uri, null);
      }
    }
    catch (CoreException e)
    {
      // could not load or there wasn't one, fall through to create
    }

    String repoName = "Shared Bundle Pool"; //$NON-NLS-1$
    IArtifactRepository result = manager.createRepository(uri, repoName,
        IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
    return (IFileArtifactRepository)result;
  }

  private IArtifactRepositoryManager getArtifactRepositoryManager() throws CoreException
  {
    IArtifactRepositoryManager manager = (IArtifactRepositoryManager)getAgent().getService(
        IArtifactRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new CoreException(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, Messages.IUBundleContainer_3));
    }

    return manager;
  }

  private IProfile getProfile() throws ProvisionException
  {
    if (profile == null)
    {
      IProfileRegistry profileRegistry = (IProfileRegistry)getAgent().getService(IProfileRegistry.SERVICE_NAME);
      if (profileRegistry == null)
      {
        throw new ProvisionException("Profile registry could not be loaded");
      }

      profile = profileRegistry.getProfile(profileID);
    }

    return profile;
  }

  private IProvisioningAgent getAgent() throws ProvisionException
  {
    if (agent == null)
    {
      agent = Targlet.createAgent(agentDir);
      agent.registerService(PROP_P2_PROFILE, profileID);
      agent.registerService(UIServices.SERVICE_NAME, new AvoidTrustPromptService());
    }

    return agent;
  }

  public static Targlet create(File p2PoolDir, File p2AgentDir, String profileID, String destination,
      URI[] p2Repositories, IVersionedId[] rootComponents, IProgressMonitor monitor) throws Exception
  {
    IProvisioningAgent agent = createAgent(p2AgentDir);
    agent.registerService(PROP_P2_PROFILE, profileID);
    agent.registerService(UIServices.SERVICE_NAME, new AvoidTrustPromptService());

    IProfileRegistry profileRegistry = (IProfileRegistry)agent.getService(IProfileRegistry.SERVICE_NAME);
    if (profileRegistry == null)
    {
      throw new ProvisionException("Profile registry could not be loaded");
    }

    profileRegistry.removeProfile(profileID);

    Map<String, String> props = new HashMap<String, String>();
    props.put(IProfile.PROP_INSTALL_FOLDER, destination); // XXX Doesn't seem to be used
    props.put(IProfile.PROP_CACHE, p2PoolDir.getAbsolutePath());
    props.put("org.eclipse.update.install.features", TRUE);

    IProfile profile = profileRegistry.addProfile(profileID, props);

    MetadataRepositoryManager metadataRepositoryManager = new MetadataRepositoryManager(agent);
    agent.registerService(IMetadataRepositoryManager.SERVICE_NAME, metadataRepositoryManager);
    for (URI uri : metadataRepositoryManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL))
    {
      metadataRepositoryManager.removeRepository(uri);
    }

    ArtifactRepositoryManager artifactRepositoryManager = new ArtifactRepositoryManager(agent);
    agent.registerService(IArtifactRepositoryManager.SERVICE_NAME, artifactRepositoryManager);
    for (URI uri : artifactRepositoryManager.getKnownRepositories(IRepositoryManager.REPOSITORIES_ALL))
    {
      artifactRepositoryManager.removeRepository(uri);
    }

    for (URI p2Repository : p2Repositories)
    {
      metadataRepositoryManager.addRepository(p2Repository);
      artifactRepositoryManager.addRepository(p2Repository);
    }

    ProvisioningContext provisioningContext = new ProvisioningContext(agent)
    {
      @Override
      public IQueryable<IInstallableUnit> getMetadata(IProgressMonitor monitor)
      {
        return super.getMetadata(monitor);
      }
    };

    provisioningContext.setMetadataRepositories(p2Repositories);
    provisioningContext.setArtifactRepositories(p2Repositories);
    provisioningContext.setProperty(ProvisioningContext.FOLLOW_REPOSITORY_REFERENCES, FALSE);
    provisioningContext.setProperty(FOLLOW_ARTIFACT_REPOSITORY_REFERENCES, FALSE);

    ProfileChangeRequest request = new ProfileChangeRequest(profile);

    for (IVersionedId rootComponent : rootComponents)
    {
      IQuery<IInstallableUnit> query = QueryUtil.createLatestQuery(QueryUtil.createIUQuery(rootComponent));

      for (IInstallableUnit installableUnit : metadataRepositoryManager.query(query, monitor))
      {
        request.setInstallableUnitProfileProperty(installableUnit, IProfile.PROP_PROFILE_ROOT_IU, TRUE);
        request.add(installableUnit);
      }
    }

    IPlanner planner = (IPlanner)agent.getService(IPlanner.SERVICE_NAME);
    if (planner == null)
    {
      throw new ProvisionException("Planner could not be loaded");
    }

    IProvisioningPlan result = planner.getProvisioningPlan(request, provisioningContext, new NullProgressMonitor());
    IStatus status1 = result.getStatus();
    if (!status1.isOK())
    {
      throw new CoreException(status1);
    }

    IEngine engine = (IEngine)agent.getService(IEngine.SERVICE_NAME);
    if (engine == null)
    {
      throw new ProvisionException("Engine could not be loaded");
    }

    IStatus status2 = PlanExecutionHelper.executePlan(result, engine, provisioningContext, new NullProgressMonitor());
    if (!status2.isOK())
    {
      throw new CoreException(status2);
    }

    // initTargetPlatform(p2PoolDir, p2AgentDir, profile);
    return new Targlet(p2PoolDir, p2AgentDir, profileID);
  }

  private static void initTargetPlatform(File bundlePoolDir, File agentDir, IProfile profile) throws Exception
  {
    TargetPlatformService targetService = (TargetPlatformService)PDECore.getDefault().acquireService(
        ITargetPlatformService.class.getName());

    ITargetLocation[] locations = { new Targlet(bundlePoolDir, agentDir, profile) };

    ITargetDefinition target = targetService.newTarget();
    target.setName("Modular Target " + System.currentTimeMillis());
    target.setTargetLocations(locations);
    targetService.saveTargetDefinition(target);
  }

  private static IProvisioningAgent createAgent(File p2AgentDir) throws ProvisionException
  {
    BundleContext bundleContext = Activator.getBundleContext();
    ServiceReference<IProvisioningAgentProvider> providerRef = bundleContext
        .getServiceReference(IProvisioningAgentProvider.class);

    try
    {
      IProvisioningAgentProvider provider = bundleContext.getService(providerRef);
      return provider.createAgent(p2AgentDir.toURI());
    }
    finally
    {
      bundleContext.ungetService(providerRef);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory implements ITargetLocationFactory
  {
    public Factory()
    {
    }

    public ITargetLocation getTargetLocation(String type, String serializedXML) throws CoreException
    {
      Element location;

      try
      {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(serializedXML.getBytes("UTF-8"))); //$NON-NLS-1$
        location = document.getDocumentElement();
      }
      catch (Exception e)
      {
        throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
      }

      if (TYPE.equals(type) && location != null)
      {
        String locationType = location.getAttribute("type");
        if (locationType.equals(type))
        {
          String bundlePool = location.getAttribute("bundlePool");
          String agent = location.getAttribute("agent");
          String profile = location.getAttribute("profile");
          return new Targlet(new File(bundlePool), new File(agent), profile);
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class UIFactory implements IAdapterFactory, ITargetLocationEditor, ITargetLocationUpdater
  {
    private static final Class<?>[] ADAPTERS = { ILabelProvider.class, ITreeContentProvider.class,
        ITargetLocationEditor.class, ITargetLocationUpdater.class };

    private ILabelProvider labelProvider;

    public UIFactory()
    {
    }

    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList()
    {
      return ADAPTERS;
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Object adaptableObject, Class adapterType)
    {
      if (adaptableObject instanceof Targlet)
      {
        if (adapterType == ILabelProvider.class)
        {
          return getLabelProvider();
        }

        if (adapterType == ITreeContentProvider.class)
        {
          return getContentProvider();
        }

        if (adapterType == ITargetLocationEditor.class)
        {
          return this;
        }

        if (adapterType == ITargetLocationUpdater.class)
        {
          return this;
        }
      }

      return null;
    }

    public boolean canEdit(ITargetDefinition target, ITargetLocation targetLocation)
    {
      return targetLocation instanceof Targlet;
    }

    public IWizard getEditWizard(ITargetDefinition target, ITargetLocation targetLocation)
    {
      return new EditTargletWizard(target, (Targlet)targetLocation);
    }

    public boolean canUpdate(ITargetDefinition target, ITargetLocation targetLocation)
    {
      return targetLocation instanceof Targlet;
    }

    public IStatus update(ITargetDefinition target, ITargetLocation targetLocation, IProgressMonitor monitor)
    {
      if (targetLocation instanceof Targlet)
      {
        int xxx;
        // try
        // {
        // boolean result = ((Targlet)targetLocation).update(new HashSet<Object>(0), monitor);
        // if (result)
        // {
        // return Status.OK_STATUS;
        // }
        //
        //            return new Status(IStatus.OK, PDECore.PLUGIN_ID, ITargetLocationUpdater.STATUS_CODE_NO_CHANGE, "", null); //$NON-NLS-1$
        // }
        // catch (CoreException e)
        // {
        // return e.getStatus();
        // }
        return new Status(IStatus.OK, PDECore.PLUGIN_ID, ITargetLocationUpdater.STATUS_CODE_NO_CHANGE, "", null); //$NON-NLS-1$
      }

      return Status.CANCEL_STATUS;
    }

    private ILabelProvider getLabelProvider()
    {
      if (labelProvider == null)
      {
        labelProvider = new TargletLabelProvider();
      }

      return labelProvider;
    }

    private ITreeContentProvider getContentProvider()
    {
      return new TargletContentProvider();
    }

    /**
     * @author Eike Stepper
     */
    private static class TargletLabelProvider extends StyledBundleLabelProvider
    {
      public TargletLabelProvider()
      {
        super(true, false);
      }

      @Override
      public Image getImage(Object element)
      {
        if (element instanceof Targlet)
        {
          return ResourceManager.getPluginImage("org.eclipse.emf.cdo.releng.setup", "icons/profile.gif");
        }

        return super.getImage(element);
      }

      @Override
      public String getText(Object element)
      {
        if (element instanceof Targlet)
        {
          Targlet targlet = (Targlet)element;
          return targlet.getProfileID();
        }

        return super.getText(element);
      }
    }

    /**
     * @author Eike Stepper
     */
    private static class TargletContentProvider implements ITreeContentProvider
    {
      private Map<IInstallableUnit, Targlet> parents = new HashMap<IInstallableUnit, Targlet>();

      public TargletContentProvider()
      {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
      {
      }

      public void dispose()
      {
        // TODO dispose() is not called by PDE!
        parents = null;
      }

      public Object[] getElements(Object inputElement)
      {
        return getChildren(inputElement);
      }

      public Object[] getChildren(Object parentElement)
      {
        if (false)
        {
          Targlet location = (Targlet)parentElement;
          if (location.isResolved())
          {
            try
            {
              // // if this is a bundle container then we must be sure that all bundle containers are
              // // happy since they all share the same profile.
              // ITargetDefinition target = location.getTarget();
              // if (target != null && P2TargetUtils.isResolved(target))
              // {
              IInstallableUnit[] units = location.getInstallableUnits();
              for (int i = 0; i < units.length; i++)
              {
                parents.put(units[i], location);
              }

              return units;
              // }
            }
            catch (CoreException e)
            {
              return new Object[] { e.getStatus() };
            }
          }
        }

        return new Object[0];
      }

      public boolean hasChildren(Object element)
      {
        return getChildren(element).length != 0;
      }

      public Object getParent(Object element)
      {
        return parents.get(element);
      }
    }

    public static class NewTargletWizard extends Wizard implements ITargetLocationWizard
    {
      private ITargetDefinition target;

      private Targlet targlet;

      private TargletWizardPage page;

      private static final String SETTINGS_SECTION = "editBundleContainerWizard"; //$NON-NLS-1$

      public NewTargletWizard()
      {
        setWindowTitle("Add Targlet");
      }

      public void setTarget(ITargetDefinition target)
      {
        this.target = target;
      }

      @Override
      public void addPages()
      {
        // IDialogSettings settings = PDEPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION);
        // if (settings == null)
        // {
        // settings = PDEPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION);
        // }
        //
        // setDialogSettings(settings);

        page = new TargletWizardPage(target);
        addPage(page);
      }

      @Override
      public boolean performFinish()
      {
        targlet = page.getBundleContainer();
        return true;
      }

      public ITargetLocation[] getLocations()
      {
        return new ITargetLocation[] { targlet };
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class EditTargletWizard extends EditBundleContainerWizard
    {
      private TargletWizardPage page;

      private ITargetDefinition target;

      private Targlet targlet;

      public EditTargletWizard(ITargetDefinition target, Targlet targlet)
      {
        super(target, targlet);
        this.target = target;
        this.targlet = targlet;
        setWindowTitle("Edit Targlet");
      }

      @Override
      public void addPages()
      {
        page = new TargletWizardPage(target, targlet);
        addPage(page);
      }
    }

    /**
     * @author Eike Stepper
     */
    private static class TargletWizardPage extends WizardPage implements IEditBundleContainerPage
    {
      private ITargetDefinition target;

      private Targlet targlet;

      /**
       * Constructor for creating a new container
       */
      public TargletWizardPage(ITargetDefinition target)
      {
        super("AddP2Container"); //$NON-NLS-1$
        setTitle("Add Targlet");
        setMessage("Select content from a software site to be added to your target.");
        this.target = target;
      }

      /**
       * Constructor for editing an existing container
       */
      public TargletWizardPage(ITargetDefinition target, Targlet targlet)
      {
        this(target);
        this.targlet = targlet;
        setTitle("Edit Targlet");
      }

      public Targlet getBundleContainer()
      {
        try
        {
          File p2PoolDir = new File("C:/develop/.p2pool-ide");
          File p2AgentDir = new File("C:/develop/.p2pool-ide/p2");
          String profileID = "C__develop_aaa_cdo.releng_master_tpX";
          String destination = "C:/develop/aaa/cdo.releng/master/tpX"; // XXX

          java.net.URI[] p2Repositories = { new java.net.URI("http://download.eclipse.org/releases/luna") };
          IVersionedId[] rootComponents = { new VersionedId("org.eclipse.emf.ecore.feature.group", Version.emptyVersion) };

          return create(p2PoolDir, p2AgentDir, profileID, destination, p2Repositories, rootComponents,
              new NullProgressMonitor());
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }

      public void storeSettings()
      {
        // IDialogSettings settings = getDialogSettings();
        // if (settings != null)
        // {
        // settings.put(SETTINGS_GROUP_BY_CATEGORY, fShowCategoriesButton.getSelection());
        // settings.put(SETTINGS_SHOW_OLD_VERSIONS, fShowOldVersionsButton.getSelection());
        // settings.put(SETTINGS_SELECTED_REPOSITORY, fRepoLocation != null ? fRepoLocation.toString() : null);
        // }
      }

      public void createControl(Composite parent)
      {
        Composite composite = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH, 0, 0);

        Label label = new Label(composite, SWT.NONE);
        label.setText("Targlet Info");
        label.setLayoutData(new GridData(GridData.FILL_BOTH));

        // restoreWidgetState();
        setControl(composite);
        // setPageComplete(false);

        // if (fEditContainer == null)
        // {
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.LOCATION_ADD_SITE_WIZARD);
        // }
        // else
        // {
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.LOCATION_EDIT_SITE_WIZARD);
        // }
      }

      /**
       * Checks if the page is complete, updating messages and finish button.
       */
      void pageChanged()
      {
        // if (fSelectedIUStatus.getSeverity() == IStatus.ERROR)
        // {
        // setErrorMessage(fSelectedIUStatus.getMessage());
        // setPageComplete(false);
        // }
        // else if (fAvailableIUGroup != null && fAvailableIUGroup.getCheckedLeafIUs().length == 0)
        // {
        // // On page load and when sites are selected, we might not have an error status, but we want finish to
        // remain
        // // disabled
        // setPageComplete(false);
        // }
        // else
        {
          setErrorMessage(null);
          setPageComplete(true);
        }
      }

      /**
       * Restores the state of the wizard from previous invocations
       */
      private void restoreWidgetState()
      {
        // IDialogSettings settings = getDialogSettings();
        // URI uri = null;
        // boolean showCategories = fQueryContext.shouldGroupByCategories();
        // boolean showOldVersions = fQueryContext.getShowLatestVersionsOnly();
        //
        // // Init the checkboxes and repo selector combo
        // if (fEditContainer != null)
        // {
        // if (fEditContainer.getRepositories() != null)
        // {
        // uri = fEditContainer.getRepositories()[0];
        // }
        // }
        // else if (settings != null)
        // {
        // String stringURI = settings.get(SETTINGS_SELECTED_REPOSITORY);
        // if (stringURI != null && stringURI.trim().length() > 0)
        // {
        // try
        // {
        // uri = new URI(stringURI);
        // }
        // catch (URISyntaxException e)
        // {
        // PDEPlugin.log(e);
        // }
        // }
        // }
        //
        // if (settings != null)
        // {
        // if (settings.get(SETTINGS_GROUP_BY_CATEGORY) != null)
        // {
        // showCategories = settings.getBoolean(SETTINGS_GROUP_BY_CATEGORY);
        // }
        // if (settings.get(SETTINGS_SHOW_OLD_VERSIONS) != null)
        // {
        // showOldVersions = settings.getBoolean(SETTINGS_SHOW_OLD_VERSIONS);
        // }
        // }
        //
        // if (uri != null)
        // {
        // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_SPECIFIED, uri);
        // }
        // else if (fEditContainer != null)
        // {
        // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_ALL, null);
        // }
        // else
        // {
        // fRepoSelector.setRepositorySelection(AvailableIUGroup.AVAILABLE_NONE, null);
        // }
        //
        // fShowCategoriesButton.setSelection(showCategories);
        // fShowOldVersionsButton.setSelection(showOldVersions);
        //
        // if (fEditContainer != null)
        // {
        // fIncludeRequiredButton.setSelection(fEditContainer.getIncludeAllRequired());
        // fAllPlatformsButton.setSelection(fEditContainer.getIncludeAllEnvironments());
        // fIncludeSourceButton.setSelection(fEditContainer.getIncludeSource());
        // fConfigurePhaseButton.setSelection(fEditContainer.getIncludeConfigurePhase());
        // }
        // else
        // {
        // // If we are creating a new container, but there is an existing iu container we should use it's settings
        // // (otherwise we overwrite them)
        // ITargetLocation[] knownContainers = fTarget.getTargetLocations();
        // if (knownContainers != null)
        // {
        // for (int i = 0; i < knownContainers.length; i++)
        // {
        // if (knownContainers[i] instanceof IUBundleContainer)
        // {
        // fIncludeRequiredButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeAllRequired());
        // fAllPlatformsButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeAllEnvironments());
        // fIncludeSourceButton.setSelection(((IUBundleContainer)knownContainers[i]).getIncludeSource());
        // fConfigurePhaseButton
        // .setSelection(((IUBundleContainer)knownContainers[i]).getIncludeConfigurePhase());
        // }
        // }
        // }
        // }
        //
        // // If the user can create two containers with different settings for include required we won't resolve
        // // correctly
        // // If the user has an existing container, don't let them edit the options, bug 275013
        // if (fTarget != null)
        // {
        // ITargetLocation[] containers = fTarget.getTargetLocations();
        // if (containers != null)
        // {
        // for (int i = 0; i < containers.length; i++)
        // {
        // if (containers[i] instanceof IUBundleContainer && containers[i] != fEditContainer)
        // {
        // fIncludeRequiredButton.setSelection(((IUBundleContainer)containers[i]).getIncludeAllRequired());
        // fAllPlatformsButton.setSelection(((IUBundleContainer)containers[i]).getIncludeAllEnvironments());
        // break;
        // }
        // }
        // }
        // }
        //
        // fAllPlatformsButton.setEnabled(!fIncludeRequiredButton.getSelection());
        //
        // updateViewContext();
        // fRepoSelector.getDefaultFocusControl().setFocus();
        // updateDetails();
        //
        // // If we are editing a bundle check any installable units
        // if (fEditContainer != null)
        // {
        // try
        // {
        // // TODO This code does not do a good job, selecting, revealing, and collapsing all
        // // Only able to check items if we don't have categories
        // fQueryContext.setViewType(IUViewQueryContext.AVAILABLE_VIEW_FLAT);
        // fAvailableIUGroup.updateAvailableViewState();
        // fAvailableIUGroup.setChecked(fEditContainer.getInstallableUnits());
        // // Make sure view is back in proper state
        // updateViewContext();
        // IInstallableUnit[] units = fAvailableIUGroup.getCheckedLeafIUs();
        // if (units.length > 0)
        // {
        // fAvailableIUGroup.getCheckboxTreeViewer().setSelection(new StructuredSelection(units[0]), true);
        // if (units.length == 1)
        // {
        // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemSelected,
        // Integer.toString(units.length)));
        // }
        // else
        // {
        // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemsSelected,
        // Integer.toString(units.length)));
        // }
        // }
        // else
        // {
        // fSelectionCount.setText(NLS.bind(Messages.EditIUContainerPage_itemsSelected, Integer.toString(0)));
        // }
        // fAvailableIUGroup.getCheckboxTreeViewer().collapseAll();
        //
        // }
        // catch (CoreException e)
        // {
        // PDEPlugin.log(e);
        // }
        // }
      }
    }
  }
}
