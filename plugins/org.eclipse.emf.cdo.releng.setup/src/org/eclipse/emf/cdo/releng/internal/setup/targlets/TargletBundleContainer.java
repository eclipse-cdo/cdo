/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.AutomaticSourceLocator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Repository;
import org.eclipse.emf.cdo.releng.setup.RepositoryList;
import org.eclipse.emf.cdo.releng.setup.SetupFactory;
import org.eclipse.emf.cdo.releng.setup.Targlet;
import org.eclipse.emf.cdo.releng.setup.util.XMLUtil;
import org.eclipse.emf.cdo.releng.setup.util.XMLUtil.ElementHandler;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.p2.engine.Phase;
import org.eclipse.equinox.internal.p2.engine.PhaseSet;
import org.eclipse.equinox.internal.p2.engine.phases.Collect;
import org.eclipse.equinox.internal.p2.engine.phases.Install;
import org.eclipse.equinox.internal.p2.engine.phases.Property;
import org.eclipse.equinox.internal.provisional.p2.director.PlanExecutionHelper;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IEngine;
import org.eclipse.equinox.p2.engine.IPhaseSet;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProvisioningPlan;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.engine.query.IUProfilePropertyQuery;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.equinox.p2.query.CollectionResult;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.IQueryable;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetLocationFactory;
import org.eclipse.pde.core.target.TargetBundle;
import org.eclipse.pde.core.target.TargetFeature;
import org.eclipse.pde.internal.core.target.AbstractBundleContainer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TargletBundleContainer extends AbstractBundleContainer
{
  public static final String TYPE = "Targlet";

  private static final String FOLLOW_ARTIFACT_REPOSITORY_REFERENCES = "org.eclipse.equinox.p2.director.followArtifactRepositoryReferences";

  private static final String FEATURE_SUFFIX = ".feature.group";

  private static final String SOURCE_IU_ID = "org.eclipse.emf.cdo.releng.targlet.source.bundles"; //$NON-NLS-1$

  private static final IRequirement BUNDLE_REQUIREMENT = MetadataFactory.createRequirement(
      "org.eclipse.equinox.p2.eclipse.type", "bundle", null, null, false, false, false); //$NON-NLS-1$ //$NON-NLS-2$

  private static final byte[] BUFFER = new byte[8192];

  private static final String PROP_ARCH = "osgi.arch"; //$NON-NLS-1$

  private static final String PROP_OS = "osgi.os"; //$NON-NLS-1$

  private static final String PROP_WS = "osgi.ws"; //$NON-NLS-1$

  private static final String TRUE = Boolean.TRUE.toString();

  private static final String FALSE = Boolean.FALSE.toString();

  private final AtomicBoolean profileNeedsUpdate = new AtomicBoolean();

  private List<Targlet> targlets = new ArrayList<Targlet>();

  private transient ITargetDefinition target;

  private transient String digest;

  private transient IProfile profile;

  public TargletBundleContainer()
  {
  }

  @Override
  public String getType()
  {
    return TYPE;
  }

  public ITargetDefinition getTarget()
  {
    return target;
  }

  public List<Targlet> getTarglets()
  {
    return Collections.unmodifiableList(targlets);
  }

  public Targlet getTarglet(String name)
  {
    for (Targlet targlet : targlets)
    {
      if (ObjectUtil.equals(targlet.getName(), name))
      {
        return targlet;
      }
    }

    return null;
  }

  public void addTarglet(Targlet targlet)
  {
    Targlet copy = SetupFactory.eINSTANCE.createTarglet(targlet);

    removeTarglet(copy.getName());
    targlets.add(copy);
    resetProfile();
  }

  public Targlet removeTarglet(String name)
  {
    for (Iterator<Targlet> it = targlets.iterator(); it.hasNext();)
    {
      Targlet targlet = it.next();
      if (ObjectUtil.equals(targlet.getName(), name))
      {
        it.remove();
        resetProfile();
        return targlet;
      }
    }

    return null;
  }

  @Override
  public String serialize()
  {
    try
    {
      return Persistence.toXML(targlets).toString();
    }
    catch (Exception ex)
    {
      Activator.log(ex);
      return null;
    }
  }

  public boolean isIncludeSources()
  {
    for (Targlet targlet : targlets)
    {
      if (targlet.isIncludeSources())
      {
        return true;
      }
    }

    return false;
  }

  public boolean isIncludeAllPlatforms()
  {
    for (Targlet targlet : targlets)
    {
      if (targlet.isIncludeAllPlatforms())
      {
        return true;
      }
    }

    return false;
  }

  public String getEnvironmentProperties()
  {
    StringBuilder builder = new StringBuilder();
    String ws = target.getWS();
    if (ws == null)
    {
      ws = Platform.getWS();
    }

    builder.append(PROP_WS);
    builder.append("="); //$NON-NLS-1$
    builder.append(ws);
    builder.append(","); //$NON-NLS-1$
    String os = target.getOS();
    if (os == null)
    {
      os = Platform.getOS();
    }

    builder.append(PROP_OS);
    builder.append("="); //$NON-NLS-1$
    builder.append(os);
    builder.append(","); //$NON-NLS-1$
    String arch = target.getArch();
    if (arch == null)
    {
      arch = Platform.getOSArch();
    }

    builder.append(PROP_ARCH);
    builder.append("="); //$NON-NLS-1$
    builder.append(arch);
    return builder.toString();
  }

  public String getNLProperty()
  {
    String nl = target.getNL();
    if (nl == null)
    {
      nl = Platform.getNL();
    }

    return nl;
  }

  public String getDigest() throws ProvisionException
  {
    if (digest == null)
    {
      InputStream stream = null;

      try
      {
        Writer writer = Persistence.toXML(targlets);
        writer.write("\n<!-- Environment Properties: ");
        writer.write(getEnvironmentProperties());
        writer.write(" -->");
        writer.write("\n<!-- NL Property: ");
        writer.write(getNLProperty());
        writer.write(" -->\n");

        final MessageDigest digest = MessageDigest.getInstance("SHA-1");
        stream = new FilterInputStream(new ByteArrayInputStream(writer.toString().getBytes("UTF-8")))
        {
          @Override
          public int read() throws IOException
          {
            for (;;)
            {
              int ch = super.read();
              switch (ch)
              {
              case -1:
                return -1;

              case 10:
              case 13:
                continue;
              }

              digest.update((byte)ch);
              return ch;
            }
          }

          @Override
          public int read(byte[] b, int off, int len) throws IOException
          {
            int read = super.read(b, off, len);
            if (read == -1)
            {
              return -1;
            }

            for (int i = off; i < off + read; i++)
            {
              byte c = b[i];
              if (c == 10 || c == 13)
              {
                if (i + 1 < off + read)
                {
                  System.arraycopy(b, i + 1, b, i, read - i - 1);
                  --i;
                }

                --read;
              }
            }

            digest.update(b, off, read);
            return read;
          }
        };

        synchronized (BUFFER)
        {
          while (stream.read(BUFFER) != -1)
          {
            // Do nothing
          }
        }

        this.digest = HexUtil.bytesToHex(digest.digest());
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new RuntimeException(ex);
      }
      finally
      {
        IOUtil.close(stream);
      }
    }

    return digest;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    // for (Targlet targlet : targlets)
    // {
    // if (builder.length() == 0)
    // {
    // builder.append(" for ");
    // }
    // else
    // {
    // builder.append(", ");
    // }
    //
    // builder.append(targlet.getName());
    // }

    return "Targlet Container" + builder;
  }

  @Override
  public String getLocation(boolean resolve) throws CoreException
  {
    return TargletProfileManager.POOL_FOLDER.getAbsolutePath();
  }

  @Override
  protected void associateWithTarget(ITargetDefinition target)
  {
    super.associateWithTarget(target);
    this.target = target;

  }

  public void updateProfile(IProgressMonitor monitor) throws ProvisionException
  {
    initProfile(monitor);

    try
    {
      List<InstallableUnit> roots = new ArrayList<InstallableUnit>();
      for (Targlet targlet : targlets)
      {
        for (InstallableUnit root : targlet.getRoots())
        {
          roots.add(root);
        }
      }

      if (roots.isEmpty())
      {
        return;
      }

      final IUAnalyzer analyzer = new IUAnalyzer();
      final Map<IInstallableUnit, File> sources = new HashMap<IInstallableUnit, File>();
      List<URI> uris = new ArrayList<URI>();

      for (Targlet targlet : targlets)
      {
        for (AutomaticSourceLocator sourceLocator : targlet.getSourceLocators())
        {
          boolean locateNestedProjects = sourceLocator.isLocateNestedProjects();
          File rootFolder = new File(sourceLocator.getRootFolder());

          Map<IInstallableUnit, File> ius = analyzer.analyze(rootFolder, locateNestedProjects, monitor);
          sources.putAll(ius);
        }

        for (P2Repository p2Repository : targlet.getActiveP2Repositories())
        {
          try
          {
            URI uri = new URI(p2Repository.getURL());
            uris.add(uri);
          }
          catch (URISyntaxException ex)
          {
            throw new ProvisionException(ex.getMessage(), ex);
          }
        }
      }

      IProvisioningAgent agent = TargletProfileManager.getInstance().getAgent();

      IMetadataRepositoryManager metadataManager = (IMetadataRepositoryManager)agent
          .getService(IMetadataRepositoryManager.SERVICE_NAME);
      IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager)agent
          .getService(IArtifactRepositoryManager.SERVICE_NAME);

      for (URI uri : uris)
      {
        metadataManager.addRepository(uri);
        artifactManager.addRepository(uri);
      }

      ProvisioningContext context = new ProvisioningContext(agent)
      {
        private CollectionResult<IInstallableUnit> result;

        @Override
        public IQueryable<IInstallableUnit> getMetadata(IProgressMonitor monitor)
        {
          if (result == null)
          {
            List<IInstallableUnit> ius = new ArrayList<IInstallableUnit>();
            Set<String> ids = analyzer.getIDs();
            prepareSources(ius, ids);

            IQueryable<IInstallableUnit> metadata = super.getMetadata(monitor);
            IQueryResult<IInstallableUnit> metadataResult = metadata.query(QueryUtil.createIUAnyQuery(), monitor);

            for (Iterator<IInstallableUnit> it = metadataResult.iterator(); it.hasNext();)
            {
              IInstallableUnit iu = it.next();
              if (!ids.contains(iu.getId()))
              {
                ius.add(iu);
              }
            }

            result = new CollectionResult<IInstallableUnit>(ius);
          }

          return result;
        }

        private void prepareSources(List<IInstallableUnit> ius, Set<String> ids)
        {
          for (IInstallableUnit iu : sources.keySet())
          {
            ius.add(iu);

            // TODO Should we create source IUs for source projects only if needed (i.e. required by feature content)?
            String id = iu.getId();
            String suffix = "";

            if (id.endsWith(FEATURE_SUFFIX))
            {
              id = id.substring(0, id.length() - FEATURE_SUFFIX.length());
              suffix = FEATURE_SUFFIX;
            }

            InstallableUnitDescription description = new MetadataFactory.InstallableUnitDescription();
            description.setId(id + ".source" + suffix);
            description.setVersion(iu.getVersion());

            for (Map.Entry<String, String> property : iu.getProperties().entrySet())
            {
              String key = property.getKey();
              String value = property.getValue();

              if ("org.eclipse.equinox.p2.name".equals(key))
              {
                value = "Source for " + value;
              }

              description.setProperty(key, value);
            }

            description.addProvidedCapabilities(Collections.singleton(MetadataFactory.createProvidedCapability(
                IInstallableUnit.NAMESPACE_IU_ID, description.getId(), description.getVersion())));

            IInstallableUnit sourceIU = MetadataFactory.createInstallableUnit(description);
            ius.add(sourceIU);
            ids.add(sourceIU.getId());
          }
        }
      };

      URI[] uriArray = uris.toArray(new URI[uris.size()]);
      context.setMetadataRepositories(uriArray);
      context.setArtifactRepositories(uriArray);
      context.setProperty(ProvisioningContext.FOLLOW_REPOSITORY_REFERENCES, FALSE);
      context.setProperty(FOLLOW_ARTIFACT_REPOSITORY_REFERENCES, FALSE);

      Version sourceIUVersion = getSourceIUVersion();

      IPlanner planner = (IPlanner)agent.getService(IPlanner.SERVICE_NAME);
      if (planner == null)
      {
        throw new ProvisionException("Planner could not be loaded");
      }

      IProfileChangeRequest request = planner.createChangeRequest(profile);
      IQuery<IInstallableUnit> query = new IUProfilePropertyQuery(IProfile.PROP_PROFILE_ROOT_IU, TRUE);
      IQueryResult<IInstallableUnit> installedIUs = profile.query(query, new ProgressMonitor());
      request.removeAll(installedIUs.toUnmodifiableSet());

      IQueryable<IInstallableUnit> metadata = context.getMetadata(monitor);
      for (InstallableUnit root : roots)
      {
        IQuery<IInstallableUnit> iuQuery = QueryUtil.createIUQuery(root.getID(), root.getVersionRange());
        IQuery<IInstallableUnit> latestQuery = QueryUtil.createLatestQuery(iuQuery);

        for (IInstallableUnit iu : metadata.query(latestQuery, new ProgressMonitor()))
        {
          request.setInstallableUnitProfileProperty(iu, IProfile.PROP_PROFILE_ROOT_IU, TRUE);
          request.add(iu);
        }
      }

      planAndInstall(agent, context, planner, request);

      if (isIncludeSources())
      {
        IInstallableUnit iu = generateSourceIU(sourceIUVersion);

        IProfileChangeRequest sourceRequest = planner.createChangeRequest(profile);
        sourceRequest.setInstallableUnitProfileProperty(iu, IProfile.PROP_PROFILE_ROOT_IU, TRUE);
        sourceRequest.add(iu);

        planAndInstall(agent, context, planner, sourceRequest);
      }

      updateWorkspace(sources, new ProgressMonitor());
      profileNeedsUpdate.set(false);
    }
    catch (Throwable t)
    {
      profileNeedsUpdate.set(true);
      // TODO Handle update problems, e.g. "return" to last working profile
    }
  }

  private Version getSourceIUVersion()
  {
    IQuery<IInstallableUnit> query = QueryUtil.createIUQuery(SOURCE_IU_ID);
    IQueryResult<IInstallableUnit> result = profile.query(query, null);
    if (!result.isEmpty())
    {
      IInstallableUnit currentSourceIU = result.iterator().next();
      Integer major = (Integer)currentSourceIU.getVersion().getSegment(0);
      return Version.createOSGi(major.intValue() + 1, 0, 0);
    }

    return Version.createOSGi(1, 0, 0);
  }

  private IInstallableUnit generateSourceIU(Version sourceIUVersion)
  {
    // Create and return an IU that has optional and greedy requirements on all source bundles
    // related to bundle IUs in the profile
    ArrayList<IRequirement> requirements = new ArrayList<IRequirement>();

    IQueryResult<IInstallableUnit> profileIUs = profile.query(QueryUtil.createIUAnyQuery(), null);
    for (Iterator<IInstallableUnit> i = profileIUs.iterator(); i.hasNext();)
    {
      IInstallableUnit profileIU = i.next();

      // TODO What about source features?
      if (profileIU.satisfies(BUNDLE_REQUIREMENT))
      {
        String id = profileIU.getId() + ".source"; //$NON-NLS-1$
        Version version = profileIU.getVersion();

        IRequirement sourceRequirement = MetadataFactory.createRequirement(
            "osgi.bundle", id, new VersionRange(version, true, version, true), null, true, false, true); //$NON-NLS-1$
        requirements.add(sourceRequirement);
      }
    }

    InstallableUnitDescription sourceIUDescription = new MetadataFactory.InstallableUnitDescription();
    sourceIUDescription.setSingleton(true);
    sourceIUDescription.setId(SOURCE_IU_ID);
    sourceIUDescription.setVersion(sourceIUVersion);
    sourceIUDescription.addRequirements(requirements);
    sourceIUDescription.setCapabilities(new IProvidedCapability[] { MetadataFactory.createProvidedCapability(
        IInstallableUnit.NAMESPACE_IU_ID, SOURCE_IU_ID, sourceIUVersion) });

    return MetadataFactory.createInstallableUnit(sourceIUDescription);
  }

  private void updateWorkspace(final Map<IInstallableUnit, File> sources, IProgressMonitor monitor)
      throws ProvisionException
  {
    try
    {
      final IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.run(new IWorkspaceRunnable()
      {
        public void run(IProgressMonitor monitor) throws CoreException
        {
          try
          {
            DocumentBuilder documentBuilder = XMLUtil.createDocumentBuilder();
            IWorkspaceRoot root = workspace.getRoot();

            // TODO plan.getAdditions() would probably also do and be cheaper
            IQueryResult<IInstallableUnit> result = profile.query(QueryUtil.createIUAnyQuery(), monitor);
            for (IInstallableUnit iu : result.toUnmodifiableSet())
            {
              File folder = sources.get(iu);
              if (folder != null)
              {
                final AtomicReference<String> projectName = new AtomicReference<String>();

                Element rootElement = XMLUtil.loadRootElement(documentBuilder, new File(folder, ".project"));
                XMLUtil.handleChildElements(rootElement, new ElementHandler()
                {
                  public void handleElement(Element element) throws Exception
                  {
                    if ("name".equals(element.getTagName()))
                    {
                      projectName.set(element.getTextContent().trim());
                    }
                  }
                });

                String name = projectName.get();
                if (name != null && name.length() != 0)
                {
                  File location = folder.getCanonicalFile();

                  IProject project = root.getProject(name);
                  if (project.exists())
                  {
                    // project.delete(false, true, monitor);
                    File existingLocation = new File(project.getLocation().toOSString()).getCanonicalFile();
                    if (!existingLocation.equals(location))
                    {
                      System.err.println("Project " + name + " exists in different location: " + existingLocation);
                      continue;
                    }
                  }
                  else
                  {
                    IProjectDescription projectDescription = workspace.newProjectDescription(name);
                    projectDescription.setLocation(new Path(location.getAbsolutePath()));
                    project.create(projectDescription, monitor);
                  }

                  if (!project.isOpen())
                  {
                    project.open(monitor);
                  }
                }
              }
            }
          }
          catch (Exception ex)
          {
            TargletProfileManager.throwProvisionException(ex);
          }
        }
      }, monitor);
    }
    catch (Exception ex)
    {
      TargletProfileManager.throwProvisionException(ex);
    }
  }

  private void resolveUnits(IProgressMonitor monitor) throws ProvisionException
  {
    try
    {
      initProfile(monitor);
      if (profileNeedsUpdate.get())
      {
        updateProfile(monitor);
      }

      List<TargetBundle> bundles = new ArrayList<TargetBundle>();
      List<TargetFeature> features = new ArrayList<TargetFeature>();

      IFileArtifactRepository cache = getBundlePool(TargletProfileManager.getInstance().getAgent());

      IQueryResult<IInstallableUnit> result = profile.query(QueryUtil.createIUAnyQuery(), null);
      for (Iterator<IInstallableUnit> i = result.iterator(); i.hasNext();)
      {
        IInstallableUnit unit = i.next();

        if (isOSGiBundle(unit))
        {
          generateBundle(unit, cache, bundles);

          // if (getIncludeSource())
          // {
          // // bit of a hack using the bundle naming convention for finding source bundles
          // // but this matches what we do when adding source to the profile so...
          // IQuery<IInstallableUnit> sourceQuery = QueryUtil.createIUQuery(unit.getId() + ".source",
          // unit.getVersion());
          // IQueryResult<IInstallableUnit> result = metadata.query(sourceQuery, null);
          // if (!result.isEmpty())
          // {
          // generateBundle(result.iterator().next(), artifacts, bundles);
          // }
          // }
        }
        else if (isFeatureJar(unit))
        {
          generateFeature(unit, cache, features);
        }
      }

      fBundles = bundles.toArray(new TargetBundle[bundles.size()]);
      fFeatures = features.toArray(new TargetFeature[features.size()]);
    }
    catch (Throwable t)
    {
      Activator.log(t);
      TargletProfileManager.throwProvisionException(t);
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

  @Override
  protected TargetBundle[] resolveBundles(ITargetDefinition target, IProgressMonitor monitor) throws CoreException
  {
    // TODO Distribute the progress more evenly between resolveBundles (2/3) and resolveFeatures (1/3)
    resolveUnits(monitor);

    return fBundles;
  }

  @Override
  protected TargetFeature[] resolveFeatures(ITargetDefinition target, IProgressMonitor monitor) throws CoreException
  {
    // All work has been done in resolveBundles() already
    return fFeatures;
  }

  private void initProfile(IProgressMonitor monitor) throws ProvisionException
  {
    if (profile == null)
    {
      TargletProfileManager manager = TargletProfileManager.getInstance();
      profile = manager.getProfile(this, profileNeedsUpdate, monitor);
    }
  }

  private void resetProfile()
  {
    digest = null;
    profile = null;
  }

  private static boolean isOSGiBundle(IInstallableUnit unit)
  {
    return providesNamespace(unit, "osgi.bundle");
  }

  private static boolean isFeatureJar(IInstallableUnit unit)
  {
    return providesNamespace(unit, "org.eclipse.update.feature");
  }

  private static boolean providesNamespace(IInstallableUnit unit, String namespace)
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

  private static void planAndInstall(IProvisioningAgent agent, ProvisioningContext context, IPlanner planner,
      IProfileChangeRequest request) throws ProvisionException
  {
    IProvisioningPlan plan = planner.getProvisioningPlan(request, context, new ProgressMonitor());
    if (!plan.getStatus().isOK())
    {
      throw new ProvisionException(plan.getStatus());
    }

    IEngine engine = (IEngine)agent.getService(IEngine.SERVICE_NAME);
    if (engine == null)
    {
      throw new ProvisionException("Engine could not be loaded");
    }

    IPhaseSet phaseSet = createPhaseSet();
    IStatus status = PlanExecutionHelper.executePlan(plan, engine, phaseSet, context, new ProgressMonitor());
    if (!status.isOK())
    {
      throw new ProvisionException(status);
    }
  }

  private static IPhaseSet createPhaseSet()
  {
    List<Phase> phases = new ArrayList<Phase>(4);
    phases.add(new Collect(100));
    phases.add(new Property(1));
    phases.add(new Install(50));
    // phases.add(new CollectNativesPhase(100));

    return new PhaseSet(phases.toArray(new Phase[phases.size()]));
  }

  private static IFileArtifactRepository getBundlePool(IProvisioningAgent agent) throws ProvisionException
  {
    IArtifactRepositoryManager manager = (IArtifactRepositoryManager)agent
        .getService(IArtifactRepositoryManager.SERVICE_NAME);
    if (manager == null)
    {
      throw new ProvisionException("Artifact respository manager could not be loaded");
    }

    URI uri = TargletProfileManager.POOL_FOLDER.toURI();

    try
    {
      if (manager.contains(uri))
      {
        return (IFileArtifactRepository)manager.loadRepository(uri, null);
      }
    }
    catch (ProvisionException ex)
    {
      // Could not load or there wasn't one, fall through to create
    }

    IArtifactRepository result = manager.createRepository(uri, "Shared Bundle Pool",
        IArtifactRepositoryManager.TYPE_SIMPLE_REPOSITORY, null);
    return (IFileArtifactRepository)result;
  }

  /**
   * @author Eike Stepper
   */
  private static class ProgressMonitor extends NullProgressMonitor
  {
    @Override
    public void beginTask(String name, int totalWork)
    {
      super.beginTask(name, totalWork);
      log(name);
    }

    @Override
    public void setTaskName(String name)
    {
      super.setTaskName(name);
      log(name);
    }

    @Override
    public void subTask(String name)
    {
      super.subTask(name);
      log(name);
    }

    private void log(String string)
    {
      if (string != null && string.length() != 0)
      {
        System.out.println(string);
      }
    }
  }

  // /**
  // * @author Eike Stepper
  // */
  // public static class ProfileDescriptor
  // {
  // private List<Targlet> targlets = new ArrayList<Targlet>();
  //
  // private String arch;
  //
  // private String os;
  //
  // private String ws;
  //
  // private String nl;
  // }

  /**
   * @author Eike Stepper
   */
  public static class Persistence implements ITargetLocationFactory
  {
    private static final String LOCATION = "location";

    private static final String LOCATION_TYPE = "type";

    private static final String TARGLET = "targlet";

    private static final String TARGLET_NAME = "name";

    private static final String TARGLET_ACTIVE_REPOSITORY_LIST = "activeRepositoryList";

    private static final String TARGLET_INCLUDE_SOURCES = "includeSources";

    private static final String TARGLET_INCLUDE_ALL_PLATFORMS = "includeAllPlatforms";

    private static final String ROOT = "root";

    private static final String ROOT_ID = "id";

    private static final String ROOT_VERSION_RANGE = "versionRange";

    private static final String SOURCE_LOCATOR = "sourceLocator";

    private static final String SOURCE_LOCATOR_ROOT_FOLDER = "rootFolder";

    private static final String SOURCE_LOCATOR_LOCATE_NESTED_PROJECTS = "locateNestedProjects";

    private static final String REPOSITORY_LIST = "repositoryList";

    private static final String REPOSITORY_LIST_NAME = "name";

    private static final String REPOSITORY = "repository";

    private static final String REPOSITORY_URL = "url";

    public TargletBundleContainer getTargetLocation(String type, String serializedXML) throws CoreException
    {
      if (TYPE.equals(type))
      {
        return fromXML(serializedXML);
      }

      return null;
    }

    public static TargletBundleContainer fromXML(String xml) throws CoreException
    {
      Element containerElement;

      try
      {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        containerElement = document.getDocumentElement();
      }
      catch (Exception e)
      {
        throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
      }

      if (containerElement != null)
      {
        String locationType = containerElement.getAttribute(LOCATION_TYPE);
        if (locationType.equals(TYPE))
        {
          TargletBundleContainer container = new TargletBundleContainer();

          NodeList targletNodes = containerElement.getChildNodes();
          for (int i = 0; i < targletNodes.getLength(); i++)
          {
            Node targletNode = targletNodes.item(i);
            if (targletNode instanceof Element)
            {
              Element targletElement = (Element)targletNode;

              Targlet targlet = SetupFactory.eINSTANCE.createTarglet();
              targlet.setName(targletElement.getAttribute(TARGLET_NAME));
              targlet.setActiveRepositoryList(targletElement.getAttribute(TARGLET_ACTIVE_REPOSITORY_LIST));
              targlet.setIncludeSources(Boolean.valueOf(targletElement.getAttribute(TARGLET_INCLUDE_SOURCES)));
              targlet.setIncludeAllPlatforms( //
                  Boolean.valueOf(targletElement.getAttribute(TARGLET_INCLUDE_ALL_PLATFORMS)));
              container.addTarglet(targlet);

              NodeList childNodes = targletElement.getChildNodes();
              for (int j = 0; j < childNodes.getLength(); j++)
              {
                Node childNode = childNodes.item(j);
                if (childNode instanceof Element)
                {
                  Element childElement = (Element)childNode;
                  String tag = childElement.getTagName();
                  if (ROOT.equals(tag))
                  {
                    InstallableUnit root = SetupFactory.eINSTANCE.createInstallableUnit();
                    root.setID(childElement.getAttribute(ROOT_ID));
                    root.setVersionRange(new VersionRange(childElement.getAttribute(ROOT_VERSION_RANGE)));
                    targlet.getRoots().add(root);
                  }
                  else if (SOURCE_LOCATOR.equals(tag))
                  {
                    AutomaticSourceLocator sourceLocator = SetupFactory.eINSTANCE.createAutomaticSourceLocator();
                    sourceLocator.setRootFolder(childElement.getAttribute(SOURCE_LOCATOR_ROOT_FOLDER));
                    sourceLocator.setLocateNestedProjects(Boolean.valueOf(childElement
                        .getAttribute(SOURCE_LOCATOR_LOCATE_NESTED_PROJECTS)));
                    targlet.getSourceLocators().add(sourceLocator);
                  }
                  else if (REPOSITORY_LIST.equals(tag))
                  {
                    RepositoryList repositoryList = SetupFactory.eINSTANCE.createRepositoryList();
                    repositoryList.setName(childElement.getAttribute(REPOSITORY_LIST_NAME));
                    targlet.getRepositoryLists().add(repositoryList);

                    NodeList repositoryNodes = childElement.getChildNodes();
                    for (int k = 0; k < repositoryNodes.getLength(); k++)
                    {
                      Node repositoryNode = repositoryNodes.item(k);
                      if (repositoryNode instanceof Element)
                      {
                        Element repositoryElement = (Element)repositoryNode;

                        P2Repository p2Repository = SetupFactory.eINSTANCE.createP2Repository();
                        p2Repository.setURL(repositoryElement.getAttribute(REPOSITORY_URL));
                        repositoryList.getP2Repositories().add(p2Repository);
                      }
                    }
                  }
                }
              }
            }
          }

          return container;
        }
      }

      return null;
    }

    public static Writer toXML(List<Targlet> targlets) throws ParserConfigurationException, TransformerException
    {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = docBuilder.newDocument();

      Element containerElement = document.createElement(LOCATION);
      containerElement.setAttribute(LOCATION_TYPE, TYPE);
      document.appendChild(containerElement);

      for (Targlet targlet : targlets)
      {
        Element targletElement = document.createElement(TARGLET);
        targletElement.setAttribute(TARGLET_NAME, targlet.getName());
        targletElement.setAttribute(TARGLET_ACTIVE_REPOSITORY_LIST, targlet.getActiveRepositoryList());
        targletElement.setAttribute(TARGLET_INCLUDE_SOURCES, Boolean.toString(targlet.isIncludeSources()));
        targletElement.setAttribute(TARGLET_INCLUDE_ALL_PLATFORMS, Boolean.toString(targlet.isIncludeAllPlatforms()));
        containerElement.appendChild(targletElement);

        for (InstallableUnit root : targlet.getRoots())
        {
          Element rootElement = document.createElement(ROOT);
          rootElement.setAttribute(ROOT_ID, root.getID());
          rootElement.setAttribute(ROOT_VERSION_RANGE, root.getVersionRange().toString());
          targletElement.appendChild(rootElement);
        }

        for (AutomaticSourceLocator sourceLocator : targlet.getSourceLocators())
        {
          Element sourceLocatorElement = document.createElement(SOURCE_LOCATOR);
          sourceLocatorElement.setAttribute(SOURCE_LOCATOR_ROOT_FOLDER, sourceLocator.getRootFolder());
          sourceLocatorElement.setAttribute(SOURCE_LOCATOR_LOCATE_NESTED_PROJECTS,
              Boolean.toString(sourceLocator.isLocateNestedProjects()));
          targletElement.appendChild(sourceLocatorElement);
        }

        for (RepositoryList repositoryList : targlet.getRepositoryLists())
        {
          Element repositoryListElement = document.createElement(REPOSITORY_LIST);
          repositoryListElement.setAttribute(REPOSITORY_LIST_NAME, repositoryList.getName());
          targletElement.appendChild(repositoryListElement);

          for (P2Repository p2Repository : repositoryList.getP2Repositories())
          {
            Element p2RepositoryElement = document.createElement(REPOSITORY);
            p2RepositoryElement.setAttribute(REPOSITORY_URL, p2Repository.getURL());
            repositoryListElement.appendChild(p2RepositoryElement);
          }
        }
      }

      StreamResult result = new StreamResult(new StringWriter());
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(new DOMSource(document), result);

      return result.getWriter();
    }
  }
}
