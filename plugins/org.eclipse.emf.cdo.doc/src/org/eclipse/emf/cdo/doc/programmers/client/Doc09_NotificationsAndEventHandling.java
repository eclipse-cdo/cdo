package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewInvalidationEvent;

/**
 * Notifications and Event Handling
 * <p>
 * This chapter describes how to handle notifications and events in CDO client applications. Notifications are
 * essential for building responsive applications that react to changes in the repository, whether triggered locally or
 * remotely.
 * <p>
 * CDO provides APIs for listening to model changes, handling remote updates, and implementing custom event processing
 * logic. Understanding these mechanisms enables you to synchronize application state, update user interfaces, and
 * coordinate collaborative workflows.
 *
 * @author Eike Stepper
 */
public class Doc09_NotificationsAndEventHandling
{
  /**
   * Listening for Model Changes
   * <p>
   * CDO emits notifications when model objects are created, modified, or deleted. This section explains how to register
   * listeners, process change events, and update your application in response to model changes. Learn best practices for
   * efficient event handling and minimizing performance impact.
   */
  public class ListeningForModelChanges
  {
    /**
     * Example: Register a CDOView invalidation listener for model changes
     * @snip
     * Registers a listener to print invalidation events for a CDOView.
     * @param view the CDOView
     */
    public void registerInvalidationListener(CDOView view)
    {
      view.addListener(event -> {
        if (event instanceof CDOViewInvalidationEvent)
        {
          System.out.println("Invalidation event: " + event);
        }
      });
    }
  }

  /**
   * Handling Remote Updates
   * <p>
   * Remote updates occur when changes are made to the repository by other clients or processes. This section covers how
   * to detect and respond to remote updates, synchronize local state, and resolve conflicts that may arise from
   * concurrent modifications.
   */
  public class HandlingRemoteUpdates
  {
    /**
     * Example: Handle remote update notifications in a CDOView
     * @snip
     * Handles remote update events and prints affected objects.
     * @param view the CDOView
     */
    public void handleRemoteUpdates(CDOView view)
    {
      view.addListener(event -> {
        if (event instanceof CDOViewInvalidationEvent)
        {
          CDOViewInvalidationEvent invalidation = (CDOViewInvalidationEvent)event;
          for (org.eclipse.emf.cdo.CDOObject obj : invalidation.getDirtyObjects())
          {
            System.out.println("Remote update: " + obj);
          }
        }
      });
    }
  }

  /**
   * Custom Event Processing
   * <p>
   * Extend CDO's event handling capabilities with custom logic. Learn how to implement custom event processors, filter
   * events, and integrate notifications with external systems or workflows.
   */
  public class CustomEventProcessing
  {
    /**
     * Example: Custom event processing for CDOView events
     * @snip
     * Filters and processes only specific types of events.
     * @param view the CDOView
     * @param eventType the event class to filter for
     */
    public void processCustomEvents(CDOView view, Class<?> eventType)
    {
      view.addListener(event -> {
        if (eventType.isInstance(event))
        {
          System.out.println("Custom event processed: " + event);
        }
      });
    }
  }
}
