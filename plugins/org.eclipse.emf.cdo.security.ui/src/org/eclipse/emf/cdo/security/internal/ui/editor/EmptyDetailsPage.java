/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * The page presented when there is no selection for which to show details.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class EmptyDetailsPage extends AbstractFormPart implements IDetailsPage
{
  public EmptyDetailsPage()
  {
  }

  @Override
  public void createContents(Composite parent)
  {
    parent.setLayout(new TableWrapLayout());
    FormToolkit toolkit = getManagedForm().getToolkit();

    Section section = toolkit.createSection(parent, Section.SHORT_TITLE_BAR);
    section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

    section.setText(Messages.EmptyDetailsPage_0);
    Label label = toolkit.createLabel(section, Messages.EmptyDetailsPage_1);
    section.setClient(label);
  }

  @Override
  public void selectionChanged(IFormPart part, ISelection selection)
  {
    // Pass
  }
}
