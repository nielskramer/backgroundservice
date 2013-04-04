Backgroundservice
=================

This applications goes trough a number of states

1. When it is first launched the activity will be hidden and the android.launcher will be shown. ( I know there might be better solutions but my attention was not on this problem )
2. Then i start my MessageService which loops trough all running process to find the openVPN proces ( If launched)
3. After it is started i register two broadcast receivers and then when it found the openVPN proces i stop my message service and start the Reinitiate service to reactivate the message that has been shown when a user opens the application again.
