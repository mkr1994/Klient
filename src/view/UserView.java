package view;

import controller.MainController;
import sdk.encrypters.Digester;
import sdk.model.CachedData;
import sdk.model.User;
import sdk.connection.ResponseCallback;
import sdk.services.UserService;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Controller class containing all user related methods.
 * Created by magnusrasmussen on 29/10/2016.
 */
public class UserView {

    private Scanner input;
    private UserService userService;

    public UserView() {
        this.input = new Scanner(System.in);
        this.userService = new UserService();
    }


    public void editUser() {
        User u = MainController.currentUser;
        String newInfo = null;
        int choice, tries = 0;
        boolean fireRequest = true, stopLoop = true;

        // Header for showing userinfo
        System.out.printf("%-30s %-30s %-25s %-35s %-25s\n", "Username:", "Firstname:", "Lastname:", "Email:", "Password:");
        System.out.printf("%-30s %-30s %-25s %-35s %-25s\n", u.getUserName(), u.getFirstName(), u.getLastName(), u.getEmail(), "*********");

        do {
            System.out.println("Press 1 to edit username\nPress 2 to edit your firstname\nPress 3 to edit lastname\nPress 4 to edit email\nPress 5 to edit password\nPress 6 to cancel");
            choice = input.nextInt();
            input.nextLine();
            if (choice < 5) {
                System.out.println("Please enter new info: ");
                newInfo = input.nextLine();
            }
            switch (choice) {
                case 1:
                    u.setUserName(newInfo);
                    break;
                case 2:
                    u.setFirstName(newInfo);
                    break;
                case 3:
                    u.setLastName(newInfo);
                    break;
                case 4:
                    u.setEmail(newInfo);
                    break;
                case 5:
                    // If the user want to edit his password he need to enter the old password first.
                    do {
                        System.out.println("Enter your old password: ");
                        newInfo = input.nextLine();
                        tries++; //The user needs to enter old password to change and only have 3 tries.
                        if (u.getPassword().equals(Digester.hashWithSalt(newInfo))) {
                            tries = 3;
                            System.out.println("Enter your new password:");
                            newInfo = input.nextLine();
                            u.setPassword(Digester.hashWithSalt(newInfo));
                            fireRequest = true;
                        } else {
                            System.err.println("You entered a wrong password! Tries left: " + (3 - tries));
                            fireRequest = false;
                            if(tries > 2)
                                MainController.currentUser = null; // Logout if 3 wrong tries.
                        }
                    } while (tries < 3);
                    break;
                case 6:
                    fireRequest = false;
                    stopLoop = true;
                    break;
                default:
                    System.err.println("Wrong input, try again!");
                    fireRequest = false;
                    break;

            }
        } while (choice > 6);

        // If the info is entered correct, the servercall is executed.
        if (fireRequest) {
            String s = "user/" + u.getUserID();
            userService.editUser(s, u, new ResponseCallback<String>() {
                @Override
                public void success(String data) {
                    System.out.println("User edited successful!\n");
                }

                @Override
                public void error(int status) {

                }
            });
        }
    }

    /**
     * This method shows all users if the origin request is from admin and lets the admin delete an user.
     *
     * @param cachedData
     */
    public void deleteUser(CachedData cachedData) {
        int userId = 0;

        /**
         * If the currentuser is admin
         */
        if (MainController.currentUser.getUserType() == true) {
            getAllUsers(cachedData); // Show all users
            if (MainController.currentUser != null) {
                System.out.println("Please enter id on the user you wish to delete: ");
                userId = input.nextInt();
                input.nextLine();
                System.out.println("Are you sure you want to delete the account? Write \"yes\" to confirm");
                if (!input.nextLine().equals("yes")) {
                    userId = 0;
                    System.err.println("You did not enter yes! Deletion cancelled...");
                }
            }
        } else { //If the currentuser is normal user
            System.out.println("Are you sure that you want to delete your account? Write \"yes\" to confirm");
            if (input.next().equals("yes")) {
                userId = MainController.currentUser.getUserID();
            } else {
                System.err.println("You did not enter yes! Deletion cancelled...");
            }
        }
        //If a valid userid is found, the request is send to the server
        if (userId != 0) {
            final int newUserId = userId;
            String s = "user/" + userId;
            userService.deleteUser(s, new ResponseCallback<Boolean>() {
                @Override
                public void success(Boolean data) {
                    System.out.println("The account was deleted successfully!\n");
                    if (MainController.currentUser.getUserID() == newUserId) { //If the user has decided to delete his own account the user is logged out.
                        MainController.currentUser = null;
                    }

                }

                @Override
                public void error(int status) {
                    System.err.println(status);
                }
            });
        }
    }

    // retrieves userinfo on the user from a token
    public void getUserFromToken() {

        userService.getUserFromToken(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                MainController.currentUser = data;
            }

            @Override
            public void error(int status) {
                System.err.println(status);
            }
        });
    }

    public void createNewUser() {
        String firstName, lastName, username, email, password;
        System.out.println("Enter your firstname: ");
        firstName = input.nextLine();
        System.out.println("Enter your lastname: ");
        lastName = input.nextLine();
        System.out.println("Enter your username: ");
        username = input.nextLine();
        System.out.println("Enter your email: ");
        email = input.next();
        System.out.println("Enter your password: ");
        password = input.next();

        User user = new User(firstName, lastName, username, email, password, false);

        System.out.println("Are you sure that you want to create a new user with the following details:");
        System.out.printf("%-30s %-30s %-25s %-25s %-15s\n", "Username:", "Firstname:", "Lastname:", "Email:", "Admin status:");
        System.out.printf("%-30s %-30s %-25s %-25s %-15b\n", user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserType());
        System.out.println("Enter \"yes\" to confirm:");
        if (input.next().equals("yes")) {
            userService.create(user, new ResponseCallback<String>() {
                @Override
                public void success(String data) {
                    System.out.println("Success! User created. \n");
                }

                @Override
                public void error(int status) {
                    System.out.println(status);

                }
            });
        } else {
            System.err.println("You didn't enter yes. Returning to main menu\n");
        }
    }

    // Shows all users
    public void getAllUsers(CachedData cachedData) {

        userService.getAll(cachedData, new ResponseCallback<ArrayList<User>>() {

            @Override
            public void success(ArrayList<User> users) {
                // Header for showing users
                System.out.printf("%-15s %-30s %-30s %-25s %-25s %-15s\n", "User ID:", "Username:", "Firstname:", "Lastname:", "Email:", "Admin status:");
                for (User user : users) {
                    System.out.printf("%-15d %-30s %-30s %-25s %-25s %-15b\n", user.getUserID(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getUserType());
                }
            }

            @Override
            public void error(int status) {
                System.err.println("Error: " + status + " seems like your session has expired, please login again\n");
                MainController.currentUser = null;
            }
        });
    }

}
