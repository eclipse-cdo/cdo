package org.eclipse.emf.cdo.dawn.transaction;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionChangeRecorder;

public class DawnTransactionChangeRecorder extends TransactionChangeRecorder
{
  public DawnTransactionChangeRecorder(InternalTransactionalEditingDomain domain, ResourceSet rset)
  {
    super(domain, rset);
  }

  @Override
  protected void assertWriting()
  {
    // Do nothing because we do not want to react an the assert. Every trnasaction is handle be CDO itself
  }

  /**
   * @since 1.0
   */
  @Override
  public void notifyChanged(Notification notification)
  {
    if (!(notification.getOldValue() instanceof ResourceSet && notification.getNewValue() == null && notification
        .getEventType() == Notification.SET))
    {
      super.notifyChanged(notification);
    }
  }
}
