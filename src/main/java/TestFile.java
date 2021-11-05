import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TestFile {
    public static void main(String args[]) {
        File myObj = new File("test.json");
        if(myObj.exists()) {
            System.out.println("Exists");
        }
        else {
            System.out.println("Does not exist");
        }
        try {
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            JSONObject test1 = new JSONObject();
            test1.put("PrivateKey",
            		"bjfcasbchvuacyahcds65cd7svcds8v9ds7v86ds8V9Sd");
            test1.put("PublicKey",
            		"bhfds7ft7s8gvwec8e9vbccyebcewucu8wycwvec7wevcwe78cvewubcew");
            test1.put("DidID",
            		"snivuisnfvsd:busvuhdsvcudsbvsbvka");
            System.out.println(test1);
            FileWriter myWriter = new FileWriter("test.json");
            myWriter.write(test1.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse
            		(new FileReader("test.json"));
            String value = (String) jsonObject.get("PublicKey");
            System.out.println("TEST: " + value);


        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
