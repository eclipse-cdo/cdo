/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.preferences;

import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.CDOLabelProvider;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.prefs.OMPreferencePage;
import org.eclipse.net4j.util.ui.widgets.TextAndDisable;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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

  public CDOUIPreferencePage()
  {
    super(OM.PREFS);
  }

  @Override
  protected Control createUI(Composite parent)
  {
    Composite composite = UIUtil.createGridComposite(parent, 2);
    composite.setLayoutData(UIUtil.createGridData());

    new Label(composite, SWT.NONE).setText("Label decoration:");
    decoration = new TextAndDisable(composite, SWT.BORDER, CDOLabelProvider.NO_DECORATION)
    {
      @Override
      protected GridData createTextLayoutData()
      {
        return UIUtil.createGridData(true, false);
      }
    };
    decoration.setLayoutData(UIUtil.createGridData(true, false));

    Text text = decoration.getText();
    IControlContentAdapter contentAdapter = new TextContentAdapter();
    IContentProposalProvider provider = new SimpleContentProposalProvider(CDOLabelProvider.DECORATION_PROPOSALS);
    new ContentAssistCommandAdapter(text, contentAdapter, provider, null, new char[] { '$' }, true);
    UIUtil.addDecorationMargin(text);

    initValues();
    return composite;
  }

  protected void initValues()
  {
    decoration.setValue(OM.PREF_LABEL_DECORATION.getValue());
  }

  @Override
  public boolean performOk()
  {
    OM.PREF_LABEL_DECORATION.setValue(decoration.getValue());
    return super.performOk();
  }
}
