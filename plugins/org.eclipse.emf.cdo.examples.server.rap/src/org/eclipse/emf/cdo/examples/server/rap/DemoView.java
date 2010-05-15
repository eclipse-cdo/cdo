/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server.rap;

import org.eclipse.emf.cdo.examples.server.DemoConfiguration;
import org.eclipse.emf.cdo.examples.server.DemoConfiguration.Mode;
import org.eclipse.emf.cdo.examples.server.DemoServer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Eike Stepper
 */
public class DemoView extends ViewPart
{
  public static final String ID = "org.eclipse.emf.cdo.examples.server.rap.view";

  private Image logoImage;

  private Image wizban;

  private Font bigFont;

  private Text nameText;

  public DemoView()
  {
  }

  private void init(Composite parent)
  {
    logoImage = Activator.getImageDescriptor("images/Logo-CDO.png").createImage();
    wizban = Activator.getImageDescriptor("images/NewRepository.gif").createImage();
    bigFont = new Font(parent.getDisplay(), parent.getFont().getFontData()[0].getName(), 24, SWT.BOLD);
  }

  @Override
  public void dispose()
  {
    bigFont.dispose();
    wizban.dispose();
    logoImage.dispose();
    super.dispose();
  }

  @Override
  public void createPartControl(Composite parent)
  {
    init(parent);

    GridLayout gridLayout = new GridLayout(2, false);
    gridLayout.horizontalSpacing = 30;
    gridLayout.marginWidth = 30;
    gridLayout.marginHeight = 30;

    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(gridLayout);
    // composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));

    Control pane = createPane(composite);
    pane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Label logo = new Label(composite, SWT.NONE);
    logo.setImage(logoImage);
    logo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
  }

  private Control createPane(Composite parent)
  {
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.horizontalSpacing = 10;
    gridLayout.marginWidth = 0;
    gridLayout.marginTop = 84;
    gridLayout.marginBottom = 0;

    Composite pane = new Composite(parent, SWT.NONE);
    pane.setLayout(gridLayout);

    Label label = new Label(pane, SWT.NONE);
    label.setText("Repository:");
    label.setFont(bigFont);

    nameText = new Text(pane, SWT.BORDER);
    nameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    nameText.setFont(bigFont);

    Button button = new Button(pane, SWT.PUSH);
    button.setText("New");
    button.setFont(bigFont);
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        NewRepositoryDialog dialog = new NewRepositoryDialog(getSite().getPage(), wizban);
        if (dialog.open() == IDialogConstants.OK_ID)
        {
          Mode mode = dialog.getMode();
          DemoConfiguration config = DemoServer.INSTANCE.addConfig(mode);
          nameText.setText(config.getName());
        }
      }
    });

    return pane;
  }

  @Override
  public void setFocus()
  {
    nameText.setFocus();
  }
}
