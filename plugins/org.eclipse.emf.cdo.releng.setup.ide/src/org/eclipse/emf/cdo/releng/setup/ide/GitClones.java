/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.ide;

import org.eclipse.emf.cdo.releng.setup.Branch;
import org.eclipse.emf.cdo.releng.setup.GitClone;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.helper.Progress;

import org.eclipse.emf.common.util.URI;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @author Eike Stepper
 */
public final class GitClones
{
  private static final SetupContext CONTEXT = Activator.getDefault();

  public static void init() throws Exception
  {
    Setup setup = CONTEXT.getSetup();
    Branch branch = setup.getBranch();
    String userName = setup.getPreferences().getUserName();

    for (GitClone gitClone : branch.getGitClones())
    {
      File workDir = CONTEXT.getWorkDir(gitClone);

      Repository repository = null;
      Git git;

      try
      {
        if (workDir.isDirectory())
        {
          Progress.log().addLine("Opening Git clone " + workDir);
          git = Git.open(workDir);
        }
        else
        {
          URI baseURI = URI.createURI(gitClone.getRemoteURI());
          String remote = URI.createHierarchicalURI(baseURI.scheme(), userName + "@" + baseURI.authority(),
              baseURI.device(), baseURI.segments(), baseURI.query(), baseURI.fragment()).toString();
          Progress.log().addLine("Cloning Git repo " + remote + " to " + workDir);

          CloneCommand command = Git.cloneRepository();
          command.setURI(remote);
          command.setRemote("origin");
          command.setBranchesToClone(Collections.singleton(gitClone.getCheckoutBranch()));
          command.setBranch(gitClone.getCheckoutBranch());
          command.setDirectory(workDir);
          command.setTimeout(10);
          command.setProgressMonitor(new ProgressLogWrapper());

          git = command.call();
        }

        repository = git.getRepository();
        StoredConfig config = repository.getConfig();

        String gerritQueue = "refs/for/" + gitClone.getCheckoutBranch();

        List<RemoteConfig> remoteConfigs = RemoteConfig.getAllRemoteConfigs(config);
        for (RemoteConfig remoteConfig : remoteConfigs)
        {
          if ("origin".equals(remoteConfig.getName()))
          {
            List<RefSpec> pushRefSpecs = remoteConfig.getPushRefSpecs();
            if (!hasGerritPushRefSpec(pushRefSpecs, gerritQueue))
            {
              RefSpec refSpec = new RefSpec("HEAD:" + gerritQueue);
              Progress.log().addLine("Adding push ref spec: " + refSpec);

              remoteConfig.addPushRefSpec(refSpec);
              remoteConfig.update(config);
              config.save();
            }
          }
        }
      }
      finally
      {
        if (repository != null)
        {
          repository.close();
        }
      }
    }
  }

  private static boolean hasGerritPushRefSpec(List<RefSpec> pushRefSpecs, String gerritQueue)
  {
    for (RefSpec refSpec : pushRefSpecs)
    {
      if (refSpec.getDestination().equals(gerritQueue))
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ProgressLogWrapper implements ProgressMonitor
  {
    public void update(int completed)
    {
    }

    public void start(int totalTasks)
    {
    }

    public boolean isCancelled()
    {
      return Progress.log().isCancelled();
    }

    public void endTask()
    {
    }

    public void beginTask(String title, int totalWork)
    {
      Progress.log().addLine(title);
    }
  }
}
