package au.com.acumen.faces.utils;

import au.com.acumen.faces.model.CandidatesApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;


@Component
public class AcuMenAlertApis {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Value("${spring.alertsUrl}")
    private String ALERTS_URL;

    @Value("${spring.alertsEmailUrl}")
    private String ALERTS_EMAILURL;



    public AcuMenAlertApis() {
    }

    public int sendAlert(String candidateTitle, String cameraName, String candidateImg, String eventDateTime) {

        URIBuilder builder = null;

//        MultipartEntity reqEntity = new MultipartEntity();


        HttpClient httpClient = HttpClients.createDefault();
        int statusCode = 0;
        byte[]imageByteArray = Base64.getDecoder().decode(candidateImg);
//        String candidateImg = candidateImg.toString();
        File imgFile =  new File("bestImage.jpg");
        FileBody uploadFilePart = new FileBody(imgFile);
//        reqEntity.addPart("photoThumbnail", uploadFilePart);


//        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
////        parts.add("candidateTitle", candidateTitle);
////        parts.add("cameraName", cameraName);
//        parts.add("photoThumbnail", imgFile);
////        parts.add("eventDateTime", eventDateTime);
//
//        restTemplate.postForObject(ALERTS_EMAILURL,parts,  String.class);

        try {
            builder = new URIBuilder(ALERTS_EMAILURL);
            builder.setParameter("candidateTitle", candidateTitle);
            builder.setParameter("cameraName", cameraName);
            builder.setParameter("photoThumbnail", String.valueOf(imageByteArray));
            builder.setParameter("eventDateTime", eventDateTime);
            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);
//            request.setEntity(reqEntity);
//            request.setHeader("Content-Type", "multipart/form-data");

//            // Call Acu-MenAlerts Api.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            statusCode = response.getStatusLine().getStatusCode();


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return statusCode;
    }
    public int sendApiRequest(String candidateTitle, String cameraName, byte[] candidateImg, String eventDateTime) {

//        URIBuilder builder = null;

        int statusCode = 0;
//        BASE64Decoder decoder = new BASE64Decoder();
//        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        try {
            CandidatesApi candidatesApi = new CandidatesApi();
//            byte[]   bytesEncoded = Base64.getEncoder().encode(candidateImg);
            String base64Encoded = DatatypeConverter.printBase64Binary(candidateImg);
            byte[] base64Decoded = DatatypeConverter.parseBase64Binary(base64Encoded);
//            byte[]imageByteArray = candidateImg;
//            FileOutputStream imageOutFile = new FileOutputStream(
//                    "bestImage.jpg");
//            imageOutFile.write(imageByteArray);
//            File imgFile = new File("bestImage.jpg");
//            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//            builder = new URIBuilder(ALERTS_EMAILURL);
//            HashMap candidatesApiH = new HashMap();
            candidatesApi.setCameraName(cameraName);
            candidatesApi.setCandidateTitle(candidateTitle);
            candidatesApi.setEventDateTime(eventDateTime);
            candidatesApi.setPhotoThumbnail(base64Decoded);
//            candidatesApiH.put("CameraName",cameraName);
//            candidatesApiH.put("candidateTitle",candidateTitle);
//            candidatesApiH.put("eventDateTime",eventDateTime);
//            candidatesApiH.put("PhotoThumbnail",imageByteArray);
//            nameValuePairs.add(new BasicNameValuePair("candidatesApi", candidatesApi.toString()));

//            urlParameters.add(new BasicNameValuePair("candidateTitle", candidateTitle));
//            urlParameters.add(new BasicNameValuePair("cameraName", cameraName));
//            urlParameters.add(new BasicNameValuePair("photoThumbnail", imageByteArray.toString()));
//            urlParameters.add(new BasicNameValuePair("eventDateTime", eventDateTime));

//            builder.setParameters((NameValuePair) nameValuePairs);
//            String json = "{\"serialDataByte\":\""+ new String(bytesEncoded) +"\"}";
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(candidatesApi);
//            System.out.println(jsonInString);
            System.out.println("\nSending 'POST' request to URL : " + ALERTS_EMAILURL);
//            String  candidatesFile = "{" +
//                                    "\"cameraName\": + " + cameraName +", "+
//                    "\"candidateTitle\": + " + candidateTitle + ", " +
//                    "\"eventDateTime\": +  "+ eventDateTime + ", " +
//                    "\"photoThumbnail\": + " +"{ ["   + base64Decoded + " " +"] }}";

            StringEntity entity = new StringEntity(jsonInString,
                    ContentType.APPLICATION_JSON);

            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpPost request = new HttpPost(ALERTS_EMAILURL);

            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);

//
//            StringEntity input = new StringEntity(candidatesApi);
//            input.setContentType("application/json");
//            postRequest.setEntity(input);
//            URI uri = builder.build();
//            HttpPost post = new HttpPost(ALERTS_EMAILURL+ "/" + candidatesApi);
////            post.setHeader("User-Agent", USER_AGENT);
//
////            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
////            // Call Acu-MenAlerts Api.
//
//            HttpResponse response = httpClient.execute(post);
//            HttpEntity entity = response.getEntity();


            System.out.println("Post parameters : " + response.getEntity());
            System.out.println("Response Code : " +
                    response.getStatusLine().getStatusCode());

            statusCode = response.getStatusLine().getStatusCode();


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return statusCode;
    }






}
