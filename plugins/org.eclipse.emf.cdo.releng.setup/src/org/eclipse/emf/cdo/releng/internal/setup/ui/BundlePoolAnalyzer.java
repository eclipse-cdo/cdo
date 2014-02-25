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
package org.eclipse.emf.cdo.releng.internal.setup.ui;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.internal.setup.targlets.P2;
import org.eclipse.emf.cdo.releng.internal.setup.util.UpdateUtil;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.SubMonitor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IArtifactKey;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.IRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.ArtifactKeyQuery;
import org.eclipse.equinox.p2.repository.artifact.IArtifactDescriptor;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepository;
import org.eclipse.equinox.p2.repository.artifact.IArtifactRepositoryManager;
import org.eclipse.equinox.p2.repository.artifact.IFileArtifactRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author Eike Stepper
 */
public final class BundlePoolAnalyzer
{
  private final Listener listener;

  private final Map<File, BundlePool> bundlePools = new HashMap<File, BundlePool>();

  private final List<Job> analyzeProfileJobs = new ArrayList<Job>();

  private Set<URI> repositoryURIs;

  public BundlePoolAnalyzer(Listener listener)
  {
    this.listener = listener;

    IProfileRegistry profileRegistry = P2.getProfileRegistry();
    for (IProfile p2Profile : profileRegistry.getProfiles())
    {
      String installFolder = p2Profile.getProperty(IProfile.PROP_INSTALL_FOLDER);
      String cache = p2Profile.getProperty(IProfile.PROP_CACHE);
      if (cache != null && !cache.equals(installFolder))
      {
        File location = new File(cache);

        BundlePool bundlePool = bundlePools.get(location);
        if (bundlePool == null)
        {
          bundlePool = new BundlePool(this, location);
          bundlePools.put(location, bundlePool);
        }

        bundlePool.addProfile(p2Profile, installFolder);
      }
    }

    listener.analyzerChanged(this);

    for (BundlePool bundlePool : bundlePools.values())
    {
      Job job = bundlePool.analyze();
      analyzeProfileJobs.add(job);
    }
  }

  public void dispose()
  {
    for (Job job : analyzeProfileJobs)
    {
      job.cancel();
    }

    analyzeProfileJobs.clear();
    bundlePools.clear();
  }

  public Map<File, BundlePool> getBundlePools()
  {
    return bundlePools;
  }

  public Set<URI> getRepositoryURIs()
  {
    if (repositoryURIs == null)
    {
      repositoryURIs = new LinkedHashSet<URI>();

      IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_ALL);
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_DISABLED);
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_LOCAL);
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_NON_LOCAL);
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_SYSTEM);
      addURIs(repositoryURIs, repositoryManager, IRepositoryManager.REPOSITORIES_NON_SYSTEM);

      for (BundlePool bundlePool : bundlePools.values())
      {
        // Don't use possibly damaged local bundle pools for damage repair
        repositoryURIs.remove(bundlePool.getLocation().toURI());
      }
    }

    return repositoryURIs;
  }

  private void addURIs(Set<URI> repos, IArtifactRepositoryManager repositoryManager, int flag)
  {
    for (URI uri : repositoryManager.getKnownRepositories(flag))
    {
      repos.add(uri);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Listener
  {
    public void analyzerChanged(BundlePoolAnalyzer analyzer);

    public void bundlePoolChanged(BundlePool bundlePool, boolean artifacts, boolean profiles);

    public void profileChanged(Profile profile);

    public void artifactChanged(Artifact artifact);
  }

  /**
   * @author Eike Stepper
   */
  public static final class BundlePool implements Comparable<BundlePool>
  {
    private final BundlePoolAnalyzer analyzer;

    private final File location;

    private final Set<URI> repositoryURIs = new LinkedHashSet<URI>();

    private final List<Profile> profiles = new ArrayList<Profile>();

    private final Map<IArtifactKey, Artifact> artifacts = new HashMap<IArtifactKey, Artifact>();

    private final Set<Artifact> unusedArtifacts = new HashSet<Artifact>();

    private final Set<Artifact> damagedArchives = new HashSet<Artifact>();

    private int damagedArchivesPercent;

    public BundlePool(BundlePoolAnalyzer agent, File location)
    {
      analyzer = agent;
      this.location = location;
    }

    public IFileArtifactRepository getP2BundlePool(IProgressMonitor monitor)
    {
      try
      {
        IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
        return (IFileArtifactRepository)repositoryManager.loadRepository(location.toURI(), monitor);
      }
      catch (ProvisionException ex)
      {
        throw new IllegalStateException(ex);
      }
    }

    public BundlePoolAnalyzer getAnalyzer()
    {
      return analyzer;
    }

    public File getLocation()
    {
      return location;
    }

    public Set<URI> getRepositoryURIs()
    {
      return repositoryURIs;
    }

    public int getProfilesCount()
    {
      synchronized (this)
      {
        return profiles.size();
      }
    }

    public Profile[] getProfiles()
    {
      synchronized (profiles)
      {
        return profiles.toArray(new Profile[profiles.size()]);
      }
    }

    public int getArtifactCount()
    {
      synchronized (this)
      {
        return artifacts.size();
      }
    }

    public Artifact[] getArtifacts()
    {
      synchronized (this)
      {
        Artifact[] array = artifacts.values().toArray(new Artifact[artifacts.size()]);
        Arrays.sort(array);
        return array;
      }
    }

    public Artifact getArtifact(IArtifactKey key)
    {
      synchronized (this)
      {
        return artifacts.get(key);
      }
    }

    public int getUnusedArtifactsCount()
    {
      synchronized (this)
      {
        return unusedArtifacts.size();
      }
    }

    public Artifact[] getUnusedArtifacts()
    {
      Artifact[] array;
      synchronized (this)
      {
        array = unusedArtifacts.toArray(new Artifact[unusedArtifacts.size()]);
      }

      Arrays.sort(array);
      return array;
    }

    public int getDamagedArchivesPercent()
    {
      return damagedArchivesPercent;
    }

    public int getDamagedArchivesCount()
    {
      synchronized (damagedArchives)
      {
        return damagedArchives.size();
      }
    }

    public Artifact[] getDamagedArchives()
    {
      Artifact[] array;
      synchronized (this)
      {
        array = damagedArchives.toArray(new Artifact[damagedArchives.size()]);
      }

      Arrays.sort(array);
      return array;
    }

    public int compareTo(BundlePool o)
    {
      return location.getAbsolutePath().compareTo(o.getLocation().getAbsolutePath());
    }

    @Override
    public String toString()
    {
      return location.toString();
    }

    Profile addProfile(IProfile p2Profile, String installFolder)
    {
      Profile profile = new Profile(this, p2Profile, installFolder == null ? null : new File(installFolder));
      repositoryURIs.addAll(profile.getRepositoryURIs());

      synchronized (this)
      {
        profiles.add(profile);
        Collections.sort(profiles);
      }

      return profile;
    }

    Job analyze()
    {
      Job job = new Job("Analyzing bundle pool " + location)
      {
        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
          analyze(monitor);
          return Status.OK_STATUS;
        }
      };

      job.schedule();
      return job;
    }

    private void analyze(IProgressMonitor monitor)
    {
      IFileArtifactRepository p2BundlePool = getP2BundlePool(monitor);
      for (IArtifactKey key : p2BundlePool.query(ArtifactKeyQuery.ALL_KEYS, monitor))
      {
        checkCancelation(monitor);

        File file = p2BundlePool.getArtifactFile(key);
        Artifact artifact = new Artifact(this, key, file);

        synchronized (this)
        {
          artifacts.put(key, artifact);
        }
      }

      analyzer.listener.bundlePoolChanged(this, true, false);

      for (Profile profile : getProfiles())
      {
        checkCancelation(monitor);
        profile.analyze(monitor);
      }

      analyzeUnusedArtifacts(monitor);
      analyzeDamagedArchives(monitor);
    }

    private void analyzeUnusedArtifacts(IProgressMonitor monitor)
    {
      for (Artifact artifact : getArtifacts())
      {
        checkCancelation(monitor);
        if (analyzeUnusedArtifact(artifact, monitor))
        {
          synchronized (this)
          {
            unusedArtifacts.add(artifact);
          }

          analyzer.listener.bundlePoolChanged(this, false, false);
        }
      }

      analyzer.listener.bundlePoolChanged(this, true, false);
    }

    private boolean analyzeUnusedArtifact(Artifact artifact, IProgressMonitor monitor)
    {
      for (Profile profile : getProfiles())
      {
        checkCancelation(monitor);
        if (profile.getArtifacts().contains(artifact))
        {
          return false;
        }
      }

      return true;
    }

    private void analyzeDamagedArchives(IProgressMonitor monitor)
    {
      Artifact[] artifacts = getArtifacts();
      int total = artifacts.length;
      int i = 0;

      for (Artifact artifact : artifacts)
      {
        checkCancelation(monitor);

        int percent = ++i * 100 / total;
        if (percent != damagedArchivesPercent)
        {
          damagedArchivesPercent = percent;
          analyzer.listener.bundlePoolChanged(this, false, false);
        }

        synchronized (artifact)
        {
          IArtifactKey key = artifact.getKey();
          if (getArtifact(key) == null)
          {
            // Continue with next artifact if this artifact was deleted meanwhile
            continue;
          }
        }

        File file = artifact.getFile();
        if (file != null)
        {
          monitor.subTask("Validating " + file);
          if (isDamaged(file, artifact.getType()))
          {
            synchronized (this)
            {
              damagedArchives.add(artifact);
            }

            analyzer.listener.bundlePoolChanged(this, false, false);

            artifact.setDamaged();
            analyzer.listener.artifactChanged(artifact);
          }
        }
      }

      analyzer.listener.bundlePoolChanged(this, false, false);
    }

    private static boolean isDamaged(File file, String type)
    {
      if (!file.exists())
      {
        return true;
      }

      if (Artifact.ARCHIVE.equals(type))
      {
        ZipInputStream in = null;
        ZipFile zip = null;

        try
        {
          zip = new ZipFile(file);
          in = new ZipInputStream(new FileInputStream(file));

          ZipEntry entry = in.getNextEntry();
          if (entry == null)
          {
            return true;
          }

          while (entry != null)
          {
            entry.getName();
            entry.getCompressedSize();
            entry.getCrc();

            zip.getInputStream(entry);
            entry = in.getNextEntry();
          }
        }
        catch (Exception ex)
        {
          return true;
        }
        finally
        {
          IOUtil.close(zip);
          IOUtil.close(in);
        }
      }

      return false;
    }

    private static void checkCancelation(IProgressMonitor monitor)
    {
      if (monitor.isCanceled())
      {
        throw new OperationCanceledException();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Profile implements Comparable<Profile>
  {
    public static final String ECLIPSE = "Eclipse";

    public static final String TARGLET = "Targlet";

    public static final String UNKNOWN = "Unknown";

    private final BundlePool bundlePool;

    private final IProfile p2Profile;

    private final File installFolder;

    private final String type;

    private final Set<URI> repositoryURIs = new LinkedHashSet<URI>();

    private final Set<Artifact> artifacts = new HashSet<Artifact>();

    private final Set<Artifact> damagedArchives = new HashSet<Artifact>();

    public Profile(BundlePool bundlePool, IProfile p2Profile, File installFolder)
    {
      this.bundlePool = bundlePool;
      this.p2Profile = p2Profile;
      this.installFolder = installFolder;

      if (P2.isTargletProfile(p2Profile))
      {
        type = TARGLET;
      }
      else if (installFolder != null)
      {
        type = ECLIPSE;
      }
      else
      {
        type = UNKNOWN;
      }

      String repoList = p2Profile.getProperty(UpdateUtil.PROP_REPO_LIST);
      if (repoList != null)
      {
        StringTokenizer tokenizer = new StringTokenizer(repoList, ",");
        while (tokenizer.hasMoreTokens())
        {
          String uri = tokenizer.nextToken();

          try
          {
            repositoryURIs.add(new URI(uri));
          }
          catch (URISyntaxException ex)
          {
            Activator.log(ex);
          }
        }
      }
    }

    public BundlePool getBundlePool()
    {
      return bundlePool;
    }

    public String getID()
    {
      return p2Profile.getProfileId();
    }

    public File getInstallFolder()
    {
      return installFolder;
    }

    public String getType()
    {
      return type;
    }

    public Set<URI> getRepositoryURIs()
    {
      return repositoryURIs;
    }

    public Set<Artifact> getArtifacts()
    {
      return artifacts;
    }

    public boolean isDamaged()
    {
      synchronized (bundlePool)
      {
        return !damagedArchives.isEmpty();
      }
    }

    public int getDamagedArchivesCount()
    {
      synchronized (bundlePool)
      {
        return damagedArchives.size();
      }
    }

    public Artifact[] getDamagedArchives()
    {
      synchronized (bundlePool)
      {
        return damagedArchives.toArray(new Artifact[damagedArchives.size()]);
      }
    }

    public int compareTo(Profile o)
    {
      return getID().compareTo(o.getID());
    }

    @Override
    public String toString()
    {
      return getID();
    }

    void analyze(IProgressMonitor monitor)
    {
      for (IInstallableUnit iu : p2Profile.query(QueryUtil.createIUAnyQuery(), monitor))
      {
        for (IArtifactKey key : iu.getArtifacts())
        {
          Artifact artifact = bundlePool.getArtifact(key);
          if (artifact != null)
          {
            synchronized (bundlePool)
            {
              artifacts.add(artifact);
              artifact.addProfile(this);
            }

            bundlePool.analyzer.listener.profileChanged(this);
          }
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Artifact implements Comparable<Artifact>
  {
    public static final String FOLDER = "Folder";

    public static final String ARCHIVE = "Archive";

    public static final String FILE = "File";

    private final BundlePool bundlePool;

    private final IArtifactKey key;

    private final File file;

    private final String relativePath;

    private final List<Profile> profiles = new ArrayList<Profile>();

    private boolean damaged;

    public Artifact(BundlePool bundlePool, IArtifactKey key, File file)
    {
      this.bundlePool = bundlePool;
      this.key = key;
      this.file = file;

      int start = bundlePool.location.getAbsolutePath().length();
      relativePath = file.getAbsolutePath().substring(start + 1);
    }

    public boolean isUnused()
    {
      return profiles.isEmpty();
    }

    public boolean isDamaged()
    {
      return damaged;
    }

    public BundlePool getBundlePool()
    {
      return bundlePool;
    }

    public IArtifactKey getKey()
    {
      return key;
    }

    public File getFile()
    {
      return file;
    }

    public boolean isFolder()
    {
      return file.isDirectory();
    }

    public String getType()
    {
      if (isFolder())
      {
        return FOLDER;
      }

      String path = file.getPath();
      if (path.endsWith(".jar") || path.endsWith(".zip"))
      {
        return ARCHIVE;
      }

      return FILE;
    }

    public String getRelativePath()
    {
      return relativePath;
    }

    public List<Profile> getProfiles()
    {
      return profiles;
    }

    public int compareTo(Artifact o)
    {
      return relativePath.compareTo(o.relativePath);
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (key == null ? 0 : key.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      Artifact other = (Artifact)obj;
      if (key == null)
      {
        if (other.key != null)
        {
          return false;
        }
      }
      else if (!key.equals(other.key))
      {
        return false;
      }

      return true;
    }

    @Override
    public String toString()
    {
      return key.toString();
    }

    void addProfile(Profile profile)
    {
      profiles.add(profile);
    }

    void setDamaged()
    {
      damaged = true;
      for (Profile profile : profiles)
      {
        synchronized (bundlePool)
        {
          profile.damagedArchives.add(this);
        }

        bundlePool.analyzer.listener.profileChanged(profile);
      }
    }

    public synchronized void delete(IProgressMonitor monitor)
    {
      IFileArtifactRepository p2BundlePool = bundlePool.getP2BundlePool(monitor);
      p2BundlePool.removeDescriptor(key, monitor);

      if (isUnused())
      {
        damaged = false;

        synchronized (bundlePool)
        {
          bundlePool.unusedArtifacts.remove(this);
          bundlePool.artifacts.remove(key);
          bundlePool.damagedArchives.remove(this);
        }

        bundlePool.analyzer.listener.bundlePoolChanged(bundlePool, true, false);
      }
      else
      {
        damaged = true;
        synchronized (bundlePool)
        {
          bundlePool.damagedArchives.add(this);
        }

        bundlePool.analyzer.listener.bundlePoolChanged(bundlePool, false, false);
        bundlePool.analyzer.listener.artifactChanged(this);

        for (Profile profile : profiles)
        {
          synchronized (bundlePool)
          {
            profile.damagedArchives.add(this);
          }

          bundlePool.analyzer.listener.profileChanged(profile);
        }
      }
    }

    public synchronized boolean repair(IProgressMonitor monitor)
    {
      if (!damaged)
      {
        return false;
      }

      if (isUnused())
      {
        delete(monitor);
        return true;
      }

      if (doRepair(monitor))
      {
        damaged = false;
        bundlePool.analyzer.listener.artifactChanged(this);

        synchronized (bundlePool)
        {
          bundlePool.damagedArchives.remove(this);
        }

        for (Profile profile : profiles)
        {
          synchronized (bundlePool)
          {
            profile.damagedArchives.remove(this);
          }

          bundlePool.analyzer.listener.profileChanged(profile);
        }

        bundlePool.analyzer.listener.bundlePoolChanged(bundlePool, false, false);
        return true;
      }

      return false;
    }

    private boolean doRepair(IProgressMonitor monitor)
    {
      Set<URI> repositoryURIs = bundlePool.getRepositoryURIs();
      SubMonitor progress = SubMonitor.convert(monitor, 2 * repositoryURIs.size()).detectCancelation();
      if (doRepair(repositoryURIs, progress))
      {
        return true;
      }

      Set<URI> allURIs = new LinkedHashSet<URI>(bundlePool.analyzer.getRepositoryURIs());
      allURIs.removeAll(repositoryURIs);
      if (doRepair(allURIs, progress))
      {
        return true;
      }

      return false;
    }

    private boolean doRepair(Set<URI> repositoryURIs, SubMonitor progress)
    {
      for (URI uri : repositoryURIs)
      {
        if (doRepair(uri, progress))
        {
          return true;
        }
      }

      return false;
    }

    private boolean doRepair(URI repositoryURI, SubMonitor progress)
    {
      try
      {
        IArtifactRepositoryManager repositoryManager = P2.getArtifactRepositoryManager();
        IArtifactRepository repository = repositoryManager.loadRepository(repositoryURI, progress.newChild());

        for (IArtifactDescriptor descriptor : repository.getArtifactDescriptors(key))
        {
          File tmp = new File(file.getAbsolutePath() + ".tmp");
          FileOutputStream destination = null;

          try
          {
            destination = new FileOutputStream(tmp);

            IStatus status = repository.getArtifact(descriptor, destination, progress.newChild());
            if (status.getSeverity() == IStatus.OK)
            {
              IOUtil.close(destination);
              IOUtil.copyFile(tmp, file);
              return true;
            }
          }
          finally
          {
            IOUtil.close(destination);
            if (!tmp.delete())
            {
              tmp.deleteOnExit();
            }
          }
        }
      }
      catch (OperationCanceledException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }

      return false;
    }
  }
}
