RUNNING THE GASTRO EXAMPLES
==============================

1) Install the example 
   - Checkout the "org.gastro.*" plugins via http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.emf/org.eclipse.emf.cdo/develop/setup/psf/pserver/cdo-gastro-example.psf?root=Modeling_Project&view=log

2) Start the server process 
   - Start the "GastroServer" launch config. It creates a Derby database at "/gastro".
   - Watch the console output. 
 
3) Populate the repository 
   - Start the "GastroTestClient1" launch config. It opens a runtime IDE with generic CDO support.
   - Open the "CDO Sessions" view.
   - Add a new session (green plus button). Enter "tcp://localhost" and "gastro".
   - Open a new transaction (right-click on the session).
   - Import the /org.gastro.testclient/inventory.xml resource into "/eDine/inventory".
   - Commit the transaction (right-click on the transaction or just save the editor).
   
4) Start the RCP application for table 1 (and optionally for table 2)
   - Start the "GastroTable1" launch config. It opens an RCP application.
   - Browse through the menu card and select some offerings here and there.
   
5) Start the RCP application for the bar
   - Start the "GastroDepartment1" launch config. It opens an RCP application for the barkeeper.
   - Watch orders coming in.
   - Eventually click on order details to mark them SERVED and watch the order disappearing when completed.
   
6) Start the RCP application for the kitchen
   - Start the "GastroDepartment2" launch config. It opens an RCP application for the cook.
   - Eventually click on order details to mark them SERVED and watch the order disappearing when completed.
   
7) Send an order (if the RCP applications are all started, see steps 4-6)
   - In the table application open the second shelf pane and send the order.
   - Watch how the relevant order details "arrive" in the bar and kitchen applications...
   
