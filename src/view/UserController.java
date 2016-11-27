package view;

import controller.MainController;
import com.google.gson.Gson;
import sdk.encrypters.Digester;
import sdk.model.CachedData;
import sdk.model.User;
import sdk.connection.ResponseCallback;
import sdk.services.UserService;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by magnusrasmussen on 29/10/2016.
 */
public class UserController {

    private Scanner input = new Scanner(System.in);

    private Gson gson = new Gson();
    private UserService userService = new UserService();

    public static boolean b;


    public void editUser() {
        User u = MainController.currentUser;
        String newInfo = null;
        int choice, tries = 0;
        boolean fireRequest = true;

        // Header for showing userinfo
        System.out.printf("%-30s %-30s %-25s %-25s %-25s\n", "Username:", "Firstname:", "Lastname:", "Email:", "Password:");
        System.out.printf("%-30s %-30s %-25s %-25s %-25s\n", u.getUserName(), u.getFirstName(), u.getLastName(), u.getEmail(), "*********");

        System.out.println("Press 1 to edit username\nPress 2 to edit firstname\nPress 3 to edit lastname\nPress 4 to edit email\nPress 5 to edit password\nPress 6 to cancel");
        choice = input.nextInt();
        input.nextLine();
        if (choice < 5) {
            System.out.println("Please enter new value: ");
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
                do {
                    System.out.println("Enter your old password: ");
                    newInfo = input.nextLine();
                    tries++;
                    if (u.getPassword().equals(Digester.hashWithSalt(newInfo))) {
                        tries = 3;
                        System.out.println("Enter your new password:");
                        newInfo = input.nextLine();
                        u.setPassword(Digester.hashWithSalt(newInfo));
                        fireRequest = true;
                    } else {
                        System.out.println("You entered a wrong password! Tries left: " + (3 - tries));
                        fireRequest = false;
                    }
                } while (tries < 3);
                break;
            case 6:
                fireRequest = false;
                break;
            default:
                System.out.println("Wrong input");
                fireRequest = false;
                break;

        }

        if (fireRequest) {
            String s = "user/" + u.getUserID();
            userService.editUser(s, u, new ResponseCallback<String>() {
                @Override
                public void success(String data) {
                    System.out.println(data);
                }

                @Override
                public void error(int status) {

                }
            });
        } else {
            MainController.logout();
        }
    }

    public void deleteUser(CachedData cachedData) {
        int userId = 0;

        if (MainController.currentUser.getUserType() == true) {
            getAllUsers(cachedData);
            System.out.println("Pleaser enter id on the user you wish to delete: ");
            userId = input.nextInt();
            System.out.println("Are you sure you want to delete tha account? Write \"yes\" to confirm");
            if (!input.nextLine().equals("yes")) {
                userId = 0;
            } else {
                System.out.println("You did not enter yes! Deletion cancelled...");
            }
        } else {
            System.out.println("Are you sure that you want to delete your account? Write \"yes\" to confirm");
            if (input.next().equals("yes")) {
                userId = MainController.currentUser.getUserID();
            } else {
                System.out.println("You did not enter yes! Deletion cancelled...");
            }
        }
        if (userId != 0) {
            String s = "user/" + userId;
            userService.deleteUser(s, new ResponseCallback<Boolean>() {
                @Override
                public void success(Boolean data) {
                    MainController.logout();

                }

                @Override
                public void error(int status) {
                    System.out.println(status);
                }
            });
        }
    }

    public void getUserFromToken() {

        userService.getUserFromToken(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                MainController.currentUser = data;
            }

            @Override
            public void error(int status) {
                System.out.println(status);
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
                    System.out.println(data);
                }

                @Override
                public void error(int status) {
                    System.out.println(status);

                }
            });
        } else {
            System.out.println("You didn't enter yes. Returning to main menu");
        }
        input.nextLine();
    }

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

            }
        });
    }

}
