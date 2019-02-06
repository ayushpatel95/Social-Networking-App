# Social-Networking-App
In this assignment, you will be developing a social networking app to communicate and share your post with friends and follow them. You will learn to use Firebase to manage app data.
Part 1: Login and Sign Up Screens
The first screen of the app is a user authentication screen.
Please follow the instructions:
1. The launcher screen should be set to the Login Screen (see figure 1 a).
When the app first starts, the login screen should check if there is a
current user session:
1. If the user is already logged in to Firebase, then start the Home Screen
and close the login Screen.
2. If there is no current session, then the Login Screen should be
used to provide user login.
2. The user should provide their first name, last name, email, date of birth,
password and password confirmation. Clicking the “Sign Up” button
should submit the user’s information to Firebase.
1. Age should be greater than 13 and should have basic password
validations.
2. If the signup is successful, then display a Toast indicating the user
has been created. Then take it back to the Login screen to Login.
3. User can also use Firebase google authentication to login.
Part 2: Home screen
This screen displays the posts of the user and his/her friends. Please follow
the instructions:
1. Display the user’s and his/her friends’ posts from the last two days in a
chronological order, latest being first. See figure 1 b.
2. Each post should contain the name of the user who posted it, the date and
time of the post (Use Prettytime library to display time), and the text.
3. There is a InputText at the bottom of the screen to write a new post.
Clicking on send icon should post the message and update the screen
(see figure 1 c). Note that the post should have a character limit of 200
characters max.
4. There should be User name and friends icon just below the action bar. It
can be implemented using a second action bar.
5. Clicking on User name should take the user to his profile screen and
clicking on friends icon should take the user to manage friends screen.
6. Clicking on Name of any friend in a particular post should take you to his
wall.
Part 3: User’s Wall screen
This screen contains the user’s posts. Please follow the instructions:
1. Display all of his/her posts in a chronological order, latest being first, see
figure 1d.
2. The post’s view should be similar to that explained in Part 2, except it
should have a delete icon at the bottom right, and clicking on this icon
should delete the post.
3. Prompt the user before deleting any post.
4. There should be an edit icon next to the name of the user in second action
bar. Clicking on this should take you to edit profile screen.
5. User relevant design for profile screen. All the user details should be
editable except email.
Part 4: Profile screen of a friend
This screen contains the posts of a particular friend. Please follow the
instructions:
1. Display all posts of the friend in a chronological order, latest being first,
see figure 1 e.
Part 4: Manage Friends screen
This screen is used to manage friends. Please follow the instructions:
1. There will be three tabs: Friends, Add New Friend, and Requests Pending.
See figures 1 (f,g, and h).
2. In the Friends tab, display all the current friends of the user. Clicking on
any friend should take you to his/her profile screen.
3. In Add New Friend tab, display all the users who are not in his friends, or
pending lists. Clicking on add friend should send a friend request and tabs
data should be refreshed.
4. In Pending Requests tab, you should display all the pending request sent
and received. For Received request there will be accept and reject option.
For Sent requests there will be a delete request option.
5. Clicking on Home icon in second action bar should take you to home
screen.
Part 5: Miscellaneous
1. The App should have a launch icon. The action bar also should have
the same icon and app title.
2. There is a log out icon in action bar. Clicking on this icon should erase
user session and take you to login screen.
3. Data across the application should be synced and should be up to date
with database.
