/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.admin.wizards;

import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.DEFAULT_CONNECTION_KEEP_ALIVE_PERIOD;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.DEFAULT_READER_POOL_CAPACITY;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.DEFAULT_WRITER_POOL_CAPACITY;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.PROPERTY_PATH;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.PROPERTY_READER_POOL_CAPACITY;
import static org.eclipse.emf.cdo.ui.internal.admin.StoreType.Database.PROPERTY_WRITER_POOL_CAPACITY;

import org.eclipse.emf.cdo.common.admin.CDOAdmin;
import org.eclipse.emf.cdo.ui.internal.admin.StoreType;
import org.eclipse.emf.cdo.ui.internal.admin.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public class CreateRepositoryStorePage extends AbstractCreateRepositoryWizardPage
{
  private static final String TITLE_PATTERN = Messages.CreateRepositoryStorePage_1;

  private Text storePathText;

  private Text connectionKeepAlivePeriodText;

  private Text readerPoolCapacityText;

  private Text writerPoolCapacityText;

  private StoreType storeType;

  public CreateRepositoryStorePage(String pageName)
  {
    super(pageName);
    setTitle(TITLE_PATTERN);
    setMessage(Messages.CreateRepositoryStorePage_2);
  }

  @Subscribe
  public void setStoreType(StoreType storeType)
  {
    this.storeType = storeType;
    setTitle(MessageFormat.format(TITLE_PATTERN, storeType.getName()));
    // TODO: When we support multiple store types, change a paged UI
  }

  @Override
  protected void createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayoutData(UIUtil.createGridData());
    composite.setLayout(new GridLayout());

    Group general = group(composite, Messages.CreateRepositoryStorePage_3);
    storePathText = text(general, Messages.CreateRepositoryStorePage_0);

    Group performance = group(composite, Messages.CreateRepositoryStorePage_4);
    connectionKeepAlivePeriodText = text(performance, Messages.CreateRepositoryStorePage_5);
    readerPoolCapacityText = text(performance, Messages.CreateRepositoryStorePage_6);
    writerPoolCapacityText = text(performance, Messages.CreateRepositoryStorePage_7);
  }

  @Override
  protected void hookListeners(Listener updateListener)
  {
    storePathText.addListener(SWT.Modify, updateListener);
    connectionKeepAlivePeriodText.addListener(SWT.Modify, updateListener);
    readerPoolCapacityText.addListener(SWT.Modify, updateListener);
    writerPoolCapacityText.addListener(SWT.Modify, updateListener);
  }

  @Override
  protected void updateEnablement(boolean firstTime)
  {
    boolean storePathOK = !StringUtil.isEmpty(text(storePathText));

    if (firstTime)
    {
      setPageComplete(storePathOK);
      return;
    }

    if (!storePathOK)
    {
      setErrorMessage(Messages.CreateRepositoryStorePage_8);
      setPageComplete(false);
      return;
    }

    // Don't need to worry about other properties the first time because they have defaults

    String keepAlive = text(connectionKeepAlivePeriodText);
    String readerPool = text(readerPoolCapacityText);
    String writerPool = text(writerPoolCapacityText);

    if (StringUtil.isEmpty(keepAlive))
    {
      setErrorMessage(Messages.CreateRepositoryStorePage_9);
      setPageComplete(false);
      return;
    }

    if (!positiveInteger(keepAlive))
    {
      setErrorMessage(Messages.CreateRepositoryStorePage_10);
      setPageComplete(false);
      return;
    }

    if (!StringUtil.isEmpty(readerPool) && !positiveInteger(readerPool))
    {
      setErrorMessage(Messages.CreateRepositoryStorePage_11);
      setPageComplete(false);
      return;
    }

    if (!StringUtil.isEmpty(writerPool) && !positiveInteger(writerPool))
    {
      setErrorMessage(Messages.CreateRepositoryStorePage_12);
      setPageComplete(false);
      return;
    }

    setErrorMessage(null);
    setPageComplete(true);
  }

  @Override
  protected void loadSettings(IDialogSettings pageSettings)
  {
    connectionKeepAlivePeriodText.setText(getSetting(pageSettings, PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD, DEFAULT_CONNECTION_KEEP_ALIVE_PERIOD));
    readerPoolCapacityText.setText(getSetting(pageSettings, PROPERTY_READER_POOL_CAPACITY, DEFAULT_READER_POOL_CAPACITY));
    writerPoolCapacityText.setText(getSetting(pageSettings, PROPERTY_WRITER_POOL_CAPACITY, DEFAULT_WRITER_POOL_CAPACITY));
  }

  @Override
  protected void saveSettings(IDialogSettings pageSettings)
  {
    pageSettings.put(PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD, text(connectionKeepAlivePeriodText));
    pageSettings.put(PROPERTY_READER_POOL_CAPACITY, text(readerPoolCapacityText));
    pageSettings.put(PROPERTY_WRITER_POOL_CAPACITY, text(writerPoolCapacityText));
  }

  @Override
  protected boolean collectRepositoryProperties(Map<String, Object> repositoryProperties)
  {
    repositoryProperties.put(CDOAdmin.PROPERTY_STORE_XML_CONFIG, createStoreXML(storeType));
    return true;
  }

  protected String createStoreXML(StoreType storeType)
  {
    Map<String, Object> storeProperties = new java.util.HashMap<>();

    storeProperties.put(PROPERTY_PATH, text(storePathText));
    storeProperties.put(PROPERTY_CONNECTION_KEEP_ALIVE_PERIOD, text(connectionKeepAlivePeriodText));
    storeProperties.put(PROPERTY_READER_POOL_CAPACITY, text(readerPoolCapacityText));
    storeProperties.put(PROPERTY_WRITER_POOL_CAPACITY, text(writerPoolCapacityText));

    return storeType.getStoreXML(storeProperties);
  }
}
