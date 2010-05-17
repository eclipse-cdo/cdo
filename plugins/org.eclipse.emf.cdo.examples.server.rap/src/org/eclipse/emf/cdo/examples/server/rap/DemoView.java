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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
public class DemoView extends ViewPart implements Runnable
{
  public static final String ID = "org.eclipse.emf.cdo.examples.server.rap.view";

  private Image logoImage;

  private Image wizban;

  private Font bigFont;

  private Text nameText;

  private Text serverText;

  private Label modeLabel;

  private Label timeoutLabel;

  private DemoConfiguration config;

  private boolean updatingConfig;

  private Thread timeouter = new Thread(this);

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
    timeouter.interrupt();
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

    Control pane = createPane(composite);
    pane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    Label logo = new Label(composite, SWT.NONE);
    logo.setImage(logoImage);
    logo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    timeouter.setDaemon(true);
    timeouter.start();
  }

  private Control createPane(Composite parent)
  {
    Composite pane = new Composite(parent, SWT.NONE);

    {
      GridLayout gridLayout = new GridLayout(2, false);
      gridLayout.horizontalSpacing = 10;
      gridLayout.marginWidth = 0;
      gridLayout.marginTop = 16;
      gridLayout.marginBottom = 0;
      pane.setLayout(gridLayout);
    }

    {
      Label label = new Label(pane, SWT.NONE);
      label.setText("Repository:");
      label.setFont(bigFont);

      GridLayout gridLayout = new GridLayout(2, false);
      gridLayout.horizontalSpacing = 10;
      gridLayout.marginWidth = 0;
      gridLayout.marginHeight = 0;

      Composite composite = new Composite(pane, SWT.NONE);
      composite.setLayout(gridLayout);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

      nameText = new Text(composite, SWT.BORDER);
      nameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
      nameText.setTextLimit(DemoConfiguration.NAME_LENGTH);
      nameText.setFont(bigFont);
      nameText.addModifyListener(new ModifyListener()
      {
        public void modifyText(ModifyEvent event)
        {
          if (!updatingConfig)
          {
            String name = nameText.getText();
            config = DemoServer.INSTANCE.getConfig(name);
            updateConfig();
          }
        }
      });

      Button button = new Button(composite, SWT.PUSH);
      button.setText("New");
      button.setFont(bigFont);
      button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          NewRepositoryDialog dialog = new NewRepositoryDialog(getSite().getPage(), wizban);
          if (dialog.open() == IDialogConstants.OK_ID)
          {
            Mode mode = dialog.getMode();
            config = DemoServer.INSTANCE.addConfig(mode);
            updateConfig();
          }
        }
      });
    }

    {
      Label label = new Label(pane, SWT.NONE);
      label.setText("Server:");
      label.setFont(bigFont);

      serverText = new Text(pane, SWT.BORDER);
      serverText.setText("tcp://cdo.eclipse.org:" + DemoServer.PORT);
      serverText.setVisible(false);
      serverText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
      serverText.setFont(bigFont);
    }

    {
      Label label = new Label(pane, SWT.NONE);
      label.setText("Mode:");
      label.setFont(bigFont);

      modeLabel = new Label(pane, SWT.NONE);
      modeLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
      modeLabel.setFont(bigFont);
    }

    {
      Label label = new Label(pane, SWT.NONE);
      label.setText("Timeout:");
      label.setFont(bigFont);

      timeoutLabel = new Label(pane, SWT.NONE);
      timeoutLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
      timeoutLabel.setFont(bigFont);
    }

    return pane;
  }

  @Override
  public void setFocus()
  {
    nameText.setFocus();
  }

  public void run()
  {
    long lastUpdate = System.currentTimeMillis();
    while (!timeouter.isInterrupted())
    {
      long now = System.currentTimeMillis();
      if (now - lastUpdate >= 1000L)
      {
        timeoutLabel.getDisplay().asyncExec(new Runnable()
        {
          public void run()
          {
            updateTimeout();
          }
        });

        lastUpdate = now;
      }

      try
      {
        Thread.sleep(200L);
      }
      catch (Exception ex)
      {
        return;
      }
    }
  }

  private void updateConfig()
  {
    if (!updatingConfig)
    {
      try
      {
        updatingConfig = true;
        if (config != null)
        {
          nameText.setText(config.getName());
          serverText.setVisible(true);
          modeLabel.setText(config.getMode().toString());
          updateTimeout();
        }
        else
        {
          serverText.setVisible(false);
          modeLabel.setText("");
          timeoutLabel.setText("");
        }
      }
      finally
      {
        updatingConfig = false;
      }
    }
  }

  private void updateTimeout()
  {
    if (timeoutLabel != null && config != null)
    {
      if (config.isActive())
      {
        timeoutLabel.setText(config.formatTimeoutMinutes() + " Minutes");
      }
      else
      {
        nameText.setText("");
        config = null;
        updateConfig();
      }
    }
  }
}
