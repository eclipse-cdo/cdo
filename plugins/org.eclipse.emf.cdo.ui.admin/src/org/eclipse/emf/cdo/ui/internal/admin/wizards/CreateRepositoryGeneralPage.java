/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.wizards;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.ui.internal.admin.StoreType;
import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.util.List;
import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class CreateRepositoryGeneralPage extends AbstractCreateRepositoryWizardPage
{
  public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$

  private final Map<String, StoreType> storeTypes;

  private Text nameText;

  private Button enableAuditingCheckbox;

  private Button enableBranchingCheckbox;

  private Button referentialIntegrityCheckbox;

  private Button allowInterruptQueriesCheckbox;

  private Text overrideUUIDText;

  private ComboViewer idGenerationLocationCombo;

  private Button securityCheckbox;

  private Button homeFoldersCheckbox;

  private ComboViewer storeCombo;

  private StoreType storeType;

  public CreateRepositoryGeneralPage(String pageName, List<StoreType> storeTypes)
  {
    super(pageName);
    setTitle(Messages.CreateRepositoryGeneralPage_5);
    setMessage(Messages.CreateRepositoryGeneralPage_0);
    this.storeTypes = mapStoreTypes(storeTypes);
  }

  @Override
  protected void createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout());

    Group properties = group(composite, Messages.CreateRepositoryGeneralPage_6);
    nameText = text(properties, Messages.CreateRepositoryGeneralPage_1);
    enableAuditingCheckbox = checkbox(properties, Messages.CreateRepositoryGeneralPage_7);
    enableBranchingCheckbox = checkbox(properties, Messages.CreateRepositoryGeneralPage_8);
    referentialIntegrityCheckbox = checkbox(properties, Messages.CreateRepositoryGeneralPage_10);
    allowInterruptQueriesCheckbox = checkbox(properties, Messages.CreateRepositoryGeneralPage_11);
    overrideUUIDText = text(properties, Messages.CreateRepositoryGeneralPage_12);
    idGenerationLocationCombo = combo(properties, Messages.CreateRepositoryGeneralPage_13, IDGenerationLocation.values());

    Group security = group(composite, Messages.CreateRepositoryGeneralPage_14);
    securityCheckbox = checkbox(security, Messages.CreateRepositoryGeneralPage_2);
    homeFoldersCheckbox = checkbox(security, Messages.CreateRepositoryGeneralPage_3);

    Group store = group(composite, Messages.CreateRepositoryGeneralPage_15);
    storeCombo = combo(store, Messages.CreateRepositoryGeneralPage_4, storeTypes.values());
  }

  @Override
  protected void hookListeners(Listener updateListener)
  {
    enableAuditingCheckbox.addListener(SWT.Selection, updateListener);
    securityCheckbox.addListener(SWT.Selection, updateListener);
    nameText.addListener(SWT.Modify, updateListener);
    storeCombo.getCombo().addListener(SWT.Selection, updateListener);
  }

  @Override
  protected void updateEnablement(boolean firstTime)
  {
    homeFoldersCheckbox.setEnabled(securityCheckbox.getSelection());
    enableBranchingCheckbox.setEnabled(enableAuditingCheckbox.getSelection());

    StoreType newStoreType = UIUtil.getElement(storeCombo.getSelection(), StoreType.class);
    if (storeType != newStoreType)
    {
      storeType = newStoreType;
      publish(storeType);
    }

    boolean nameOK = !StringUtil.isEmpty(nameText.getText().trim());
    boolean storeTypeOK = storeType != null;

    setPageComplete(nameOK && storeTypeOK);
  }

  protected IDGenerationLocation getIDGenerationLocation()
  {
    return UIUtil.getElement(idGenerationLocationCombo.getSelection(), IDGenerationLocation.class);
  }

  @Override
  protected boolean collectRepositoryProperties(Map<String, Object> repositoryProperties)
  {
    repositoryProperties.put(PROPERTY_NAME, nameText.getText().trim());

    // Additional properties that are standard across store types
    repositoryProperties.put("supportingAudits", checked(enableAuditingCheckbox)); //$NON-NLS-1$
    repositoryProperties.put("supportingBranches", checked(enableBranchingCheckbox)); //$NON-NLS-1$
    repositoryProperties.put("ensureReferentialIntegrity", checked(referentialIntegrityCheckbox)); //$NON-NLS-1$
    repositoryProperties.put("allowInterruptRunningQueries", checked(allowInterruptQueriesCheckbox)); //$NON-NLS-1$
    repositoryProperties.put("overrideUUID", text(overrideUUIDText)); //$NON-NLS-1$
    repositoryProperties.put("idGenerationLocation", getIDGenerationLocation().name()); //$NON-NLS-1$

    repositoryProperties.put(CDOAdmin.PROPERTY_SECURITY_MANAGER, checked(securityCheckbox));
    repositoryProperties.put(CDOAdmin.PROPERTY_SECURITY_HOME_FOLDERS, checked(homeFoldersCheckbox));

    return true;
  }

  @Override
  protected void loadSettings(IDialogSettings pageSettings)
  {
    // The repository names and override-UUIDs are unique, so we don't remember them

    enableAuditingCheckbox.setSelection(getSetting(pageSettings, "enableAuditing", true)); //$NON-NLS-1$
    enableBranchingCheckbox.setSelection(getSetting(pageSettings, "enableBranching", true)); //$NON-NLS-1$
    referentialIntegrityCheckbox.setSelection(getSetting(pageSettings, "referentialIntegrity", false)); //$NON-NLS-1$
    allowInterruptQueriesCheckbox.setSelection(getSetting(pageSettings, "allowInterruptQueries", true)); //$NON-NLS-1$

    IDGenerationLocation idGen = IDGenerationLocation.valueOf(getSetting(pageSettings, "idGeneration", //$NON-NLS-1$
        IDGenerationLocation.STORE.name()));
    idGenerationLocationCombo.setSelection(new StructuredSelection(idGen));

    securityCheckbox.setSelection(getSetting(pageSettings, "security", true)); //$NON-NLS-1$
    homeFoldersCheckbox.setSelection(getSetting(pageSettings, "homeFolders", true)); //$NON-NLS-1$

    StoreType storeType = storeTypes.get(getSetting(pageSettings, "storeType", null)); //$NON-NLS-1$
    if (storeType != null)
    {
      storeCombo.setSelection(new StructuredSelection(storeType));
    }
    else
    {
      storeCombo.getCombo().select(0);
    }
  }

  @Override
  protected void saveSettings(IDialogSettings pageSettings)
  {
    // The repository names and override-UUIDs are unique, so we don't remember them

    pageSettings.put("enableAuditing", enableAuditingCheckbox.getSelection()); //$NON-NLS-1$
    pageSettings.put("enableBranching", enableBranchingCheckbox.getSelection()); //$NON-NLS-1$
    pageSettings.put("referentialIntegrity", referentialIntegrityCheckbox.getSelection()); //$NON-NLS-1$
    pageSettings.put("allowInterruptQueries", allowInterruptQueriesCheckbox.getSelection()); //$NON-NLS-1$
    pageSettings.put("idGeneration", getIDGenerationLocation().name()); //$NON-NLS-1$

    pageSettings.put("security", securityCheckbox.getSelection()); //$NON-NLS-1$
    pageSettings.put("homeFolders", homeFoldersCheckbox.getSelection()); //$NON-NLS-1$

    pageSettings.put("storeType", storeType.getID()); //$NON-NLS-1$
  }

  private static Map<String, StoreType> mapStoreTypes(List<StoreType> storeTypes)
  {
    Map<String, StoreType> result = new java.util.HashMap<>();

    for (StoreType storeType : storeTypes)
    {
      result.put(storeType.getID(), storeType);
    }

    return result;
  }
}
