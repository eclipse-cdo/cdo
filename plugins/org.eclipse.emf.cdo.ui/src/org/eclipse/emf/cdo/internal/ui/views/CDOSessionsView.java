package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOConstants;
import org.eclipse.emf.cdo.container.CDOContainerAdapter;
import org.eclipse.emf.cdo.internal.ui.bundle.CDOUI;

import org.eclipse.net4j.container.Container;
import org.eclipse.net4j.container.ContainerManager;
import org.eclipse.net4j.transport.ConnectorException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class CDOSessionsView extends StructuredView
{
  private static final Container CONTAINER = ContainerManager.INSTANCE.getContainer();

  private static final CDOContainerAdapter CDO_ADAPTER = (CDOContainerAdapter)CONTAINER.getAdapter(CDOConstants.TYPE);

  private static final ItemProvider ITEM_PROVIDER = new CDOSessionsItemProvider();

  private TreeViewer viewer;

  private Action openSessionAction = new OpenSessionAction();

  private Action attachAdapterAction = new AttachAdapterAction();

  public CDOSessionsView()
  {
  }

  @Override
  protected void doCreatePartControl(Composite parent)
  {
    viewer = new TreeViewer(parent, (SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

    // drillDownAdapter = new DrillDownAdapter(viewer);
    viewer.setContentProvider(ITEM_PROVIDER);
    viewer.setLabelProvider(ITEM_PROVIDER);
    viewer.setSorter(new CDOSessionsNameSorter());
    viewer.setInput(CDO_ADAPTER);
  }

  @Override
  protected StructuredViewer getCurrentViewer()
  {
    return viewer;
  }

  @Override
  protected void fillLocalPullDown(IMenuManager manager)
  {
    addContribution(manager, openSessionAction);
    addContribution(manager, attachAdapterAction);
    super.fillLocalPullDown(manager);
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    addContribution(manager, openSessionAction);
    addContribution(manager, attachAdapterAction);
    super.fillLocalToolBar(manager);
  }

  /**
   * @author Eike Stepper
   */
  private final class OpenSessionAction extends Action
  {
    public OpenSessionAction()
    {
      setText("Open Session");
      setToolTipText("Open a CDO session");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
          ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

    public void run()
    {
      String description = showInputDialog("Enter a session description");
      if (description != null)
      {
        try
        {
          CDO_ADAPTER.getSession(description);
        }
        catch (ConnectorException ex)
        {
          CDOUI.LOG.error(ex);
          showMessage("Error while creating session for description " + description);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class AttachAdapterAction extends Action
  {
    public AttachAdapterAction()
    {
      setText("Attach Adapter");
      setToolTipText("Attach a CDO adapter");
      setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
          ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

    public void run()
    {
      InputDialog dialog = new InputDialog(getCurrentViewer().getControl().getShell(), "CDO Sessions",
          "Enter a session description:", null, null);
      if (dialog.open() == InputDialog.OK)
      {
        String description = dialog.getValue();

        try
        {
          CDO_ADAPTER.getSession(description);
        }
        catch (ConnectorException ex)
        {
          CDOUI.LOG.error(ex);
          showMessage("Error while creating session for description " + description);
        }
      }
    }
  }
}