# ChatMessenger
</br>
</br>
Android application that allow to chat between users.</br>
Users can sign up or sign in (if thay are in the database) in application.</br>
There is database that saves an information about users and chat-messages in app.</br>
On abstract server, with link http://\mymessenger.ua\/api\/, there is also database with users and messages.</br>
An information about user contains unique login, nickname, phone number, e-mail, password with md5 protection, avatar image, online status.</br>
An information about message contains nickname of user, message text and message time.</br>
</br>
Sign In activity can processed 2 fields with user login and password and let you chats or forbid you chats.</br>
Also, if you forgot the password, activity allow you to write an email to remember you the password.</br>
Buttons Facebook and Google don't work!</br>
</br>
Sign Up activity allow you to sign up with login, email and password fields.</br>
User can begin to chat with user nickname as login, without phone number and default image as avatar.</br>
Or, user can change nickname, change avatar image and add the phone number and than start to chat.</br>
If the user login is unique, user can't signs up with the login that already in database.</br>
</br>
Main window shows 4 fragments: users, chat, user settings (preferences) and information about application.</br>
Users fragment shows list of users that signed up in application and are in the database on the server.</br>
Chat fragment presents list of messages that are in the database on the server. They are sorted with time when were written.</br>
Pref fragment allow user to change avatar image, nickname, e-mail address, phone number and password.</br>
</br>
