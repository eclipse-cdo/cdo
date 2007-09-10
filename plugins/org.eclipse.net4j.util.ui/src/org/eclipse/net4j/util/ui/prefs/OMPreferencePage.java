package org.eclipse.net4j.util.ui.prefs;

import org.eclipse.net4j.util.om.pref.OMPreferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Eike Stepper
 */
public abstract class OMPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
  private IWorkbench workbench;

  private OMPreferenceStore preferenceStore;

  public OMPreferencePage(OMPreferences preferences)
  {
    preferenceStore = new OMPreferenceStore(preferences);
  }

  public OMPreferences getPreferences()
  {
    return preferenceStore.getPreferences();
  }

  public IWorkbench getWorkbench()
  {
    return workbench;
  }

  public void init(IWorkbench workbench)
  {
    this.workbench = workbench;
  }

  @Override
  protected IPreferenceStore doGetPreferenceStore()
  {
    return preferenceStore;
  }
}