package com.parkingmanager.parkingmanager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


@Service
@EnableScheduling
public class ParkingService {

    List<ObjectNode> flowParkingData;
    ParkingRepository parkingRepository;
    HoodRepository hoodRepository;

    List<Object> rijekaParkingData;








    private final String rijekaApiUrl = "https://www.rijeka-plus.hr/wp-json/restAPI/v1/parkingAPI/";

    private String getIdParkingSinka(String ipRoutera) throws Exception {

        try {
            TrustManager[] trustAllCertificates = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Create an SSL context that uses the custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

            // Set the custom SSL context as the default for HTTPS connections
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            // Specify the URL
            URL url = new URL("https://"+ipRoutera+":8088/cubes/0/analytics/0/sinks");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the request headers
            connection.setRequestProperty("Accept-Version", "2.0");
            connection.setRequestProperty("Authorization", "Bearer VTLMQILFQG");

            // Get the response code
            int responseCode = connection.getResponseCode();
           // System.out.println("Response Code: " + responseCode);

            // Read and log the response body
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Log the response body
            //System.out.println("Response Body:");
            //System.out.println(response.toString());
            connection.disconnect();
            for(String s: response.toString().split("history_capacity")) {

                //System.out.println(s);

                if(s.split(",").length>4)
                    if(s.split(",")[4].split(":")[1].contains("PARKINGDATA"))
                        return s.split(",")[3].split(":")[1];
            }

            // Close the connection

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("PARKINGDATA sink ne postoji na ovoj kameri");
            throw new Exception();
        }
        return null;
    }

    private String getSeqNumber(String ipRoutera) throws  Exception {
        System.out.println("ULAZIM U GET SEQ NUMBER ZA IP "+ ipRoutera);

        try {
            TrustManager[] trustAllCertificates = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Create an SSL context that uses the custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

            // Set the custom SSL context as the default for HTTPS connections
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            // Specify the URL
            URL url = new URL("https://"+ipRoutera+":8088/cubes/0/analytics/0/sinks");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the request headers
            connection.setRequestProperty("Accept-Version", "2.0");
            connection.setRequestProperty("Authorization", "Bearer VTLMQILFQG");

            // Get the response code
            int responseCode = connection.getResponseCode();
            //System.out.println("Response Code: " + responseCode);

            // Read and log the response body
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Log the response body
            //System.out.println("Response Body:");
            //System.out.println(response.toString());
            connection.disconnect();
            return response.toString().split(",")[0].split(":")[1].replace("\"", "");

            // Close the connection

        } catch (Exception e) {
           // e.printStackTrace();
            System.out.println("Seq number nije nabavljen - kamera u kurcu");
            throw new Exception();
        }

    }

    private String brojZauzetihParkingMjestaFlow(String ipRouter) {

        System.out.println("OBRADUJEM ROUTER "+ ipRouter);

        try {
            TrustManager[] trustAllCertificates = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Create an SSL context that uses the custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
            System.out.println(ipRouter);
            // Set the custom SSL context as the default for HTTPS connections
            URL url = new URL("https://"+ipRouter+":8088/cubes/0/analytics/0/sinks/data");

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Accept-Version", "2.0");
            connection.setRequestProperty("Authorization", "Bearer VTLMQILFQG");
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams for sending/receiving data
            connection.setDoOutput(true);
            System.out.println("PRVO MI TREBA SEQ NUMBER PA GA IDEM VIDJET");
            String seqNumber= getSeqNumber(ipRouter);
            System.out.println("SEQ NUMBER JE NABAVLJEN TE JE ON "+ seqNumber);
            System.out.println("SADA MI TREBA SINK ID, IDEM GA NABAVIT");
            String sinkId = getIdParkingSinka(ipRouter);
            System.out.println("SINKID JE NABAVLJEN TE ON IZNOSI"+ sinkId);


            // Define the JSON body
            String jsonBody = "{\"sequence_number\": \"" + seqNumber + "\", \"sinks\": [{\"id\": " + sinkId + "}]}";

            // Write the JSON body to the request
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(jsonBody);
                outputStream.flush();
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
           System.out.println("Response Code: " + responseCode);

            // Read and log the response body
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // Log the response body

            connection.disconnect();
           // System.out.println("----------------");
          //  for(String s :response.toString().split("category") ) {
            //    System.out.println(s);
           // }

            System.out.println( response.toString().split("category"));
            int len = response.toString().split("category").length;



           // System.out.println("----------------");
            if(isNumeric(findCarData(response.toString().split("category")))) {
                System.out.println("DOBIO SAM PODATKE TE BROJ ZAUZETIH IZNOSI "+ findCarData(response.toString().split("category")));
                return findCarData(response.toString().split("category"));

            }
            else {

                return "error";
            }

            // Close the connection

        } catch (Exception e) {
            System.out.println("NEGDJE JE DOSLO DO GRESKE TIJEKOM KOMUNIKACIJE S KAMEROM TE VRACAM ERROR");
            return "error";
        }

    }

    private String findCarData(String[] categories) {
        String data = "";
        int len = categories.length;
        for(int i =0;i<len;i++) {
            if(categories[i].split(",")[0].split(":")[1].replace("\"", "").equals("car"))
                data=categories[i].split(",")[1].split(":")[1].replace("}","");

        }
        return data;
    }


    public static String fetchContentFromUrl(String urlLink) throws IOException {

        System.out.println("U FUNKCIJI SAM KOJA FETCHA RIJEKA PODATKE");
        // Create a URL object
        URL url = new URL(urlLink);
        HttpURLConnection connection;


        // Open a connection to the URL


        try {
            connection = (HttpURLConnection) url.openConnection();
            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Set the request header if needed (e.g., User-Agent)
            // connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response content
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();
                return response.toString();
            } else {
                System.out.println("GRESKA PRILIKOM FETCHANJA RIJEKA PODATAKA");
                throw new IOException("HTTP error code: " + responseCode);
            }
        }
        catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }
    @Autowired
    ParkingService(ParkingRepository parkingRepository,HoodRepository hoodRepository) {
        this.parkingRepository = parkingRepository;
        this.hoodRepository=hoodRepository;
        rijekaParkingData=new ArrayList<>();
        flowParkingData= new ArrayList<>();



        System.out.println("----------------------------------");
        System.out.println("INICIJALIZIRAN PARKING SERVICE");
        System.out.println("----------------------------------");

        //System.out.println("EVO ME U PARKING SERVICU I RADIM STVARI");
        //System.out.println(getSeqNumber("192.168.30.108"));
        //System.out.println(getIdParkingSinka("192.168.30.108"));
        //System.out.println("EVO BROJ ZAUZETIH");
        //System.out.println(brojZauzetihParkingMjestaFlow("192.168.30.108"));
        //System.out.println(brojZauzetihParkingMjestaFlow("192.168.30.108"));
        //System.out.println("------------------------");
        System.out.println("KRECEM POPUNJAVATI FLOW I RIJEKA PARKING DATA");
        popuniParkingMapu();

    }

    List<Object> updateRijekaParking() {

        System.out.println("UŠAO SAMU U FUNKCIJU UPDATERIJEKAPARKING");


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("KRECEM FETHCATI RIJEKA PODATKE");
            String jsonString = fetchContentFromUrl(rijekaApiUrl);
            if(jsonString==null)
                throw  new IOException("RIJEKA PARKING PODATCI SU VRATILI NULL");

            List<Object> jsonList = objectMapper.readValue(jsonString, List.class);


            System.out.println("FETHCAO SAM RIJEKA PARKING PODATKE TE SU ONI "+jsonString);
            System.out.println("IZLAZIM IZ UPDATE RIJEKA PARKING FUNKCIJE");

            return jsonList;

            } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;

        }




    }
    void popuniParkingMapu()  {

        //List<Parking> parkings = parkingRepository.findByHoodId("64fa06044672c77de9350cca");
        //System.out.println(parkings.toString());

        System.out.println("POCINJEM INICIJALIZIRATI FLOW PARKING DATA");
        flowParkingData = updateFlowParkingDataInit();
        System.out.println("FLOW PARKING DATA JE INICIJALIZIRAN I IZGLEDA OVAKO " + flowParkingData);

        System.out.println("POCINJEM INICIJALIZIRATI RIJEKA PARKING DATA");
        rijekaParkingData = updateRijekaParking();
        System.out.println("RIJEKA PARKING DATA JE INICIJALIZIRANA I IZGLEDA OVAKO "+ rijekaParkingData);


        //getRijekaParkingName(rijekaParkingData.get(16).toString());
        //getRijekaBrojSlobodnih(rijekaParkingData.get(16).toString());
        //getRijekaParkingDate(rijekaParkingData.get(16).toString());






    }

    private List<ObjectNode> updateFlowParkingDataInit() {
        System.out.println("EVO ME U FLOW INIT FUNKCIJI");
        Calendar calendar = Calendar.getInstance();
        flowParkingData= new ArrayList<>();

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());

        // Get the current hour, minute, and second
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String minuteStr = String.format("%02d", minute);

        // Create a big string by concatenating the date, hour, minute, and second
        String dateTimeString = currentDate + " " + hour + ":" + minuteStr;
        System.out.println("DATUM UPDATEA JE "+ dateTimeString);
        List<Parking> sviParkinzi = parkingRepository.findAll();
        List<Parking> flowParkinzi = sviParkinzi.stream()
                .filter(parking -> "yes".equals(parking.getFlow()))
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("NASAO SAM FLOW PARKINGE IZ BAZE I SADA IH IDEM INICIJALIZIRATI TO SU: "+flowParkinzi);
        for(Parking parking: flowParkinzi) {
            ObjectNode jsonParking = objectMapper.createObjectNode(); // Create an empty JSON object
            System.out.println("ZA FLOW PARKING"+ parking.toString() + "POCINJEM SETATI ID,BROJ ZAZETIH I DATUMZADNJEPROMJENE");


                jsonParking.put("id", parking.getId());
                jsonParking.put("brojZauzetih", "error");
                jsonParking.put("datumZadnjePromjene", dateTimeString);

            flowParkingData.add(jsonParking);

            System.out.println("U FLOW PARKING DATA SAM DODAO " + jsonParking);
        }

        System.out.println("INICIJALIZIRAO SAM USPJEŠNO FLOW PARKINGE TE IZLAZIM IZ FUNCKIJE");


        return flowParkingData;

    }

    private String getRijekaBrojSlobodnih(String row) {
        String brojSlobodnih = row.split(",")[5].split("=")[1];

       // System.out.println(brojSlobodnih);
        return brojSlobodnih.toLowerCase().strip();
    }

    String getRijekaParkingName(String row) {
        String name="";

      name=row.split(",")[0].split("=")[1];

        return name.toLowerCase().strip();

    }
    String getRijekaParkingDate(String row) {
        String datumUpdatea = row;
        datumUpdatea=row.split(",")[8].split("=")[1]+ " " + row.split(",")[9].split("=")[1];

        return datumUpdatea.toLowerCase().strip();
    }



    private List<ObjectNode> updateFlowParkingData() {
        System.out.println("EVO ME U FUNCKIJI KOJA UPDATA FLOW PARKING DATA");
        Calendar calendar = Calendar.getInstance();
        List<ObjectNode >flowParkingDataTemp= new ArrayList<>();

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());

        // Get the current hour, minute, and second
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String minuteStr = String.format("%02d", minute);

        // Create a big string by concatenating the date, hour, minute, and second
        String dateTimeString = currentDate + " " + hour + ":" + minuteStr;
        System.out.println("DATUM UPDATA FLOW PARKINGA JE "+ dateTimeString);
        List<Parking> sviParkinzi = parkingRepository.findAll();
        List<Parking> flowParkinzi = sviParkinzi.stream()
                .filter(parking -> "yes".equals(parking.getFlow()))
                .collect(Collectors.toList());
        System.out.println("NASO SAM SVE FLOW PARKINGE ONI SU "+flowParkinzi );

        ObjectMapper objectMapper = new ObjectMapper();
        for(Parking parking: flowParkinzi) {
            ObjectNode jsonParking = objectMapper.createObjectNode(); // Create an empty JSON object
            System.out.println("ZA FLOW PARKING " + parking+ " POCINJEM SETATI ID,BROJ ZAZETIH I DATUMZADNJEPROMJENE");
            System.out.println("ZOVEM METODU KOJA CE POKRENUT POSTUPAK FETCHANJA PODATKA SA KAMERE ZA NAVEDENI PARKING");
            String data = getFlowData(parking);
            System.out.println("ZA OVAJ PARKING BROJ ZAUZETIH JE " + data);


            if(!data.contains("error")) {
                System.out.println("DATA JE DOBRA STOGA UPDEJTAM PARKING INFO");
                jsonParking.put("id", parking.getId());
                jsonParking.put("brojZauzetih", data);
                jsonParking.put("datumZadnjePromjene", dateTimeString);
                System.out.println("NOVA FLOW PARKING DATA ZA PARKING"+parking +" JE "+jsonParking);
                flowParkingDataTemp.add(jsonParking);
            }
            else {
                ObjectNode stariParking= objectMapper.createObjectNode();
                System.out.println("DATA CONTAINA ERROR TJ DOSLO JE DO GRESKE");
                System.out.println("NE UPDEJATAM NOVU DATU VEC OSTAVLJAM PROSLU, STOGA MORAM DOHVATIT PROSLU DATU ZA PARKING " + parking);
                for (ObjectNode node : flowParkingData) {


                    if (node.get("id").toString().replace("\"", "").equals(parking.getId())) {
                        stariParking=node;
                        System.out.println("USPJESNO SAM DOHVATIO STARU DATU ONA JE "+ node);
                    }
                }

                flowParkingDataTemp.add(stariParking);


            }

        }


        flowParkingData=flowParkingDataTemp;
        return flowParkingData;
    }

    private String getFlowData(Parking parking) {
        System.out.println("ZA PARKING" +parking.getName() +"IDEM VIDJET PODATKE");
        if(parking.getBrKamera().equals("1")) {
            System.out.println("IMA JEDNU KAMERU");
                return brojZauzetihParkingMjestaFlow(parking.getIpRoutera1().strip()); // promijeni vrijednosti parking date u get ip routera1 i get br kamera
        }
        if(parking.getBrKamera().equals("2"))
            return brojZauzetihParkingMjestaFlow2(parking.getIpRoutera1(),parking.getIpRoutera2());
        if(parking.getBrKamera().equals(3))
            return brojZauzetihParkingMjestaFlow3(parking.getIpRoutera1(),parking.getIpRoutera2(),parking.getIpRoutera3());
        return null;
    }

    private String brojZauzetihParkingMjestaFlow3(String brRoutera1, String brRoutera2, String brRoutera3) {
        String broj1 = brojZauzetihParkingMjestaFlow(brRoutera1);
        String broj2 = brojZauzetihParkingMjestaFlow(brRoutera2);
        String broj3 = brojZauzetihParkingMjestaFlow(brRoutera3);

        return Integer.toString(Integer.parseInt(broj1) + Integer.parseInt(broj2) + Integer.parseInt(broj3));
        
    }

    private String brojZauzetihParkingMjestaFlow2(String brRoutera1, String brRoutera2) {

        String broj1 = brojZauzetihParkingMjestaFlow(brRoutera1);
        String broj2 = brojZauzetihParkingMjestaFlow(brRoutera2);

        return Integer.toString(Integer.parseInt(broj1) + Integer.parseInt(broj2));
    }


    Parking saveParking(Parking parking) {
        Parking ret=parkingRepository.save(parking);
        return ret;

    }





    public List<Parking> getHoodParkings(String hoodId) {


        List<Parking> parkings = parkingRepository.findByHoodId(hoodId);
        //System.out.println(parkings.get(0));

        // Calculate and set brojZauzetih for each parking
        for (Parking parking : parkings) {

            if ("yes".equals(parking.getSmartRi())) {
                //System.out.println(parking.getName());
                //System.out.println(parking.getBrojParkinga());
                // System.out.println(brojZauzetihSmartRi(parking.getName().strip().toLowerCase()));


                parking.setBrojZauzetih(Integer.toString(Integer.parseInt(parking.getBrojParkinga()) - Integer.parseInt(brojZauzetihSmartRi(parking.getName().strip().toLowerCase())))); // Assuming you have a setter for brojZauzetih in the Parking class
                parking.setLastUpdateTime(lastUpdateTimeSmartRi(parking.getName().toLowerCase().strip()));
            }

            if ("yes".equals(parking.getFlow())) {
                // System.out.println("FLOW PARKING JE "+ parking.toString() );
                String data = brojZauzetihFlow(parking.getId().strip().toLowerCase());
                parking.setBrojZauzetih(data); // Assuming you have a setter for brojZauzetih in the Parking class
                parking.setLastUpdateTime(lastUpdateTimeFlow(parking.getId().strip().toLowerCase()));


            }

        }
        return parkings;

    }

        public List<Parking> getCityParkings(String cityId) {



            List<Parking> parkings = parkingRepository.findByCityId(cityId);
            //System.out.println(parkings.get(0));

            // Calculate and set brojZauzetih for each parking
            for (Parking parking : parkings) {

                if("yes".equals(parking.getSmartRi())) {
                    //System.out.println(parking.getName());
                    //System.out.println(parking.getBrojParkinga());
                    // System.out.println(brojZauzetihSmartRi(parking.getName().strip().toLowerCase()));


                    parking.setBrojZauzetih(Integer.toString(Integer.parseInt(parking.getBrojParkinga())-Integer.parseInt(brojZauzetihSmartRi(parking.getName().strip().toLowerCase())))); // Assuming you have a setter for brojZauzetih in the Parking class
                    parking.setLastUpdateTime(lastUpdateTimeSmartRi(parking.getName().toLowerCase().strip())); }

                if ("yes".equals(parking.getFlow())) {
                    // System.out.println("FLOW PARKING JE "+ parking.toString() );
                    String data = brojZauzetihFlow(parking.getId().strip().toLowerCase());
                    parking.setBrojZauzetih(data); // Assuming you have a setter for brojZauzetih in the Parking class
                    parking.setLastUpdateTime(lastUpdateTimeFlow(parking.getId().strip().toLowerCase()));



                }

            }



        return parkings;

    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Matches integers and decimals
    }


    private String brojZauzetihFlow(String id){
        String brojZauzetih="";
        for(ObjectNode parking: flowParkingData) {
            //System.out.println(id);
            //System.out.println(parking.get("id").toString().substring(1,parking.get("id").toString().length()-1));
            //System.out.println("---------------------");
            if(parking.get("id").toString().substring(1,parking.get("id").toString().length()-1).strip().toLowerCase().equals(id))
                brojZauzetih=parking.get("brojZauzetih").toString().substring(1,parking.get("brojZauzetih").toString().length()-1);

        }
        return brojZauzetih;
    }

    private String lastUpdateTimeFlow(String id) {
        String lastUpdateTime="";
        for(ObjectNode parking: flowParkingData) {
            if(parking.get("id").toString().substring(1,parking.get("id").toString().length()-1).toLowerCase().equals(id))
                lastUpdateTime=parking.get("datumZadnjePromjene").toString().substring(1,parking.get("datumZadnjePromjene").toString().length()-1);

        }
        return lastUpdateTime;
    }



    private String lastUpdateTimeSmartRi(String name) {
        String lastUpdateTime="";
        for(Object parking: rijekaParkingData) {
            if(getRijekaParkingName(parking.toString()).equals(name))
                lastUpdateTime=getRijekaParkingDate(parking.toString());

        }
        return lastUpdateTime;
    }

    private String brojZauzetihSmartRi(String name) {
        String br="";
        for(Object parking: rijekaParkingData) {
            if(getRijekaParkingName(parking.toString()).equals(name))
                br=getRijekaBrojSlobodnih(parking.toString());

        }
        return br;
    }

    @Scheduled(fixedRate = 20000) // Run every 2 minutes (2 * 60,000 milliseconds)
    public void updateParkingData() {
        System.out.println("SCHEDULER JE I POZVAN SAM, KRECEM UPDATATI PODATKE");
        System.out.println("SCEDULER JE I POCINJEM UPDATATI RIJEKA PARKING PODATKE");
        List<Object> rijeka = updateRijekaParking();
        if(rijeka!=null)
        rijekaParkingData=rijeka;
        System.out.println("SCHEDULER JE, UPDEJTO SAM RIJEKA PARKING PODATKE I ONI SU "+ rijekaParkingData);
        System.out.println("SCHEDULER JE, KRECEM UPDATATI FLOW PARKING PODATKE");
        flowParkingData=updateFlowParkingData();
        System.out.println("SCHEDULER JE, UPDEJTO SAM FLOW PARKING PODATKE I ONI SU "+ flowParkingData);

        // Iterate through each parking and simulate real-world changes


    }


    public Parking getDetalji(String parkingId) {
        //System.out.println(flowParkingData);
        Optional<Parking> parkingOpt = parkingRepository.findById(parkingId);
        if(parkingOpt.isPresent()){
         Parking parking = parkingOpt.get();
            if("yes".equals(parking.getSmartRi())) {
                //System.out.println(parking.getName());
                //System.out.println(parking.getBrojParkinga());
                //System.out.println(brojZauzetihSmartRi(parking.getName().strip().toLowerCase()));


                parking.setBrojZauzetih(Integer.toString(Integer.parseInt(parking.getBrojParkinga())-Integer.parseInt(brojZauzetihSmartRi(parking.getName().strip().toLowerCase())))); // Assuming you have a setter for brojZauzetih in the Parking class
                parking.setLastUpdateTime(lastUpdateTimeSmartRi(parking.getName().toLowerCase().strip())); }

            if ("yes".equals(parking.getFlow())) {
               // System.out.println("FLOW PARKING JE "+ parking.toString() );
                String data = brojZauzetihFlow(parking.getId().strip().toLowerCase());
                parking.setBrojZauzetih(data); // Assuming you have a setter for brojZauzetih in the Parking class
                parking.setLastUpdateTime(lastUpdateTimeFlow(parking.getId().strip().toLowerCase()));






            }




         return parking;
        }
        else
            return null;
    }
}
