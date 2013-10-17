package org.eclipse.emf.cdo.releng.projectconfig.presentation.handlers;

import org.eclipse.emf.cdo.releng.projectconfig.util.ProjectConfigUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ApplyPreferenceProfilesHandler extends AbstractHandler
{
  public ApplyPreferenceProfilesHandler()
  {
  }

  /**
   * the command has been executed, so extract extract the needed information
   * from the application context.
   */
  public Object execute(ExecutionEvent event) throws ExecutionException
  {
    ProjectConfigUtil.getWorkspaceConfiguration().applyPreferenceProfiles();
    return null;
  }
}
