/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version.bucky;

import org.eclipse.emf.cdo.releng.version.BuildState;
import org.eclipse.emf.cdo.releng.version.Release;
import org.eclipse.emf.cdo.releng.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.version.VersionValidator;

import org.eclipse.buckminster.cvspkg.internal.CVSSession;
import org.eclipse.buckminster.cvspkg.internal.RepositoryMetaData;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.team.core.RepositoryProvider;
import org.eclipse.team.internal.ccvs.core.CVSTag;
import org.eclipse.team.internal.ccvs.core.CVSTeamProvider;
import org.eclipse.team.internal.ccvs.core.ICVSFolder;
import org.eclipse.team.internal.ccvs.core.ICVSResource;
import org.eclipse.team.internal.ccvs.core.resources.CVSWorkspaceRoot;
import org.eclipse.team.internal.ccvs.core.syncinfo.FolderSyncInfo;

import java.util.Date;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class BuckyValidator extends VersionValidator
{
  public BuckyValidator()
  {
  }

  @Override
  public void updateBuildState(BuildState buildState, String releasePath, Release release, IProject project,
      IResourceDelta delta, IProgressMonitor monitor) throws Exception
  {
    if (delta != null)
    {
      buildState.setChangedSinceRelease(true);
      return;
    }

    LocalModificationVisitor visitor = new LocalModificationVisitor(monitor);
    project.accept(visitor);
    if (visitor.isChanged())
    {
      buildState.setChangedSinceRelease(true);
      return;
    }

    CVSTeamProvider provider = (CVSTeamProvider)RepositoryProvider.getProvider(project);
    CVSWorkspaceRoot root = provider.getCVSWorkspaceRoot();
    String location = root.getRemoteLocation().getLocation(false);
    String module = root.getLocalRoot().getRepositoryRelativePath();
    String repositoryLocation = location + "," + module;

    VersionBuilder.trace("Bucky: Getting release timestamp...");
    CVSTag releaseTag = new CVSTag(release.getTag(), CVSTag.VERSION);
    Date releaseModification = getLastModification(repositoryLocation, releaseTag, monitor);

    ICVSFolder cvsProject = CVSWorkspaceRoot.getCVSFolderFor(project);
    FolderSyncInfo syncInfo = cvsProject.getFolderSyncInfo();
    CVSTag projectTag = syncInfo.getTag();
    if (projectTag == null)
    {
      projectTag = new CVSTag();
    }

    VersionBuilder.trace("Bucky: Getting project timestamp...");
    Date projectModification = getLastModification(repositoryLocation, projectTag, monitor);
    buildState.setChangedSinceRelease(!releaseModification.equals(projectModification));
  }

  private Date getLastModification(String repositoryLocation, CVSTag tag, IProgressMonitor monitor)
      throws CoreException
  {
    org.eclipse.buckminster.cvspkg.internal.CVSSession session = null;

    try
    {
      session = new CVSSession(repositoryLocation);
      RepositoryMetaData metaData = RepositoryMetaData.getMetaData(session, tag, monitor);
      return metaData.getLastModification();
    }
    finally
    {
      if (session != null)
      {
        session.close();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class LocalModificationVisitor implements IResourceVisitor
  {
    private IProgressMonitor monitor;

    private boolean changed;

    private LocalModificationVisitor(IProgressMonitor monitor)
    {
      this.monitor = monitor;
    }

    public boolean isChanged()
    {
      return changed;
    }

    public boolean visit(IResource resource) throws CoreException
    {
      ICVSResource cvsResource = CVSWorkspaceRoot.getCVSResourceFor(resource);
      if (cvsResource.isManaged())
      {
        VersionBuilder.trace("Bucky: " + resource.getFullPath());
        if (cvsResource.isModified(monitor))
        {
          changed = true;
          return false;
        }
      }

      return true;
    }
  }
}
