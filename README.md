# Executing the Project
The project can be executed through the following Gradle tasks:
#### Server: 
```bash
./gradlew bootRun
```
#### Client: 
```bash
./gradlew run
```
#### Admin: 
```bash
./gradlew adminRun
```
It can also be run from IntelliJ. The respective classes are:
#### Server: 
[server.Main](server/src/main/java/server/Main.java)
#### Client: 
[client.Main](client/src/main/java/client/Main.java)
#### Admin: 
[admin.AdminConsole](client/src/main/java/admin/AdminConsole.java)

# Accessing the admin interface
To access the admin interface, run: 
```bash
./gradlew adminRun
\```
To access the admin interface, run: 
```bash
./gradlew adminRun --console=plain
\```
It can also be run from IntelliJ, run the main method in [admin.AdminConsole](client/src/main/java/admin/AdminConsole.java)

# Location of long-polling example:
The long-polling is implemented on the startscreen to refresh the recent events of the application using a thread for every event in the list with the function getEventUpdate in the serverUtils. At the server side it is handled with DeferredResult by the getPolling function in the eventService class.

# Description of Extensions
## Live Language Switch
There is a combo box containing language options in the starting page and event overview. To add another language using the template there is an "Add New Language" option within the combo box.
## Detailed Expenses
On the overview page there is a panel containing expenses. Within this panel the expenses are categorized by transfer, debt settlement, and normal expense. There is an add transfer button in the overview too, found next to the add expense button.
## Foreign Currency
In the settings menu (accessible through the overview and the start screen) a user can configure their language options. In the add/edit expense pages currency options are also present. The endpoint for the rates is /api/rates/{date}/{from}/{to}. The relevant classes are: server.api.CurrencyController, server.api.CurrencyService, client.utils.CurrencyConverter (and all of its usages).
## Open Debts
There is a settle debts page accessible from the overview through the "Settle Debts" button. To see details of a debt click on the arrow to expand the pane. When creating or editing participants there are fields to include bank information.
## Statistics
In the add/edit expense pages there is functionality to select a tag or add your own tag. The statistics page can be accessed through the overview using the "Statistics" button. Within the statistics page there is a button called "Manage tags" to delete or manage the tags in an event.
## Email Notification
To configure email credentials there is a place within the settings to add the necessary information. Once this is configured the invite screen becomes available to use. Additionally within the debts page there is an option to remind participants of payments. There is an area to give participants email addresses in the add/edit participant pages.

# HCI Functionality
## Color Contrast
The application itself is high contrast, utilizing blue and red buttons, which maintain contrast with colorblind users, and black text over white background.
## KeyBoard Shortcuts
There are keyboard shortcuts configured. The shortcuts themselves can be found listed in the settings, accessible from the overview and the start screen.
## Multi-Modal Visualization
Many of the buttons in the application utilize multi-modal visualization. Take for example the ‘Go Back’, ‘Settle Debts’ and ‘Statistics’ buttons from the overview. They all have an icon associated with them and the ‘Go Back’ and ‘Settle Debts’ buttons are color coded, with red associating with cancel or return and blue with okay or continue.
## Logical Navigation
This criteria is met through maintaining a standard placement of the back and okay buttons (bottom right edge of the screen). Additionally the user flow is logical as a user goes from start screen to overview and from there can access all other functionalities.
## KeyBoard Navigation
The tab button can be used to cycle through the different buttons and text fields on screen. Whichever element of the UI is focused when using keyboard input has a red outline to aid in the use of the application without a mouse. The required flows are also possible using just the tab and enter buttons, and can be done with or without the keyboard shortcuts.
## Supporting undo functionality
There is undo functionality when editing an expense. The undo button is only visible if there are recent changes to be undone, otherwise it is not visible. Additionally CTRL + Z is a shortcut to undo edits.
## Informative Feedback
When adding an expense, editing an expense, adding a participant, or editing a participant, text appears in the bottom of the overview scene saying “Expense added”, “Edits Saved” or “Participant Added” to inform the user that their changes have been applied.
## Confirmation for Key Actions
Within the admin console there is confirmation required (in the form of typing Y or N) when deleting an event. Additionally when deleting a participant that is involved in an expense there is a warning and confirmation message alert that pops up.
## Error Messages
Invalid values when creating an expense (eg. a negative payment amount or an empty title) are rejected and an error message will be shown to the user. When the user enters a server address at the starting page, the availability will be checked. In the case that the server is unavailable, an error message will be shown.

# Special Features
When selecting all members of the event when selecting who to split between, the application automatically selects “split between everyone”

# Work In Progress/Future Plans

- Implementing functionality to catch unavailability of the server and return to server connection screen.
- Implement fixes for some bugs that we had, such as when setting debts and converting currency the debt returns.
- Extend our testing
