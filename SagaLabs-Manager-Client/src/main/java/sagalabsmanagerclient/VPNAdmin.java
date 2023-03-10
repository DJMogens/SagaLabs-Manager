package sagalabsmanagerclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VPNAdmin {
    public static void getVPNApi(String apiEndpoint) {
        String vpnIp = "20.160.16.155"; //hardcoded for now. Meant to be retrieved from database.
        try {
            URL url = new URL("http://" + vpnIp + apiEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Basic " + AzureMethods.getKeyVaultSecret("sagavpn-api-key"));//get api key from vault


            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listUsers(){
        getVPNApi("/api/users/list");
    }

}
