package au.com.acumen.faces.api;


import au.com.acumen.faces.model.*;
import au.com.acumen.faces.repository.*;
import au.com.acumen.faces.utils.AcuMenAlertApis;
import au.com.acumen.faces.utils.CustomErrorType;
import au.com.acumen.faces.utils.MsImageApis;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

//import sun.misc.BASE64Decoder;


@Api
@RestController
//@EnableEurekaClient
@RequestMapping(path="/messages")
public class AcceptMessagesController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String candidateImage = "";

    @Autowired
    MsImageApis msApis;

    @Autowired
    AcuMenAlertApis acuMenAlertApis;

    @Autowired
    private DetectionTableRepository detectionTableRepository;

    @Autowired
    private CameraTableRepository camerasTableRepository;

    @Autowired
    private CandidateFacesTableRepository candidateFacesTableRepository;

    @Autowired
    private CandidatesTableRepository candidatesTableRepository;


    @Autowired
    private MsRecognitionTableRepository msRecognitionTableRepository;

    @Autowired
    private MsCandidateRelationTableRepository msCandidateRelationTableRepository;



    @RequestMapping(method = RequestMethod.POST, path = "/acceptLocalMessage")
    @ApiOperation(value = "Process Candidate Image for the Acu-Men Global Faces")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity <ImageResult> acceptLocalMessage (@RequestPart(value = "candidateImg", required = false) MultipartFile candidateImg) {

//        public ResponseEntity <ImageResult> acceptMessage (@RequestParam(value = "nodeProc",required = false) MultipartFile nodeProc,
//                @RequestPart(value = "candidateImg", required = false) MultipartFile candidateImg) {

            NodeProc node = new NodeProc();
            ImageResult imageResult = new ImageResult();

        log.trace("Entering acceptMessage() with {}");
        JSONParser parser = new JSONParser();
//        File file;
        try {
//            Resource resourceFile = storageService.loadAsResource(nodeProc);
//            if(nodeProc != null) {
//                file = new File(nodeProc.getAbsolutePath());
//            }
//            }else {
//                file = new File(candidateImg.getOriginalFilename());
//            }
//            File file = new File("/home/leo/Public/Acu-men/Acu-Men-Alerts/AcuMenFaces/src/main/resources/nodeproc_sample.dat");


//            File file = new File(nodeProc.getOriginalFilename());
            File file = new File(candidateImg.getOriginalFilename());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(candidateImg.getBytes());
            fos.close();

            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
//            System.out.println("Json Object = " + jsonObject);

            node.setCameraID( (String) jsonObject.get("CameraId"));
            node.setTimeUTC((String) jsonObject.get("TimeUtc"));
            node.setJobID((String) jsonObject.get("JobID"));
            node.setTimeCode((Double) jsonObject.get("TimeCode"));
//            node.setFramesVisible((Integer) jsonObject.get("FramesVisible"));
            node.setPosition((String) jsonObject.get("Position"));

            List<Images> images = new ArrayList<>();
            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("Images");

            for(int i = 0; i < msg.size(); i++){
                JSONObject objarray = (JSONObject) msg.get(i);
                Images objImage = new Images();
                objImage.setQuality((Double) objarray.get("Quality"));
                objImage.setImageBase64((String) objarray.get("ImageBase64"));

                images.add((objImage));
            }
            node.setImages(images);
            List<Images> msgImage = node.getImages();

            Double bestQuality = node.getImages().get(0).getQuality();
            String bestImage = node.getImages().get(0).getImageBase64();

            for (Images image : msgImage) {
                // -1 less
                // 0 equal
                // 1 greater.
                int result = bestQuality.compareTo(image.getQuality());
                if (result == -1) {
                    bestQuality = image.getQuality();
                    bestImage = image.getImageBase64();
                }

            }
            //Remove all Line Feeds from the image.
            bestImage = bestImage.replaceAll("\n","");
//            File imageFile = new File("bestImage.jpg");
//            FileInputStream imgFos = new FileInputStream(imageFile);
//            byte[] bytes = new byte[0];
//            fos.write(bytes, 8, bytes.length - 8);
//            String personId = msApis.createPerson("Leo");
//            String persisId = msApis.addPersonFace(bestImage,personId);

            // Train the Group just before Image detection
//            msApis.getFaceList();
            msApis.trainPersonGroup();
            ImageDetect detectedImage = msApis.detectImage(bestImage);
//            ImageIdentify ident = msApis.findSimilar(detectedImage.getFaceId());
            ImageIdentify identfyImage = msApis.identifyImage(detectedImage.getFaceId());
            if(identfyImage.getPersonId() != null) {
                Person person = msApis.getPerson(identfyImage.getPersonId());
                imageResult.setName(person.getName());
                imageResult.setPersistedFaceId(person.getPersistedFaceId());
            }else{
                log.trace("Unable to Identify Candidate Image. Candidate with FaceId  {} can't be identified.", detectedImage.getFaceId());
                 return new ResponseEntity(new CustomErrorType("Unable to Identify Candidate Image. Candidate with FaceId " + detectedImage.getFaceId() + " can't be identified.")
                            , HttpStatus.NOT_FOUND);


            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = new Date();
            imageResult.setProcessedDate(formatter.format(date));
            imageResult.setAge(detectedImage.getAge());
            imageResult.setGender(detectedImage.getGender());
            imageResult.setFaceId(detectedImage.getFaceId());
            imageResult.setPersonId(identfyImage.getPersonId());
            imageResult.setConfidence(identfyImage.getConfidence());

            int code = this.saveData(node,bestImage,imageResult);
            if(code == 400){
                return new ResponseEntity(new CustomErrorType("Unable to Identify Candidate Image. Candidate with PersonId " + identfyImage.getPersonId() + " can't be identified.")
                        , HttpStatus.NOT_FOUND);

            }
//            msApis.listPersonGroups();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return ResponseEntity.ok(imageResult);
    }
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json",path = "/acceptMessage")
    @ApiOperation(value = "Process Candidate Image for the Acu-Men Global Faces")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Service not available"),
            @ApiResponse(code = 500, message = "Unexpected Runtime error") })

    public ResponseEntity <ImageResult> acceptMessage (@RequestBody LinkedHashMap candidateImg) {

//        public ResponseEntity <ImageResult> acceptMessage (@RequestParam(value = "nodeProc",required = false) MultipartFile nodeProc,
//                @RequestPart(value = "candidateImg", required = false) MultipartFile candidateImg) {

        NodeProc node = new NodeProc();
        ImageResult imageResult = new ImageResult();

        log.trace("Entering acceptMessage() with {}");
        JSONParser parser = new JSONParser();
//        File file;
//        try {
//            Resource resourceFile = storageService.loadAsResource(nodeProc);
//            if(nodeProc != null) {
//                file = new File(nodeProc.getAbsolutePath());
//            }
//            }else {
//                file = new File(candidateImg.getOriginalFilename());
//            }
//            File file = new File("/home/leo/Public/Acu-men/Acu-Men-Alerts/AcuMenFaces/src/main/resources/nodeproc_sample.dat");


//            File file = new File(nodeProc.getOriginalFilename());
//            File file = new File(String.valueOf(candidateImg));
//            file.createNewFile();
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(candidateImg.getBytes());
//            fos.close();
//
//            Object obj = parser.parse(new FileReader(file));
//            JSONObject jsonObject = (JSONObject) candidateImg;
//            System.out.println("Json Object = " + candidateImg);

            node.setCameraID( (String) candidateImg.get("CameraId"));
            node.setTimeUTC((String) candidateImg.get("TimeUtc"));
            node.setJobID((String) candidateImg.get("JobID"));
            node.setTimeCode((Double) candidateImg.get("TimeCode"));
            node.setFramesVisible((Integer) candidateImg.get("FramesVisible"));
            node.setPosition((String) candidateImg.get("Position"));


//        node.setCameraID( (String) candidateImg.getCameraID());
//            node.setTimeUTC((String) candidateImg.getTimeUTC());
//            node.setJobID((String) candidateImg.getJobID());
//            node.setTimeCode((Double) candidateImg.getTimeCode());
//            node.setFramesVisible((Long) candidateImg.getFramesVisible());
//            node.setPosition((String) candidateImg.getPosition());

            List<Images> images = new ArrayList<>();
            // loop array
//            JSONArray msg = (JSONArray) jsonObject.get("Images");

//            for(int i = 0; i < msg.size(); i++){
//                JSONObject objarray = (JSONObject) msg.get(i);
//                Images objImage = new Images();
//                objImage.setQuality((Double) objarray.get("Quality"));
//                objImage.setImageBase64((String) objarray.get("ImageBase64"));
//
//                images.add((objImage));
//            }
        node.setImages((List<Images>) candidateImg.get("Images"));
        List<Images> imgs = new ArrayList<>();
//        imgs.add((Images) node.getImages());
        for(int i = 0; i < node.getImages().size(); i++){
//                JSONObject objarray = (JSONObject) msg.get(i);
                Images objImage = new Images();
                LinkedHashMap linkedImg = node.getImages().get(i);
//                Images hashImg = node.getImages().get(i);
                objImage.setQuality((Double) linkedImg.get("Quality"));
                objImage.setImageBase64((String) linkedImg.get("ImageBase64"));

                images.add((objImage));
            }
            node.setImages(images);
            List<Images> msgImage = node.getImages();

            Double bestQuality = node.getImages().get(0).getQuality();
            String bestImage = node.getImages().get(0).getImageBase64();

            for (Images image : msgImage) {
                // -1 less
                // 0 equal
                // 1 greater.
                int result = bestQuality.compareTo(image.getQuality());
                if (result == -1) {
                    bestQuality = image.getQuality();
                    bestImage = image.getImageBase64();
                }

            }
            //Remove all Line Feeds from the image.
            bestImage = bestImage.replaceAll("\n","");
//            File imageFile = new File("bestImage.jpg");
//            FileInputStream imgFos = new FileInputStream(imageFile);
//            byte[] bytes = new byte[0];
//            fos.write(bytes, 8, bytes.length - 8);
//            String personId = msApis.createPerson("Leo");
//            String persisId = msApis.addPersonFace(bestImage,personId);

            // Train the Group just before Image detection
//            msApis.getFaceList();

            ImageDetect detectedImage = msApis.detectImage(bestImage);
            if(detectedImage.getFaceId() == null ) {
                log.trace("Unable to Identify Candidate Image. Candidate with FaceId  {} can't be identified.");
                return new ResponseEntity(new CustomErrorType("Unable to Identify Candidate Image. Candidate can't be identified." )
                        , HttpStatus.NOT_FOUND);

            }
//            ImageIdentify ident = msApis.findSimilar(detectedImage.getFaceId());
            ImageIdentify identfyImage = msApis.identifyImage(detectedImage.getFaceId());
            if(identfyImage.getPersonId() != null) {
                Person person = msApis.getPerson(identfyImage.getPersonId());
                imageResult.setName(person.getName());
                imageResult.setPersistedFaceId(person.getPersistedFaceId());
            }else{
                log.trace("Unable to Identify Candidate Image. Candidate with FaceId  {} can't be identified.", detectedImage.getFaceId());
                return new ResponseEntity(new CustomErrorType("Unable to Identify Candidate Image. Candidate with FaceId " + detectedImage.getFaceId() + " can't be identified.")
                        , HttpStatus.NOT_FOUND);


            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date date = new Date();
            imageResult.setProcessedDate(formatter.format(date));
            imageResult.setAge(detectedImage.getAge());
            imageResult.setGender(detectedImage.getGender());
            imageResult.setFaceId(detectedImage.getFaceId());
            imageResult.setPersonId(identfyImage.getPersonId());
            imageResult.setConfidence(identfyImage.getConfidence());

            int code = this.saveData(node,bestImage,imageResult);
            if(code == 400){
                return new ResponseEntity(new CustomErrorType("Unable to Identify Candidate Image. Candidate with PersonId " + identfyImage.getPersonId() + " can't be identified.")
                        , HttpStatus.NOT_FOUND);

            }
            msApis.trainPersonGroup();
//            msApis.listPersonGroups();

//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        return ResponseEntity.ok(imageResult);
    }

    public int saveData(NodeProc node,String bestImage, ImageResult result) {

        Cameras cameras = new Cameras();
        List<Cameras> cam = new ArrayList<>();
        Detection detection = new Detection();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        MsRecognition msRecognition = new MsRecognition();
        int statusCode = 0;
        try {

            cam = camerasTableRepository.findByCameraName(node.getCameraID());
            if(cam.size() <= 0 ) {
                cameras.setCameraName(node.getCameraID());
                cameras.setCameraAddress("");
                cameras.setCameraLocation("");
                cam.add(cameras);
                camerasTableRepository.saveAndFlush(cameras);
            }else {
                cameras.setCameraAddress("");
                cameras.setCameraLocation("");
                cameras.setCameraName(cam.get(0).getCameraName());
            }
            Date utc = formatter.parse(result.getProcessedDate());
            detection.setCameras(cam.get(0));
            detection.setDetectionDateTime(utc);
            detection.setDetectionImg(Base64.getDecoder().decode(bestImage));
            detectionTableRepository.saveAndFlush(detection);

            msRecognition.setPersonId(result.getPersonId());
            msRecognition.setConfidence(result.getConfidence());
            msRecognition.setDetection(detection);
            msRecognitionTableRepository.saveAndFlush(msRecognition);

            MsCandidateRelation msCandidateRelation = new MsCandidateRelation();
            msCandidateRelation = msCandidateRelationTableRepository.findByPersonId(result.getPersonId());

            Candidates candidates = new Candidates();
            byte[]imageByteArray = Base64.getDecoder().decode(bestImage);
            if(msCandidateRelation != null) {

                candidates = candidatesTableRepository.findOne(msCandidateRelation.getCandidate().getCandidateId());
                //Add the detected image to the candidates Person Face.
                String persistedFaceId = msApis.addPersonFace(bestImage,result.getPersonId());
                //Train the Group for the New Candidate face.
                msApis.trainPersonGroup();
                if ((candidates.getTitle() != null) && (!candidates.getTitle().isEmpty())) {
                    result.setTitle(candidates.getTitle());
                    statusCode = acuMenAlertApis.sendApiRequest(candidates.getTitle(), cameras.getCameraName(), imageByteArray, result.getProcessedDate());
                }
            }else{
                log.trace("Unable to Identify Candidate Image. Candidate with PersonId  {} can't be identified.", result.getPersonId());
                return 400;


            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return statusCode;
    }

}