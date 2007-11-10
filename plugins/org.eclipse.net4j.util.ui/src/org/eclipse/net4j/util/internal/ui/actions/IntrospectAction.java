package org.eclipse.net4j.util.internal.ui.actions;

import org.eclipse.net4j.util.internal.ui.views.Net4jIntrospectorView;
import org.eclipse.net4j.util.ui.actions.SafeAction;

/**
 * @author Eike Stepper
 */
public class IntrospectAction extends SafeAction
{
  private Object object;

  public IntrospectAction(Object object)
  {
    super("Introspect");
    this.object = object;
  }

  @Override
  protected void safeRun() throws Exception
  {
    Net4jIntrospectorView introspector = Net4jIntrospectorView.getInstance(true);
    introspector.setObject(object);
  }
}