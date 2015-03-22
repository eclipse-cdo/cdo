/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.repositories.wizards;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminClientManager;
import org.eclipse.emf.cdo.admin.CDOAdminClientRepository;
import org.eclipse.emf.cdo.admin.CDOAdminClientUtil;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.explorer.ui.ViewerUtil;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author Eike Stepper
 */
public class MasterRepositoryController
{
  private static final Image REPOSITORY_IMAGE = OM.getImage("icons/repository.gif");

  private static final Image EMPTY_IMAGE = OM.getImage("icons/empty.gif");

  private static final Image VALIDATING_IMAGE = ContainerItemProvider.PENDING_IMAGE;

  private static final Image OK_IMAGE = OM.getImage("icons/ok.gif");

  private static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages()
      .getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private final DisposeListener disposeListener = new DisposeListener()
  {
    public void widgetDisposed(DisposeEvent e)
    {
      dispose();
    }
  };

  private Composite parent;

  private Text hostText;

  private ValidatingText portText;

  private ValidatingText nameText;

  private TableViewer repositoryTableViewer;

  private IManagedContainer container;

  private CDOAdminClientManager adminManager;

  private IListener adminManagerListener = new ContainerEventAdapter<CDOAdminClient>()
  {
    @Override
    protected void onAdded(IContainer<CDOAdminClient> container, CDOAdminClient admin)
    {
      admin.addListener(adminListener);
    }

    @Override
    protected void onRemoved(IContainer<CDOAdminClient> container, CDOAdminClient admin)
    {
      admin.removeListener(adminListener);
    }
  };

  private IListener adminListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      ViewerUtil.refresh(repositoryTableViewer, null);
    }
  };

  private String connectorDescription;

  private String name;

  public MasterRepositoryController(Composite parent)
  {
    this.parent = parent;

    container = ContainerUtil.createPluginContainer();
    LifecycleUtil.activate(container);

    adminManager = CDOAdminClientUtil.createAdminManager(container);
    adminManager.addListener(adminManagerListener);
    LifecycleUtil.activate(adminManager);

    AbstractRepositoryPage.createLabel(parent, "Host:");
    hostText = new Text(parent, SWT.BORDER);
    hostText.setText("localhost");
    hostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

    AbstractRepositoryPage.createLabel(parent, "Port:");
    portText = new HostValidatingText(parent);
    portText.setText("2036");
    hostText.addModifyListener(portText);

    AbstractRepositoryPage.createLabel(parent, "Repository name:");
    nameText = new RepositoryValidatingText(parent);

    new Label(parent, SWT.NONE);
    repositoryTableViewer = new TableViewer(parent, SWT.BORDER | SWT.SINGLE);
    repositoryTableViewer.setContentProvider(new AdminContentProvider());
    repositoryTableViewer.setLabelProvider(new AdminLabelProvider());
    repositoryTableViewer.setInput(adminManager);
    repositoryTableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    repositoryTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
    {
      public void selectionChanged(SelectionChangedEvent event)
      {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        CDOAdminClientRepository adminRepository = (CDOAdminClientRepository)selection.getFirstElement();
        if (adminRepository != null)
        {
          nameText.setText(adminRepository.getName());
          nameText.modifyText(false);
        }
      }
    });

    portText.modifyText(false);
    nameText.modifyText(false);

    parent.addDisposeListener(disposeListener);
  }

  public final String getConnectorDescription()
  {
    return connectorDescription;
  }

  public final String getName()
  {
    return name;
  }

  public void dispose()
  {
    parent.removeDisposeListener(disposeListener);

    if (container != null)
    {
      nameText.cancelValidation();
      portText.cancelValidation();

      LifecycleUtil.deactivate(adminManager);
      adminManager = null;

      container.deactivate();
      container = null;
    }
  }

  protected void validateController()
  {
    if (nameText != null)
    {
      name = nameText.getText();
    }
  }

  /**
   * @author Eike Stepper
   */
  private abstract class ValidatingText extends Composite implements ModifyListener
  {
    private Text text;

    private Label imageLabel;

    private Label statusLabel;

    private ValidationThread validationThread;

    public ValidatingText(Composite parent, int widthHint)
    {
      super(parent, SWT.NONE);
      setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

      GridLayout layout = new GridLayout(3, false);
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      setLayout(layout);

      GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
      gridData.widthHint = widthHint;

      text = new Text(this, SWT.BORDER);
      text.setLayoutData(gridData);
      text.addModifyListener(this);

      imageLabel = new Label(this, SWT.NONE);

      statusLabel = new Label(this, SWT.NONE);
      statusLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    public final String getText()
    {
      return text.getText();
    }

    public final void setText(String text)
    {
      this.text.setText(text);
    }

    public void cancelValidation()
    {
      if (validationThread != null)
      {
        validationThread.cancel();
        validationThread = null;
      }
    }

    public void modifyText(ModifyEvent e)
    {
      modifyText(true);
    }

    public void modifyText(boolean delay)
    {
      validateController();

      imageLabel.setImage(EMPTY_IMAGE);
      statusLabel.setText("");

      cancelValidation();

      parent.getDisplay().timerExec(delay ? 400 : 0, new Runnable()
      {
        public void run()
        {
          if (!parent.isDisposed())
          {
            String validationInfo = getValidationInfo();
            if (validationInfo != null)
            {
              validationThread = new ValidationThread(validationInfo);
              validationThread.start();
            }
            else
            {
              finished(false);
            }
          }
        }
      });
    }

    protected abstract String getValidationInfo();

    protected abstract String validate(String validationInfo) throws Exception;

    protected void finished(boolean valid)
    {
    }

    /**
     * @author Eike Stepper
     */
    private final class ValidationThread extends Thread
    {
      private final String validationInfo;

      private boolean canceled;

      public ValidationThread(String validationInfo)
      {
        super("Host Validator");
        setDaemon(true);

        this.validationInfo = validationInfo;
      }

      public void cancel()
      {
        canceled = true;
        interrupt();
      }

      @Override
      public void run()
      {
        updateLabels(null, false);

        String message = null;
        boolean valid = true;

        try
        {
          message = validate(validationInfo);
        }
        catch (Exception ex)
        {
          message = ex.getMessage();
          valid = false;
        }

        if (canceled)
        {
          return;
        }

        updateLabels(message, valid);
      }

      private void updateLabels(final String message, final boolean valid)
      {
        Display display = parent.getDisplay();
        if (!parent.isDisposed())
        {
          display.syncExec(new Runnable()
          {
            public void run()
            {
              try
              {
                if (message == null)
                {
                  imageLabel.setImage(VALIDATING_IMAGE);
                  statusLabel.setText("Validating...");
                }
                else
                {
                  imageLabel.setImage(valid ? OK_IMAGE : ERROR_IMAGE);
                  statusLabel.setText(message);
                  finished(valid);
                }
              }
              catch (Exception ex)
              {
                //$FALL-THROUGH$
              }
            }
          });
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class HostValidatingText extends ValidatingText
  {
    private HostValidatingText(Composite parent)
    {
      super(parent, 120);
    }

    @Override
    protected String getValidationInfo()
    {
      connectorDescription = null;

      String host = hostText.getText();
      if (host.length() == 0)
      {
        return null;
      }

      String port = getText();
      if (port.length() == 0)
      {
        port = "2036";
      }

      return host + ":" + port;
    }

    @Override
    protected String validate(String validationInfo) throws Exception
    {
      String[] tokens = validationInfo.split(":");
      String host = tokens[0];
      String port = tokens[1];

      InetAddress addr;

      try
      {
        addr = InetAddress.getByName(host);
      }
      catch (Exception ex)
      {
        throw new Exception("Unknown host");
      }

      try
      {
        new InetSocketAddress(addr, Integer.parseInt(port));
      }
      catch (IllegalArgumentException ex)
      {
        throw new Exception("Invalid port");
      }

      connectorDescription = validationInfo;
      return "Valid";
    }

    @Override
    protected void finished(boolean valid)
    {
      nameText.modifyText(false);

      if (valid && connectorDescription != null)
      {
        adminManager.addConnection("tcp://" + connectorDescription);
      }

      ViewerUtil.refresh(repositoryTableViewer, null);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class RepositoryValidatingText extends ValidatingText
  {
    private RepositoryValidatingText(Composite parent)
    {
      super(parent, 120);
    }

    @Override
    protected String getValidationInfo()
    {
      if (connectorDescription == null)
      {
        return null;
      }

      String repositoryName = getText();
      if (repositoryName.length() == 0)
      {
        return null;
      }

      return repositoryName;
    }

    @Override
    protected String validate(String repositoryName) throws Exception
    {
      CDONet4jSession session = null;

      try
      {
        IConnector connector = null;

        try
        {
          connector = Net4jUtil.getConnector(container, "tcp", connectorDescription);
        }
        catch (Exception ex)
        {
          connector = null;
        }

        if (connector == null)
        {
          throw new Exception("Host unreachable");
        }

        try
        {
          CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
          config.setConnector(connector);
          config.setRepositoryName(repositoryName);
          // config.setCredentialsProvider(this);

          session = config.openNet4jSession();
          if (session != null && session.isClosed())
          {
            session = null;
          }
        }
        catch (Exception ex)
        {
          session = null;
        }

        if (session == null)
        {
          throw new Exception("Repository unreachable");
        }

        CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
        String message = getMode(repositoryInfo);

        if (repositoryInfo.isSupportingBranches()
            && repositoryInfo.getIDGenerationLocation() == IDGenerationLocation.CLIENT)
        {
          message += ", Replicable";
        }

        if (repositoryInfo.isAuthenticating())
        {
          message += ", Authenticating";
        }

        return message;
      }
      finally
      {
        LifecycleUtil.deactivate(session);
      }
    }

    private String getMode(CDORepositoryInfo repositoryInfo)
    {
      if (repositoryInfo.isSupportingBranches())
      {
        return "Branching";
      }

      if (repositoryInfo.isSupportingAudits())
      {
        return "Auditing";
      }

      return "Normal";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdminContentProvider implements IStructuredContentProvider
  {
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public void dispose()
    {
    }

    public Object[] getElements(Object inputElement)
    {
      if (connectorDescription != null)
      {
        for (CDOAdminClient admin : adminManager.getConnections())
        {
          String url = admin.getURL();
          if (url.equals("tcp://" + connectorDescription))
          {
            return admin.getRepositories();
          }
        }
      }

      return ContainerItemProvider.NO_ELEMENTS;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AdminLabelProvider extends LabelProvider
  {
    @Override
    public Image getImage(Object element)
    {
      if (element instanceof CDOAdminClientRepository)
      {
        return REPOSITORY_IMAGE;
      }

      return super.getImage(element);
    }

    @Override
    public String getText(Object element)
    {
      if (element instanceof CDOAdminClientRepository)
      {
        CDOAdminClientRepository adminRepository = (CDOAdminClientRepository)element;
        return adminRepository.getName();
      }

      return super.getText(element);
    }
  }
}
