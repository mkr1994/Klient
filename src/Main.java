import controller.MainController;

import java.io.IOException;


/**
 * Created by magnusrasmussen on 24/10/2016.
 */
public class Main  {
    public static void main(String[] args) throws IOException {

        MainController mainController = new MainController();

        mainController.run();
    }
}

