package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.IPluginTransportContainer;
import org.eclipse.net4j.ITransportContainer;
import org.eclipse.net4j.internal.ui.bundle.OM;
import org.eclipse.net4j.ui.actions.SafeAction;
import org.eclipse.net4j.ui.views.ContainerItemProvider;
import org.eclipse.net4j.ui.views.ContainerView;
import org.eclipse.net4j.ui.views.IElementFilter;
import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;

public class ConnectorsView extends ContainerView
{
  private Action addConnectorAction = new SafeAction("Add Connector", "Add a connector", getAddImageDescriptor())
  {
    @Override
    protected void doRun() throws Exception
    {
      IPluginTransportContainer.INSTANCE.getConnector("tcp", "127.0.0.1:2036");
    }
  };

  public ConnectorsView()
  {
  }

  @Override
  protected ITransportContainer getContainer()
  {
    return IPluginTransportContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IConnector;
      }
    });
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(addConnectorAction);
    manager.add(new SafeAction("PREFS")
    {
      @Override
      protected void doRun() throws Exception
      {
        OMPreference<String> pref = OM.PREF_TEXT;
        InputDialog dlg = new InputDialog(getSite().getShell(), "PREFS", "Enter a text:", pref.getValue(), null);
        if (dlg.open() == InputDialog.OK)
        {
          pref.setValue(dlg.getValue());
        }
      }
    });

    super.fillLocalToolBar(manager);
  }
}