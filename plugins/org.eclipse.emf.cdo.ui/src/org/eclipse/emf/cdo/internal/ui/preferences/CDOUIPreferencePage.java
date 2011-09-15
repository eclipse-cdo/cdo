/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.preferences;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.ui.CDOLabelDecorator;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

/**
 * @author Eike Stepper
 */
public class CDOUIPreferencePage extends OMPreferencePage
{
  private TextAndDisable decoration;

  private Button autoReload;

  public CDOUIPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 2);
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText(Messages.getString("CDOUIPreferencePage.0")); //$NON-NLS-1$
    decoration = new TextAndDisable(composite, SWT.BORDER, CDOLabelDecorator.NO_DECORATION)
    {
      @Override
      protected GridData createTextLayoutData()
      {
        return UIUtil.createGridData(true, false);
      }
    };

    decoration.setLayoutData(UIUtil.createGridData(true, false));

    Text text = decoration.getText();
    StringBuffer tags = new StringBuffer();
    for (String tag : CDOLabelDecorator.DECORATION_PROPOSALS)
    {
      tags.append(tag + " "); //$NON-NLS-1$
    }

    text.setToolTipText(Messages.getString("CDOUIPreferencePage.2") + tags.toString()); //$NON-NLS-1$
    IControlContentAdapter contentAdapter = new TextContentAdapter();
    IContentProposalProvider provider = new SimpleContentProposalProvider(CDOLabelDecorator.DECORATION_PROPOSALS);
    new ContentAssistCommandAdapter(text, contentAdapter, provider, null, new char[] { '$' }, true);
    UIUtil.addDecorationMargin(text);

    autoReload = new Button(composite, SWT.CHECK);
    autoReload.setText(Messages.getString("CDOUIPreferencePage.1")); //$NON-NLS-1$
    autoReload.setLayoutData(UIUtil.createGridData(false, false));
    UIUtil.setIndentation(autoReload, -1, 10);

    initValues();
    return composite;
  }

  protected void initValues()
  {
    decoration.setValue(OM.PREF_LABEL_DECORATION.getValue());
    autoReload.setSelection(OM.PREF_EDITOR_AUTO_RELOAD.getValue());
  }

  @Override
  public boolean performOk()
  {
    OM.PREF_LABEL_DECORATION.setValue(decoration.getValue());
    OM.PREF_EDITOR_AUTO_RELOAD.setValue(autoReload.getSelection());
    return super.performOk();
  }
}
