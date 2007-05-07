package org.eclipse.emf.cdo.internal.ui.wizards.steps;

import org.eclipse.net4j.ui.wizards.ParallelStep;
import org.eclipse.net4j.ui.wizards.StringStep;

/**
 * @author Eike Stepper
 */
public class RepoNameStep extends ParallelStep
{
  public static final String KEY_REPO_NAME = "Repository name";

  public RepoNameStep()
  {
    add(new StringStep(KEY_REPO_NAME));
  }

  public String getRepoName()
  {
    Object value = getWizard().getSingleContextValue(KEY_REPO_NAME);
    return value instanceof String ? (String)value : null;
  }
}