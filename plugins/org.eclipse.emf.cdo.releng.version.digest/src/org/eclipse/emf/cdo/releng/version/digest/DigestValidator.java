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
package org.eclipse.emf.cdo.releng.version.digest;

import org.eclipse.emf.cdo.releng.version.BuildState;
import org.eclipse.emf.cdo.releng.version.Release;
import org.eclipse.emf.cdo.releng.version.VersionBuilder;
import org.eclipse.emf.cdo.releng.version.VersionValidator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.build.IBuild;
import org.eclipse.pde.core.build.IBuildEntry;
import org.eclipse.pde.core.build.IBuildModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class DigestValidator extends VersionValidator
{
  private static final byte[] BUFFER = new byte[8192];

  public DigestValidator()
  {
  }

  @Override
  public void updateBuildState(BuildState buildState, String releasePath, Release release, IProject project,
      IResourceDelta delta, IPluginModelBase pluginModel, IProgressMonitor monitor) throws Exception
  {
    DigestValidatorState validatorState = (DigestValidatorState)buildState.getValidatorState();
    beforeValidation(validatorState, pluginModel);
    if (validatorState == null || delta == null)
    {
      VersionBuilder.trace("Digest: Full validation...");
      buildState.setValidatorState(null);
      validatorState = validateFull(project, null, pluginModel, monitor);
    }
    else
    {
      VersionBuilder.trace("Digest: Delta validation...");
      validatorState = validateDelta(delta, validatorState, pluginModel, monitor);
    }

    afterValidation(validatorState);
    if (validatorState == null)
    {
      throw new IllegalStateException("No validation state");
    }

    byte[] validatorDigest = validatorState.getDigest();
    VersionBuilder.trace("DIGEST  = " + formatDigest(validatorDigest));

    byte[] releaseDigest = getReleaseDigest(releasePath, project.getName());
    VersionBuilder.trace("RELEASE = " + formatDigest(releaseDigest));

    buildState.setChangedSinceRelease(!MessageDigest.isEqual(validatorDigest, releaseDigest));
    buildState.setValidatorState(validatorState);
  }

  public DigestValidatorState validateFull(IResource resource, DigestValidatorState parentState,
      IPluginModelBase pluginModel, IProgressMonitor monitor) throws Exception
  {
    if (resource.getType() != IResource.PROJECT && !isConsidered(resource))
    {
      return null;
    }

    VersionBuilder.trace("Digest: " + resource.getFullPath());
    DigestValidatorState result = new DigestValidatorState();
    result.setName(resource.getName());
    result.setParent(parentState);

    if (resource instanceof IContainer)
    {
      IContainer container = (IContainer)resource;
      List<DigestValidatorState> memberStates = new ArrayList<DigestValidatorState>();
      for (IResource member : container.members())
      {
        DigestValidatorState memberState = validateFull(member, result, pluginModel, monitor);
        if (memberState != null)
        {
          memberStates.add(memberState);
        }
      }

      byte[] digest = getFolderDigest(memberStates);
      VersionBuilder.trace("Considered: " + container.getFullPath() + " --> " + formatDigest(digest));
      result.setDigest(digest);
      result.setChildren(memberStates.toArray(new DigestValidatorState[memberStates.size()]));
    }
    else
    {
      IFile file = (IFile)resource;
      byte[] digest = getFileDigest(file);
      VersionBuilder.trace("Considered: " + file.getFullPath() + " --> " + formatDigest(digest));
      result.setDigest(digest);
    }

    return result;
  }

  public DigestValidatorState validateDelta(IResourceDelta delta, DigestValidatorState validatorState,
      IPluginModelBase pluginModel, IProgressMonitor monitor) throws Exception
  {
    IResource resource = delta.getResource();
    if (!resource.exists() || resource.getType() != IResource.PROJECT && !isConsidered(resource))
    {
      return null;
    }

    DigestValidatorState result = validatorState;
    switch (delta.getKind())
    {
    case IResourceDelta.ADDED:
      result = new DigestValidatorState();
      result.setName(resource.getName());

      //$FALL-THROUGH$
    case IResourceDelta.CHANGED:
      if (resource instanceof IContainer)
      {
        Set<DigestValidatorState> memberStates = new HashSet<DigestValidatorState>();
        for (IResourceDelta memberDelta : delta.getAffectedChildren())
        {
          IResource memberResource = memberDelta.getResource();
          DigestValidatorState memberState = validatorState.getChild(memberResource.getName());
          DigestValidatorState newMemberState = validateDelta(memberDelta, memberState, pluginModel, monitor);
          if (newMemberState != null)
          {
            newMemberState.setParent(result);
            memberStates.add(newMemberState);
          }
        }

        IContainer container = (IContainer)resource;
        for (DigestValidatorState oldChild : validatorState.getChildren())
        {
          IResource member = container.findMember(oldChild.getName());
          if (member != null)
          {
            memberStates.add(oldChild);
          }
        }

        byte[] digest = getFolderDigest(memberStates);
        result.setDigest(digest);
        result.setChildren(memberStates.toArray(new DigestValidatorState[memberStates.size()]));
        // VersionBuilder.trace("   " + delta.getFullPath() + "  -->  " + TestResourceChangeListener.getKind(delta) +
        // " " + TestResourceChangeListener.getFlags(delta));
      }
      else
      {
        boolean changed = result == validatorState;
        if (changed && (delta.getFlags() & IResourceDelta.CONTENT) == 0)
        {
          return validatorState;
        }

        IFile file = (IFile)resource;
        byte[] digest = getFileDigest(file);
        result.setDigest(digest);
        // VersionBuilder.trace("   " + delta.getFullPath() + "  -->  " + TestResourceChangeListener.getKind(delta) +
        // " " + TestResourceChangeListener.getFlags(delta));
      }

      break;

    case IResourceDelta.REMOVED:
      result = null;
    }

    return result;
  }

  public void beforeValidation(DigestValidatorState validatorState, IPluginModelBase pluginModel) throws Exception
  {
  }

  public void afterValidation(DigestValidatorState validatorState) throws Exception
  {
  }

  protected boolean isConsidered(IResource resource)
  {
    return !resource.isDerived();
  }

  private byte[] getReleaseDigest(String releasePath, String name) throws IOException, CoreException,
      ClassNotFoundException
  {
    IFile file = getDigestFile(new Path(releasePath));
    if (!file.exists())
    {
      throw new IllegalStateException("Digest file not found: " + file.getFullPath());
    }

    ObjectInputStream stream = null;

    try
    {
      stream = new ObjectInputStream(file.getContents());

      @SuppressWarnings("unchecked")
      Map<String, byte[]> map = (Map<String, byte[]>)stream.readObject();
      return map.get(name);
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }
  }

  private byte[] getFolderDigest(Collection<DigestValidatorState> states) throws Exception
  {
    List<DigestValidatorState> list = new ArrayList<DigestValidatorState>(states);
    Collections.sort(list);

    MessageDigest digest = MessageDigest.getInstance("SHA");
    for (DigestValidatorState state : list)
    {
      byte[] bytes = state.getDigest();
      if (bytes != null)
      {
        digest.update(state.getName().getBytes());
        digest.update(bytes);
      }
    }

    return digest.digest();
  }

  private byte[] getFileDigest(IFile file) throws Exception
  {
    InputStream stream = null;

    try
    {
      final MessageDigest digest = MessageDigest.getInstance("SHA-1");
      stream = new FilterInputStream(file.getContents())
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

      while (stream.read(BUFFER) != -1)
      {
        // Do nothing
      }

      return digest.digest();
    }
    finally
    {
      if (stream != null)
      {
        try
        {
          stream.close();
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }
  }

  private String formatDigest(byte[] digest)
  {
    StringBuilder builder = new StringBuilder();
    for (byte b : digest)
    {
      if (builder.length() != 0)
      {
        builder.append(", ");
      }

      builder.append("(byte)");
      builder.append(b);
    }

    return builder.toString();
  }

  public static IFile getDigestFile(IPath releasePath)
  {
    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(releasePath);
    return file.getParent().getFile(new Path("release.digest"));
  }

  /**
   * @author Eike Stepper
   */
  public static class BuildModel extends DigestValidator
  {
    private Set<String> considered = new HashSet<String>();

    public BuildModel()
    {
    }

    @Override
    public void beforeValidation(DigestValidatorState validatorState, IPluginModelBase pluginModel) throws Exception
    {
      considered.clear();
      considered.add("");

      IBuild build = getBuild(pluginModel);
      IBuildEntry binIncludes = build.getEntry(IBuildEntry.BIN_INCLUDES);
      if (binIncludes != null)
      {
        for (String binInclude : binIncludes.getTokens())
        {
          IBuildEntry sources = build.getEntry("source." + binInclude);
          if (sources != null)
          {
            for (String source : sources.getTokens())
            {
              consider(source);
            }
          }
          else
          {
            consider(binInclude);
          }
        }
      }
    }

    @Override
    public void afterValidation(DigestValidatorState validatorState) throws Exception
    {
      considered.clear();
    }

    @Override
    protected boolean isConsidered(IResource resource)
    {
      IPath path = resource.getProjectRelativePath();
      while (!path.isEmpty())
      {
        if (considered.contains(path.toString()))
        {
          return true;
        }

        path = path.removeLastSegments(1);
      }

      return false;
    }

    @SuppressWarnings("restriction")
    private IBuild getBuild(IPluginModelBase pluginModel) throws CoreException
    {
      IBuildModel buildModel = PluginRegistry.createBuildModel(pluginModel);
      if (buildModel == null)
      {
        IProject project = pluginModel.getUnderlyingResource().getProject();
        IFile buildFile = org.eclipse.pde.internal.core.project.PDEProject.getBuildProperties(project);
        if (buildFile.exists())
        {
          buildModel = new org.eclipse.pde.internal.core.build.WorkspaceBuildModel(buildFile);
          buildModel.load();
        }
      }

      if (buildModel == null)
      {
        throw new IllegalStateException("Could not determine build model for "
            + pluginModel.getBundleDescription().getSymbolicName());
      }

      IBuild build = buildModel.getBuild();
      if (build == null)
      {
        throw new IllegalStateException("Could not determine build model for "
            + pluginModel.getBundleDescription().getSymbolicName());
      }

      return build;
    }

    private void consider(String path)
    {
      if (path.endsWith("/"))
      {
        path = path.substring(0, path.length() - 1);
      }

      considered.add(path);
    }
  }
}
