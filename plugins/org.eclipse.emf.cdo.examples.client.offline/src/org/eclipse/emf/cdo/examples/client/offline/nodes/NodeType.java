/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline.nodes;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.examples.client.offline.Application;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.FailoverAgent;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOViewTargetChangedEvent;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.ThrowableEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.wb.swt.ExampleResourceManager;

import org.h2.jdbcx.JdbcDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class NodeType extends SetContainer<Node> implements IElement
{
  public static final String TYPE_PROPERTY = "Type";

  public static final String NAME_PROPERTY = "Name";

  public static final String PORT_PROPERTY = "Port";

  public static final String SERVER_PROPERTY = "Server";

  public static final String MONITOR_PROPERTY = "Monitor";

  public static final String BRANCH_PROPERTY = "Branch";

  public static final String BROWSER_PROPERTY = "BrowserPort";

  private static final String REPOSITORY_NAME = "repository";

  private final NodeManager manager;

  private final List<Property> properties = new ArrayList<>();

  private final Properties settings = new Properties();

  private final Composite[] detailsControls = { null, null };

  public NodeType(NodeManager manager)
  {
    super(Node.class);
    this.manager = manager;
    addProperty(new Property.Entry(this, NAME_PROPERTY));
    settings.setProperty(TYPE_PROPERTY, getTypeName());
    activate();
  }

  public NodeManager getManager()
  {
    return manager;
  }

  public List<Property> getProperties()
  {
    return properties;
  }

  @Override
  public Properties getSettings()
  {
    return settings;
  }

  @Override
  public void showSettings()
  {
    showSettings(null);
  }

  public void showSettings(Node node)
  {
    Composite composite = getDetailsControl(node);
    Control[] children = composite.getChildren();

    Properties settings = node == null ? this.settings : node.getSettings();
    for (Property property : properties)
    {
      String name = property.getName();
      Control control = getControl(children, name);
      if (control != null)
      {
        String value = settings.getProperty(name, "");
        property.showSetting(control, value);
      }
    }
  }

  public void configureWindow(IWorkbenchWindowConfigurer configurer)
  {
    configurer.setInitialSize(new Point(800, 500));
    configurer.setTitle(Application.NODE.getName());
    configurer.setShowCoolBar(false);
    configurer.setShowMenuBar(false);
    configurer.setShowStatusLine(false);
  }

  private Control getControl(Control[] children, String name)
  {
    for (Control control : children)
    {
      if (name.equals(control.getData("name")))
      {
        return control;
      }
    }

    return null;
  }

  @Override
  public Image getImage()
  {
    return ExampleResourceManager.getPluginImage(Application.PLUGIN_ID, "icons/Folder.gif");
  }

  public Image getInstanceImage()
  {
    return ExampleResourceManager.getPluginImage(Application.PLUGIN_ID, "icons/" + getTypeName() + ".gif");
  }

  public String getTypeName()
  {
    String name = getClass().getSimpleName();
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1)
    {
      name = name.substring(lastDot + 1);
    }

    return name;
  }

  @Override
  public Composite getDetailsControl()
  {
    return getDetailsControl(null);
  }

  public Composite getDetailsControl(Node node)
  {
    if (node == null)
    {
      return detailsControls[0];
    }

    return detailsControls[1];
  }

  public void start(Node node)
  {
    IRepository repository = createRepository(node);
    node.setObject(IRepository.class, repository);

    IAcceptor acceptor = createAcceptor(node);
    node.setObject(IAcceptor.class, acceptor);

    String browserPort = node.getSetting(BROWSER_PROPERTY);
    if (browserPort != null && browserPort.length() != 0)
    {
      CDOServerBrowser browser = (CDOServerBrowser)IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.server.browsers", "default", browserPort);
      node.setObject(CDOServerBrowser.class, browser);
    }
  }

  public void stop(Node node)
  {
    CDOServerBrowser browser = node.getObject(CDOServerBrowser.class);
    LifecycleUtil.deactivate(browser);

    IAcceptor acceptor = node.getObject(IAcceptor.class);
    LifecycleUtil.deactivate(acceptor);

    IRepository repository = node.getObject(IRepository.class);
    LifecycleUtil.deactivate(repository);
  }

  public Button createUI(final NodeManagerDialog dialog, int kind)
  {
    Composite composite = new Composite(dialog.getDetails(), SWT.NONE);
    composite.setLayout(new GridLayout(2, false));

    for (Property property : properties)
    {
      property.createField(dialog, composite);
    }

    new Label(composite, SWT.NONE);

    Button button = new Button(composite, SWT.PUSH);
    button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    if (kind == NodeManagerDialog.NODE)
    {
      button.setText("Delete");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          dialog.onDelete(NodeType.this);
        }
      });

      composite.setEnabled(false);
      detailsControls[1] = composite;
    }
    else
    {
      button.setText("Create");
      button.addSelectionListener(new SelectionAdapter()
      {
        @Override
        public void widgetSelected(SelectionEvent e)
        {
          dialog.onCreate(NodeType.this);
        }
      });

      detailsControls[0] = composite;
    }

    return button;
  }

  protected void addProperty(Property property)
  {
    properties.add(property);
  }

  @Override
  protected Node[] sortElements(Node[] array)
  {
    Arrays.sort(array);
    return array;
  }

  protected IRepository createRepository(Node node)
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + node.getFolder() + "/db/repository");

    Map<String, String> mappingStrategyProps = new HashMap<>();
    mappingStrategyProps.put(IMappingStrategy.Props.EAGER_TABLE_CREATION, "true");

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
    mappingStrategy.setProperties(mappingStrategyProps);

    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);

    Map<String, String> repositoryProps = new HashMap<>();
    repositoryProps.put(IRepository.Props.OVERRIDE_UUID, REPOSITORY_NAME);
    repositoryProps.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    repositoryProps.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    repositoryProps.put(IRepository.Props.ID_GENERATION_LOCATION, "CLIENT");

    IRepository repository = createRepository(node, store, repositoryProps);
    repository.setInitialPackages(CompanyPackage.eINSTANCE);
    activateRepository(repository);
    return repository;
  }

  protected IRepository createRepository(Node node, IStore store, Map<String, String> props)
  {
    return CDOServerUtil.createRepository(REPOSITORY_NAME, store, props);
  }

  protected void activateRepository(IRepository repository)
  {
    // Don't do this with failover participants!
    CDOServerUtil.addRepository(IPluginContainer.INSTANCE, repository);
  }

  protected IAcceptor createAcceptor(Node node)
  {
    String description = "0.0.0.0:" + node.getSetting(PORT_PROPERTY);
    return (IAcceptor)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.acceptors", "tcp", description);
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Property
  {
    private final NodeType container;

    private final String name;

    public Property(NodeType container, String name)
    {
      this.container = container;
      this.name = name;
    }

    public NodeType getContainer()
    {
      return container;
    }

    public String getName()
    {
      return name;
    }

    public abstract void createField(NodeManagerDialog dialog, Composite parent);

    public abstract void showSetting(Control control, String value);

    /**
     * @author Eike Stepper
     */
    public static abstract class Labelled extends Property
    {
      public Labelled(NodeType container, String name)
      {
        super(container, name);
      }

      @Override
      public void createField(NodeManagerDialog dialog, Composite parent)
      {
        String name = getName();
        Label label = new Label(parent, SWT.NONE);

        label.setText(name + ":");
        label.setLayoutData(new GridData(GridData.END));

        Control control = createControl(dialog, parent);
        control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        control.setData("name", name);
      }

      protected abstract Control createControl(NodeManagerDialog dialog, Composite parent);
    }

    /**
     * @author Eike Stepper
     */
    public static class Entry extends Labelled
    {
      public Entry(NodeType container, String name)
      {
        super(container, name);
      }

      @Override
      protected Control createControl(final NodeManagerDialog dialog, Composite parent)
      {
        final Text text = new Text(parent, SWT.BORDER);
        text.addModifyListener(new ModifyListener()
        {
          @Override
          public void modifyText(ModifyEvent e)
          {
            if (!dialog.isUpdatingDetails())
            {
              dialog.onModify(Entry.this, text.getText());
            }
          }
        });

        return text;
      }

      @Override
      public void showSetting(Control control, String value)
      {
        ((Text)control).setText(value);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Selection extends Labelled
    {
      private final Class<? extends NodeType> type;

      public Selection(NodeType container, String name, Class<? extends NodeType> type)
      {
        super(container, name);
        this.type = type;
      }

      public Class<? extends NodeType> getType()
      {
        return type;
      }

      @Override
      protected Control createControl(final NodeManagerDialog dialog, Composite parent)
      {
        final ComboViewer comboViewer = new ComboViewer(parent, SWT.NONE);
        comboViewer.setLabelProvider(new LabelProvider());
        comboViewer.setContentProvider(new IStructuredContentProvider()
        {
          @Override
          public Object[] getElements(Object inputElement)
          {
            List<Node> result = new ArrayList<>();
            Node[] nodes = getContainer().getManager().getNodes();
            for (Node node : nodes)
            {
              if (type.isAssignableFrom(node.getType().getClass()))
              {
                result.add(node);
              }
            }

            Collections.sort(result);
            return result.toArray(new Node[result.size()]);
          }

          @Override
          public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
          {
          }

          @Override
          public void dispose()
          {
          }
        });

        comboViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
          @Override
          public void selectionChanged(SelectionChangedEvent event)
          {
            if (!dialog.isUpdatingDetails())
            {
              IStructuredSelection selection = (IStructuredSelection)comboViewer.getSelection();
              Node node = (Node)selection.getFirstElement();
              if (node != null)
              {
                dialog.onModify(Selection.this, node.getName());
              }
            }
          }
        });

        comboViewer.setInput(new Object());

        Combo combo = comboViewer.getCombo();
        combo.setData("viewer", comboViewer);
        return combo;
      }

      @Override
      public void showSetting(Control control, String value)
      {
        ComboViewer comboViewer = (ComboViewer)control.getData("viewer");
        comboViewer.refresh(true);

        Node node = getContainer().getManager().getNode(value);
        if (node != null)
        {
          comboViewer.setSelection(new StructuredSelection(node));
        }
        else
        {
          comboViewer.setSelection(StructuredSelection.EMPTY);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Client extends NodeType
  {
    public Client(NodeManager manager)
    {
      super(manager);
      addProperty(new Property.Selection(this, SERVER_PROPERTY, Server.class));
      addProperty(new Property.Entry(this, BROWSER_PROPERTY));
    }

    @Override
    public void start(final Node node)
    {
      super.start(node);

      final CDOSession session = (CDOSession)IPluginContainer.INSTANCE.getElement("org.eclipse.emf.cdo.sessions", "cdo",
          "jvm://example?repositoryName=" + REPOSITORY_NAME);
      session.getPackageRegistry().putEPackage(CompanyPackage.eINSTANCE);

      node.setObject(CDOSession.class, session);

      if (session.getRepositoryInfo().getState() == State.INITIAL)
      {
        session.addListener(new IListener()
        {
          @Override
          public void notifyEvent(IEvent event)
          {
            if (session.getRepositoryInfo().getState() != State.INITIAL)
            {
              session.removeListener(this);
              createTransaction(node);
            }
          }
        });
      }
      else
      {
        createTransaction(node);
      }
    }

    @Override
    public void stop(Node node)
    {
      CDOSession session = node.getObject(CDOSession.class);
      LifecycleUtil.deactivate(session);

      super.stop(node);
    }

    @Override
    protected IRepository createRepository(Node node, IStore store, Map<String, String> props)
    {
      String serverName = node.getSetting(SERVER_PROPERTY);
      final Node serverNode = getManager().getNode(serverName);
      if (serverNode == null)
      {
        throw new IllegalStateException("Server not found: " + serverName);
      }

      CDOSessionConfigurationFactory factory = new CDOSessionConfigurationFactory()
      {
        @Override
        public CDONet4jSessionConfiguration createSessionConfiguration()
        {
          String serverAddress = "localhost:" + serverNode.getSetting(PORT_PROPERTY);

          CDONet4jSessionConfiguration configuration;
          if (serverNode.getType() instanceof FailoverMonitor)
          {
            configuration = CDONet4jUtil.createFailoverSessionConfiguration(serverAddress, serverNode.getName());
          }
          else
          {
            IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", serverAddress);

            configuration = CDONet4jUtil.createNet4jSessionConfiguration();
            configuration.setConnector(connector);
            configuration.setRepositoryName(REPOSITORY_NAME);
          }

          configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
          return configuration;
        }
      };

      IRepositorySynchronizer synchronizer = CDOServerUtil.createRepositorySynchronizer(factory);
      synchronizer.setRetryInterval(2);
      synchronizer.setMaxRecommits(10);
      synchronizer.setRecommitInterval(2);

      return CDOServerUtil.createOfflineClone(REPOSITORY_NAME, store, props, synchronizer);
    }

    @Override
    protected IAcceptor createAcceptor(Node node)
    {
      return (IAcceptor)IPluginContainer.INSTANCE.getElement("org.eclipse.net4j.acceptors", "jvm", "example");
    }

    protected void createTransaction(final Node node)
    {
      CDOSession session = node.getObject(CDOSession.class);

      String branchPath = node.getSettings().getProperty(BRANCH_PROPERTY, CDOBranch.MAIN_BRANCH_NAME);
      CDOBranch branch = session.getBranchManager().getBranch(branchPath);
      if (branch == null)
      {
        branch = session.getBranchManager().getMainBranch();
        node.getSettings().setProperty(BRANCH_PROPERTY, branch.getPathName());
        getManager().saveNode(node);
      }

      CDOTransaction transaction = session.openTransaction(branch);
      transaction.options().addChangeSubscriptionPolicy(CDOAdapterPolicy.ALL);
      transaction.addListener(new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOViewTargetChangedEvent)
          {
            CDOViewTargetChangedEvent e = (CDOViewTargetChangedEvent)event;
            String branchPath = e.getBranchPoint().getBranch().getPathName();
            node.getSettings().setProperty(BRANCH_PROPERTY, branchPath);
            getManager().saveNode(node);
          }
        }
      });

      node.setObject(CDOTransaction.class, transaction);
    }

    @Override
    public String toString()
    {
      return "Clients";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Server extends NodeType
  {
    public Server(NodeManager manager)
    {
      super(manager);
      addProperty(new Property.Entry(this, PORT_PROPERTY));
    }

    @Override
    public void configureWindow(IWorkbenchWindowConfigurer configurer)
    {
      super.configureWindow(configurer);
      configurer.setInitialSize(new Point(500, 350));
    }

    public void setConnectedToNetwork(Node node, boolean on)
    {
      if (on)
      {
        IAcceptor acceptor = createAcceptor(node);
        node.setObject(IAcceptor.class, acceptor);
      }
      else
      {
        IAcceptor acceptor = node.getObject(IAcceptor.class);
        LifecycleUtil.deactivate(acceptor);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Repository extends Server
  {
    public Repository(NodeManager manager)
    {
      super(manager);
    }

    @Override
    public String toString()
    {
      return "Normal Repositories";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class NormalRepository extends Repository
  {
    public NormalRepository(NodeManager manager)
    {
      super(manager);
      addProperty(new Property.Entry(this, BROWSER_PROPERTY));
    }

    @Override
    public String toString()
    {
      return "Normal Repositories";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FailoverRepository extends Repository
  {
    private FailoverAgent agent;

    public FailoverRepository(NodeManager manager)
    {
      super(manager);
      addProperty(new Property.Selection(this, MONITOR_PROPERTY, FailoverMonitor.class));
      addProperty(new Property.Entry(this, BROWSER_PROPERTY));
    }

    @Override
    protected IRepository createRepository(Node node, IStore store, Map<String, String> props)
    {
      String monitorName = node.getSetting(MONITOR_PROPERTY);
      Node monitorNode = getManager().getNode(monitorName);
      if (monitorNode == null)
      {
        throw new IllegalStateException("Monitor not found: " + monitorName);
      }

      final String monitorAddress = "localhost:" + monitorNode.getSetting(PORT_PROPERTY);

      ISynchronizableRepository repository = CDOServerUtil.createFailoverParticipant(REPOSITORY_NAME, store, props);

      agent = new FailoverAgent()
      {
        @Override
        protected IRepositorySynchronizer createRepositorySynchronizer()
        {
          IRepositorySynchronizer synchronizer = super.createRepositorySynchronizer();
          synchronizer.addListener(ThrowableEventAdapter.ToPrintStream.CONSOLE);
          return synchronizer;
        }

        @Override
        protected CDONet4jSessionConfiguration createSessionConfiguration(String connectorDescription, String repositoryName)
        {
          IConnector connector = Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", connectorDescription);

          CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
          configuration.setConnector(connector);
          configuration.setRepositoryName(repositoryName);
          configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
          return configuration;
        }
      };

      agent.setMonitorConnector(Net4jUtil.getConnector(IPluginContainer.INSTANCE, "tcp", monitorAddress));
      agent.setConnectorDescription("localhost:" + node.getSetting(PORT_PROPERTY));
      agent.setRepository(repository);
      agent.setGroup(monitorNode.getName());
      agent.setRate(1500L);
      agent.setTimeout(3000L);

      return repository;
    }

    @Override
    protected void activateRepository(IRepository repository)
    {
      agent.activate();
    }

    @Override
    public String toString()
    {
      return "Failover Repositories";
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class FailoverMonitor extends Server
  {
    public FailoverMonitor(NodeManager manager)
    {
      super(manager);
    }

    @Override
    public void start(Node node)
    {
      super.start(node);

      org.eclipse.emf.cdo.server.net4j.FailoverMonitor monitor = (org.eclipse.emf.cdo.server.net4j.FailoverMonitor)IPluginContainer.INSTANCE.getElement(
          org.eclipse.emf.cdo.server.net4j.FailoverMonitor.PRODUCT_GROUP, org.eclipse.emf.cdo.server.net4j.FailoverMonitor.Factory.TYPE, node.getName());
      node.setObject(org.eclipse.emf.cdo.server.net4j.FailoverMonitor.class, monitor);
    }

    @Override
    public void stop(Node node)
    {
      org.eclipse.emf.cdo.server.net4j.FailoverMonitor monitor = node.getObject(org.eclipse.emf.cdo.server.net4j.FailoverMonitor.class);
      LifecycleUtil.deactivate(monitor);

      super.stop(node);
    }

    @Override
    protected IRepository createRepository(Node node)
    {
      return null;
    }

    @Override
    public String toString()
    {
      return "Failover Monitors";
    }
  }
}
