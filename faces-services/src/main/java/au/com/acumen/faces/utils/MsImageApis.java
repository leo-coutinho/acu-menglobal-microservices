package au.com.acumen.faces.utils;

import au.com.acumen.faces.model.ImageDetect;
import au.com.acumen.faces.model.ImageIdentify;
import au.com.acumen.faces.model.Person;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;

//import sun.misc.BASE64Decoder;


@Component
public class MsImageApis {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${spring.identityUrl}")
    private String IDENTITY_URL;

    @Value("${spring.detectUrl}")
    private String DETECT_URL;


    @Value("${spring.persongroupUrl}")
    private String PERSON_GROUP;

    @Value("${spring.subscriptionKey}")
    private String SUBSCRIPTION_KEY;

    @Value("${spring.groupId}")
    private String GROUP_ID;

    @Value("${spring.faceListUrl}")
    private String FACE_LIST_URL;

    @Value("${spring.similarUrl}")
    private String FIND_SIMILAR_URL;


//    public MsImageApis(String IDENTITY_URL, String DETECT_URL, String PERSON_GROUP,
//                       String SUBSCRIPTION_KEY, String GROUP_ID) {
//        this.IDENTITY_URL = IDENTITY_URL;
//        this.DETECT_URL = DETECT_URL;
//        this.PERSON_GROUP = PERSON_GROUP;
//        this.SUBSCRIPTION_KEY = SUBSCRIPTION_KEY;
//        this.GROUP_ID = GROUP_ID;
//    }

    public MsImageApis() {
    }

    public ImageDetect detectImage(String bestImage) {

        URIBuilder builder = null;
        HttpClient httpClient = HttpClients.createDefault();
        ImageDetect detectResponse = new ImageDetect();

        try {
            builder = new URIBuilder(DETECT_URL);
            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("returnFaceAttributes", "age,gender");
            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
//            BASE64Decoder decoder = new BASE64Decoder();
            byte[]imageByteArray = Base64.getDecoder().decode(bestImage);
            FileOutputStream imageOutFile = new FileOutputStream(
                    "bestImage.jpg");
            imageOutFile.write(imageByteArray);

            File imgFile = new File("bestImage.jpg");
//            FileInputStream inputStream = new FileInputStream(bestImage);
//            MultipartFile multipartFile = new MockMulti

            FileEntity reEntity = new FileEntity(imgFile, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reEntity);

            // Call Microsoft Azure for Image Detection.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String jsonString = EntityUtils.toString(entity).trim();
                if (jsonString.charAt(0) == '[') {
                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONArray jsonObArray = new JSONArray();

                    if(jsonString.charAt(1) != ']') {
                        jsonObArray.add(object);
                        JSONObject jsonObject = (JSONObject) (((JSONArray) jsonObArray.get(0)).get(0));

                        detectResponse.setFaceId((String) jsonObject.get("faceId"));
                        HashMap attributes = (HashMap) jsonObject.get("faceAttributes");
                        detectResponse.setGender((String) attributes.get("gender"));
                        detectResponse.setAge((double) attributes.get("age"));
                        System.out.println("Detected FaceId = " + detectResponse.getFaceId());
                        System.out.println("Detected Gender = " + detectResponse.getGender());
                        System.out.println("Detected Age = " + detectResponse.getAge());
                    } else if (jsonString.charAt(0) == '{') {
                        JSONParser parser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
                        System.out.println(jsonObject.toString());
                    } else {
                        System.out.println(jsonString);
                    }
                }
            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    return detectResponse;
    }

    public String addPersonFace(String bestImage,String personId) {

        URIBuilder builder = null;
        HttpClient httpClient = HttpClients.createDefault();
        ImageDetect detectResponse = new ImageDetect();
        String persistedFaceId = "";

        try {
            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID + "/persons/" + personId + "/persistedFaces");
            URI uri = builder.build();

            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
            BASE64Decoder decoder = new BASE64Decoder();

            byte[]imageByteArray = decoder.decodeBuffer(bestImage);
//            byte[]imageByteArray = Base64.getDecoder().decode(bestImage);

            FileOutputStream imageOutFile = new FileOutputStream(
                    "bestImage.jpg");
            imageOutFile.write(imageByteArray);

            File imgFile = new File("bestImage.jpg");

//            File imgFile = new File("bestImage.jpg");

            FileEntity reEntity = new FileEntity(imgFile, ContentType.APPLICATION_OCTET_STREAM);
            request.setEntity(reEntity);

            // Call Microsoft Azure for Image Detection.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();

                if (jsonString.charAt(0) == '{') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONObject jsonObject = (JSONObject) object;
                    persistedFaceId = ((String) jsonObject.get("persistedFaceId"));


                    System.out.println("Detected persistedFaceId = "+ persistedFaceId);

                }

            }



        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return persistedFaceId;
    }

    public ImageIdentify identifyImage(String faceId) {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        ImageIdentify imageIdentify = new ImageIdentify();

        try {
            builder = new URIBuilder(IDENTITY_URL);
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

            obj.put("personGroupId", GROUP_ID);
            JSONArray list = new JSONArray();
            list.add(faceId);

            obj.put("faceIds", list);
//            obj.put("maxNumOfCandidatesReturned",1);
//            obj.put("confidenceThreshold", 0.5);

            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
            request.setEntity(reqEntity);

            // Call Microsoft Azure for Image Identification.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();

                if (jsonString.charAt(0) == '[') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONArray jsonObArray = new JSONArray();
                    jsonObArray.add(object);

                    JSONObject jsonObject = (JSONObject) (((JSONArray) jsonObArray.get(0)).get(0));

                    HashMap cand = new HashMap();
                    cand.put("candidates",jsonObject.get("candidates"));
                    if(!cand.isEmpty()) {
                        JSONArray candidates = new JSONArray();
                        candidates.add(cand.get("candidates"));
                        if(((JSONArray) candidates.get(0)).size() > 0) {
                            JSONObject candidatesObj = (JSONObject) (((JSONArray) candidates.get(0)).get(0));
                            if (candidatesObj.size() > 1) {
                                imageIdentify.setPersonId((String) candidatesObj.get("personId"));
                                imageIdentify.setConfidence((double) candidatesObj.get("confidence"));
                                System.out.println("Detected PersonId = " + imageIdentify.getPersonId());
                                System.out.println("Detected Confidence = " + imageIdentify.getConfidence());

                            } else {
                                System.out.println("No Candidates found with faceId = " + faceId);
                            }
                        }
                    }

                }

            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return imageIdentify;

    }
    public ImageIdentify findSimilar(String faceId) {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        ImageIdentify imageIdentify = new ImageIdentify();

        try {
            builder = new URIBuilder(FIND_SIMILAR_URL);
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

            obj.put("faceId", faceId);
            obj.put("faceListId", faceId);
//            JSONArray list = new JSONArray();
//            list.add(faceId);
//
//            obj.put("faceId", list);
//            obj.put("maxNumOfCandidatesReturned",5);

            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
            request.setEntity(reqEntity);

            // Call Microsoft Azure for Image Identification.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();

                if (jsonString.charAt(0) == '[') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONArray jsonObArray = new JSONArray();
                    jsonObArray.add(object);

                    JSONObject jsonObject = (JSONObject) (((JSONArray) jsonObArray.get(0)).get(0));

                    HashMap cand = new HashMap();
                    cand.put("candidates",jsonObject.get("candidates"));
                    if(!cand.isEmpty()) {
                        JSONArray candidates = new JSONArray();
                        candidates.add(cand.get("candidates"));
                        if(((JSONArray) candidates.get(0)).size() > 0) {
                            JSONObject candidatesObj = (JSONObject) (((JSONArray) candidates.get(0)).get(0));
                            if (candidatesObj.size() > 1) {
                                imageIdentify.setPersonId((String) candidatesObj.get("personId"));
                                imageIdentify.setConfidence((double) candidatesObj.get("confidence"));
                                System.out.println("Detected PersonId = " + imageIdentify.getPersonId());
                                System.out.println("Detected Confidence = " + imageIdentify.getConfidence());

                            } else {
                                System.out.println("No Candidates found with faceId = " + faceId);
                            }
                        }
                    }

                }

            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return imageIdentify;

    }

    public void getFaceList() {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();

        try {
            builder = new URIBuilder(FACE_LIST_URL);
//            builder.setParameter( GROUP_ID);

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
//            obj.put("personGroupId", GROUP_ID);
//
//            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
//            request..setEntity(reqEntity);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String content = EntityUtils.toString(entity);
                System.out.println(content);
                InputStream is = entity.getContent();
                String theString = IOUtils.toString(is, "UTF-8");

                String jsonString = EntityUtils.toString(entity);

                if (jsonString.charAt(0) == '[') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONObject jsonObject = (JSONObject) object;
                    String timeUTC = ((String) jsonObject.get("TimeUtc"));
                    String frame = ((String) jsonObject.get("FramesVisible"));
                    String position = ((String) jsonObject.get("Position"));
                    JSONArray jsonObArray = new JSONArray();
                    jsonObArray.add(jsonObject.get("Images"));


                }



                    System.out.println(EntityUtils.toString(entity));
            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    public JSONArray listPersonGroups() {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        JSONArray jsonObArray = new JSONArray();

        try {
            builder = new URIBuilder(PERSON_GROUP );
//            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID + "/persons");

//            builder.setParameter( GROUP_ID);

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
//            obj.put("personGroupId", GROUP_ID);
//
//            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
//            request..setEntity(reqEntity);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

//            String content = EntityUtils.toString(entity);
//            System.out.println(content);
//            InputStream is = entity.getContent();
//            String theString = IOUtils.toString(is, "UTF-8");

            String jsonString = EntityUtils.toString(entity);

            if (jsonString.charAt(0) == '[') {

                JSONParser parserr = new JSONParser();
                Object object = parserr.parse(jsonString);

                JSONArray jsonObject = (JSONArray) object;
//                JSONArray jsonObArray = new JSONArray();

//                String personGroupId = ((String) jsonObject.get("personGroupId"));
//                String name = ((String) jsonObject.get("name"));
//                String userData = ((String) jsonObject.get("userData"));
                jsonObArray.add(jsonObject);
//                jsonObArray.add(jsonObject.get("personGroupId"));
//                jsonObArray.add(jsonObject.get("name"));
//                jsonObArray.add(jsonObject.get("userData"));


            }



//            System.out.println(EntityUtils.toString(entity));
//        }

//            if (entity != null)
//            {
//                System.out.println(EntityUtils.toString(entity));
//            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return jsonObArray;
    }

    public String createPerson(String personName) {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        String personId = "";

        try {
            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID + "/persons");
//            builder.setParameter( GROUP_ID);

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
            obj.put("name", personName);
//            obj.put("personGroupId", GROUP_ID);
//
            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
            request.setEntity(reqEntity);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();

                if (jsonString.charAt(0) == '{') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONObject jsonObject = (JSONObject) object;
                    personId = ((String) jsonObject.get("personId"));


                    System.out.println("Detected PersonId = "+ personId);

                }

            }



        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

     return personId;
    }
    public int createGroupId() {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        int responseCode = 0;

        try {
//            builder = new URIBuilder(PERSON_GROUP + "/" );
            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID );
//            builder.setParameter( GROUP_ID);

            URI uri = builder.build();
            HttpPut request = new HttpPut(uri);
//            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
            obj.put("name", GROUP_ID);
//            obj.put("personGroupId", GROUP_ID);
//
            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
            request.setEntity(reqEntity);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
//            String jsonString = EntityUtils.toString(entity).trim();

            if (entity != null) {
                String jsonString = EntityUtils.toString(entity).trim();
                System.out.println("Create GroupID Result = " + jsonString);
            }
//
//                if (jsonString.charAt(0) == '{') {
//
//                    JSONParser parserr = new JSONParser();
//                    Object object = parserr.parse(jsonString);
//
//                    JSONObject jsonObject = (JSONObject) object;
//                    personId = ((String) jsonObject.get("personId"));
//
//
//                    System.out.println("Detected PersonId = "+ personId);
//
//                }
//
//            }
//


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseCode;
    }

    public Person getPerson(String personId) {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();
        Person person = new Person();

        try {
            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID + "/persons/" + personId);
//            builder.setParameter( GROUP_ID);

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);
//            obj.put("personGroupId", GROUP_ID);
//
//            StringEntity reqEntity = new StringEntity( obj.toJSONString() );
//            request..setEntity(reqEntity);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                String jsonString = EntityUtils.toString(entity).trim();

                if (jsonString.charAt(0) == '{') {

                    JSONParser parserr = new JSONParser();
                    Object object = parserr.parse(jsonString);

                    JSONObject jsonObject = (JSONObject) object;
                    person.setPersonId((String) jsonObject.get("personId"));
                    person.setName((String) jsonObject.get("name"));

                    JSONArray persistedFaces = new JSONArray();
                    persistedFaces.add(jsonObject.get("persistedFaceIds"));
//                    String candidatesObj = (String) (((JSONArray) persistedFaces.get(0)).get(0));

                    person.setPersistedFaceId((String)(((JSONArray) persistedFaces.get(0)).get(0)));


                    System.out.println("Detected PersonId = "+ person.getPersonId());
                    System.out.println("Detected name = "+ person.getName());
                    System.out.println("Detected persistedFaceIds = "+ person.getPersistedFaceId());

                }

//                    System.out.println(EntityUtils.toString(entity));
            }



        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    return person;

    }

    public void trainPersonGroup() {

        URIBuilder builder = null;
        JSONObject obj = new JSONObject();
        HttpClient httpClient = HttpClients.createDefault();

        try {
            builder = new URIBuilder(PERSON_GROUP + "/" + GROUP_ID + "/train");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
//            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", SUBSCRIPTION_KEY);

            // Call Microsoft Azure for Listing all groups.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            System.out.println("Trained Person Group "+ GROUP_ID);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        return person;

    }
}
